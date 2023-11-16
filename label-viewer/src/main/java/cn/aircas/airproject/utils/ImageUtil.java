package cn.aircas.airproject.utils;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.callback.impl.GdalConverProgressCallback;
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
import java.io.*;
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

                Dataset dataset = hDriver.CreateCopy(path, ds,1,null, new GdalConverProgressCallback(progress));
                ds.FlushCache();
                ds.delete();
                hDriver.delete();
                dataset.FlushCache();
                dataset.delete();
                log.info("格式转换成功");
                return path;
            }

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }


    /**
     * 构建金字塔
     * @param imagePath
     */
    public static void buildOverviews(String imagePath, ProgressContr progress) {
        gdal.AllRegister();
        gdal.SetConfigOption("GDAL_PAM_ENABLED", "FALSE");
        Dataset dataset = gdal.Open(imagePath);
        dataset.BuildOverviews(new int[]{2, 4, 8});
        dataset.delete();
    }
}
