package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.ProgressCallback;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 影像处理工具类
 */
@Slf4j
public class ImageUtil {


    /**
     * key: 任务ID，即浏览器ID
     * value: 该任务下的所有任务进度信息集合
     */
    public static final Map<String, ProgressContr> progressMaps = new ConcurrentHashMap<>();
    public static final Map<String, List<ProgressContr>> progresss = new ConcurrentHashMap<>();


    /**
     * 灰度转换
     * @param images
     */
    public static void normalization(File images, File destImages) {
        BufferedImage image = null;
        File file = null;
        try {
            String absolutePath = destImages.getAbsolutePath();
            file = new File(absolutePath);
            image = ImageIO.read(file);

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
                }
            }
            ImageIO.write(image , absolutePath.substring(absolutePath.lastIndexOf(".") + 1) , file);
        }catch (IOException e) {
            log.error("归一化影像读取失败 ：{}" , e.getMessage());
        }
    }


    /**
     * 影像格式转换
     * @param inputPath
     * @param outputPath
     * @param format
     * @return
     */
    public static String formatConvertor(String progressId, String inputPath, String outputPath, String format) {
        gdal.AllRegister();
        gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8", "YES");
        Dataset ds = gdal.Open(inputPath, gdalconstConstants.GA_ReadOnly);
        if (ds == null) {
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

            String path = FileUtils.getStringPath(outputPath, new Object[]{baseName}) + "." + extension;
            if (new File(path).exists()) {
                path = cn.aircas.airproject.utils.FileUtils.autoMakeIfFileRepeat(new File(path)).getAbsolutePath();
            }
            Dataset dataset = hDriver.CreateCopy(path, ds,0,null, new ProgressCallback(){
                @Override
                public int run(double dfComplete, String pszMessage) {
                    ProgressContr progress = ProgressContr.builder().taskId(progressId).filePath(inputPath)
                            .progress(new DecimalFormat("##.##").format(dfComplete * 100)).build();
                    progressMaps.put(progressId, progress);
                    System.out.println("进度：" + new DecimalFormat("##.##").format(dfComplete*100));
                    return super.run(dfComplete, pszMessage);
                }
            });
            ds.FlushCache();
            ds.delete();
            hDriver.delete();
            dataset.FlushCache();
            dataset.delete();
            log.info("格式转换成功");
            return path;
        }
    }


    public static void main(String[] args) {
        System.out.println(ImageUtil.progressMaps.get("111"));
    }
}
