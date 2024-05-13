package cn.aircas.airproject.utils;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.callback.impl.GdalConverProgressCallback;
import cn.aircas.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.*;
import org.gdal.gdalconst.gdalconst;
import org.gdal.gdalconst.gdalconstConstants;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 影像处理工具类
 */
@Slf4j
public class ImageUtil {


    /*static {
        gdal.AllRegister();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }*/


    /**
     * key: 任务ID，即浏览器ID
     * value: 该任务下的所有任务进度信息集合
     */
    public static final Map<String, ProgressContr> progressMaps = new ConcurrentHashMap<>();
    public static final Map<String, List<ProgressContr>> progresss = new ConcurrentHashMap<>();


    /**
     * 灰度转换
     * @param src
     * @param callback
     */
    public static BufferedImage grayConver(File src, GrayConverCallback callback) {
        BufferedImage image = null;
        File file = null;
        try {
            String absolutePath = src.getAbsolutePath();
            file = new File(absolutePath);
            image = ImageIO.read(file);
            double complete = 0;
            for (int j = 0 ; j < image.getHeight() ; j++) {
                for (int i = 0 ; i < image.getWidth() ; i ++ ) {
                    int p = image.getRGB(i , j);
                    int a = (p >> 24) & 0xff;
                    int r = (p >> 16) & 0xff;
                    int g = (p >> 8) & 0xff;
                    int b = p & 0xff;
                    int avg = (r + g + b) / 3;

                    p = (a << 24) | (avg << 16) | (avg << 8) | avg;

                    image.setRGB(i ,j , p);
                    callback.run(complete++/(image.getHeight() * image.getWidth()));
                }
            }
        }catch (IOException e) {
            log.error("灰度转换影像读取失败 ：{}" , e.getMessage());
        }
        return image;
    }


    /**
     * 灰度图片写入
     * @param src
     * @param format
     * @param dest
     * @throws IOException
     */
    public static void grayImageWrite(BufferedImage src, String format, File dest) throws IOException {
        ImageIO.write(src , format , dest);
    }


    /**
     * OpenCV灰度转换
     * @param src
     * @param callback
     */
    public static String opencvGrayConver(String src, String dst, OpenCV.NormalizeType type, GrayConverCallback callback) {
        gdal.AllRegister();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Dataset dataset = gdal.Open(src);
        String projection = dataset.GetProjection();
        double[] geoTransform = dataset.GetGeoTransform();

        try {
            Mat matSrc = Imgcodecs.imread(src);
            //转换成灰度图
            Mat matGray = new Mat(matSrc.size(), CvType.CV_8UC1);
            Imgproc.cvtColor(matSrc, matGray, Imgproc.COLOR_BGR2GRAY);
            //转换成浮点类型矩阵
            Mat matFloatGray = new Mat(matGray.size(), CvType.CV_32F);
            matGray.convertTo(matFloatGray, CvType.CV_32F);
            //归一化处理
            Mat result = Mat.zeros(matFloatGray.size(), CvType.CV_32FC1);
            Core.normalize(matFloatGray, result, 0, 1, type.getCode());

            switch (type) {
                //MINMAX 按 alpha 和 beta 进行缩放限制，得到的结果为 0 或 1，需要乘以 255 恢复为灰度图数据
                case MINMAX:
                    //NORM_INF，无穷范数，每个值除以最大值来进行无穷范数归一化，这里限制了最大值为 1，同样需要乘以 255
                case INF:
                    Core.multiply(result, new Scalar(255), result);
                    break;
                //1 范数，每个值除以它们的和来进行归一化，生成的最大值 < 1。这里随意取了一个值，保证输出为灰度图数据即可。
                case L1:
                    Core.multiply(result, new Scalar(20000000), result);
                    break;
                //2 范数，每个值除以该向量的模长，归一化为单位向量。与 L1 类似，需要乘以一个值保证输出。
                case L2:
                    Core.multiply(result, new Scalar(30000), result);
                    break;
                default:
            }

            if (new File(dst).exists()) {
                dst = cn.aircas.airproject.utils.FileUtils.autoMakeIfFileRepeat(new File(dst)).getAbsolutePath();
            }
            result.convertTo(result, CvType.CV_8UC1);
            Imgcodecs.imwrite(dst, result);

            if (StringUtils.isNotBlank(projection)){
                dataset = gdal.Open(dst, gdalconst.GA_Update);
                dataset.SetProjection(projection);
                dataset.SetGeoTransform(geoTransform);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "灰度转换失败或不支持的灰度转换类型";
        }

        return "灰度转换成功";
    }


    /**
     * 影像格式转换
     * @param inputPath
     * @param outputPath
     * @param format
     * @return
     */
    public static String formatConvertor(String inputPath, String outputPath, String format, ProgressContr progress) {


        try {
            gdal.AllRegister();
            gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
            Dataset srcDataset = gdal.Open(inputPath, gdalconstConstants.GA_ReadOnly);
            if (srcDataset == null) {
                log.error("GDALOpen failed-" + gdal.VSIGetLastErrorNo());
                log.error(gdal.GetLastErrorMsg());
                return null;
            } else {
                File inputFile = new File(inputPath);
                Driver hDriver = gdal.GetDriverByName(format);
                log.info("Driver:" + hDriver.getShortName() + "/" + hDriver.getLongName());
                String baseName = FilenameUtils.getBaseName(inputFile.getName());

                String extension;
                if (format.equalsIgnoreCase("JPEG")) {
                    extension = "jpg";
                } else if (format.equalsIgnoreCase("GTIFF")) {
                    extension = "tif";
                } else {
                    extension = format.toLowerCase();
                }

                String dstPath = FileUtils.getStringPath(outputPath, new Object[]{baseName}) + "." + extension;
                progress.setOutputPath(dstPath.replace("/home/data", ""));
                if (new File(dstPath).exists()) {
                    dstPath = cn.aircas.airproject.utils.FileUtils.autoMakeIfFileRepeat(new File(dstPath)).getAbsolutePath();
                }

                int bandCount = srcDataset.getRasterCount();
                List<Integer> bandmapping = null;
                if(bandCount == 1){
                    ;
                } else if(bandCount == 3){
                    bandmapping = new ArrayList<>(Arrays.asList(new Integer[]{1,2,3}));
                } else if (bandCount == 4){
                    bandmapping = new ArrayList<>(Arrays.asList(new Integer[]{1,2,3}));
                } else {
                    int partitionBoard = bandCount/3;
                    bandmapping = new ArrayList<>(Arrays.asList(new Integer[]{partitionBoard,2*partitionBoard,3*partitionBoard}));
                }
                TranslateOptions translateOptions = getTranslateOptions(srcDataset, bandmapping, format);
                gdal.Translate(dstPath, srcDataset, translateOptions, new GdalConverProgressCallback(progress));

                srcDataset.FlushCache();
                srcDataset.delete();
                log.info("格式转换成功");
                return "格式转换成功";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "当前格式不支持的转换为"+format+"类型";
        }

    }

    /**
     * 根据设定的波段映射关系，将源图像转换为指定类型的图像
     * @param srcDataset 原图像的Dataset对象，不能为null
     * @param bandMapping 图像格式转换时使用的波段映射列表，(100,1,80)表示源图像100波段映射为新图像第1个波段，依次类推；单波段图像，该值为null
     * @param transExtension 要转换的图像后缀名在gdal中的格式字符串
     * @return 根据输入参数情况生成的供gdal.translate使用的转换选项对象
     */
    private static TranslateOptions getTranslateOptions(Dataset srcDataset, List<Integer> bandMapping, String transExtension){
        if(srcDataset == null){
            return null;
        }
        if(bandMapping == null){
            bandMapping = new LinkedList<>();
            bandMapping.add(1);
        }

        int bandCount = bandMapping.size();
        StringBuilder cmdSb = new StringBuilder();
        cmdSb.append("-ot Byte -of ");
        cmdSb.append(transExtension);
        cmdSb.append(" ");
        double[] minmax = new double[2];
        for(int i = 0; i < bandCount; ++i){
            Band band = srcDataset.GetRasterBand(bandMapping.get(i));
            band.ComputeRasterMinMax(minmax);

            cmdSb.append(" -b ");
            cmdSb.append(bandMapping.get(i));
            cmdSb.append(" -scale_");
            cmdSb.append(i+1);
            cmdSb.append(" ");
            cmdSb.append(minmax[0]);
            cmdSb.append(" ");
            cmdSb.append(minmax[1]);
            cmdSb.append(" 0 255");
        }
        return new TranslateOptions(gdal.ParseCommandLine(cmdSb.toString()));
    }



    /**
     * 构建金字塔
     * @param imagePath
     */
    public static void buildOverviews(String imagePath, ProgressContr progress) {
        gdal.AllRegister();
        gdal.SetConfigOption("GDAL_PAM_ENABLED", "FALSE");
        Dataset dataset = gdal.Open(imagePath);
        dataset.BuildOverviews(new int[]{2, 4, 8}, new GdalConverProgressCallback(progress));
        dataset.delete();
    }


    /**
     * 灰度转换
     * @param filePath
     */
    public static void grayConver(String filePath) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat srcImage = Imgcodecs.imread(filePath);
        Mat dstImage = new Mat();
        Imgproc.cvtColor(srcImage, dstImage, Imgproc.COLOR_BGR2GRAY,0);
        String destPath = filePath + "_gray." + FilenameUtils.getExtension(new File(filePath).getName());
        Imgcodecs.imwrite(destPath, dstImage);
        log.info("转换灰度成功：{}", destPath);
    }


    public static void main(String[] args) {
        String src = "C:\\Users\\Administrator\\Desktop\\temp\\P_GZ_test13_2013_1126_Level_18.tif";
        String dst = "C:\\Users\\Administrator\\Desktop\\temp\\target\\gray\\P_GZ_test13_2013_1126_Level_18.tif";
        ImageUtil.opencvGrayConver(src, dst, OpenCV.NormalizeType.MINMAX, null);
        System.out.println("888");
    }
}
