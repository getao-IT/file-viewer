package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.common.PageResult;
import cn.aircas.airproject.entity.domain.FileSearchParam;
import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.entity.domain.ImageSearchParam;
import cn.aircas.airproject.entity.emun.SourceFileType;
import cn.aircas.airproject.service.FileTypeService;
import cn.aircas.utils.date.DateUtils;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageInfo;
import cn.aircas.utils.image.ParseImageInfo;
import cn.aircas.utils.image.slice.CreateThumbnail;
import cn.aircas.utils.image.slice.SliceGenerateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;



@Service("IMAGE-SERVICE")
@Slf4j
public class ImageFileServiceImpl implements FileTypeService {

    @Value("${sys.rootPath}")
    String rootPath;

    /**
     * 转换查询参数
     *
     * @param fileSearchParam
     * @return
     */
    private ImageSearchParam convertSearchParam(FileSearchParam fileSearchParam) {
        ImageSearchParam imageSearchParam = new ImageSearchParam();
        BeanUtils.copyProperties(fileSearchParam, imageSearchParam);
        imageSearchParam.setImageIdList(fileSearchParam.getFileIdList());
        String searchParam = fileSearchParam.getSearchParam();
        if (StringUtils.isNotBlank(searchParam)) {
            List<String> params = Arrays.asList(searchParam.split(" "));
            imageSearchParam.setSearchParamList(params);
        }

        imageSearchParam.setImageName(fileSearchParam.getFileName());
        return imageSearchParam;
    }


    /**
     * 不保留经纬度信息，单纯裁切影像得到切片图片
     * @param fileType
     * @param path
     * @param beginX
     * @param beginY
     * @param width
     * @param height
     * @return
     */
    public Boolean makeImageSlice(SourceFileType fileType, String path, int beginX, int beginY, int width, int height) {
        String filePath = FileUtils.getStringPath(this.rootPath, path);
        File file = new File(filePath);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedImage imageRead = ImageIO.read(fileInputStream);
            BufferedImage bufferedImage = new BufferedImage(width, height, imageRead.getType());
            Graphics2D graphics = bufferedImage.createGraphics();
            int dx1 = 0;
            int dy1 = 0;
            int dx2 = beginX + width;
            int dy2 = beginY + height;
            int sx1 = 1000;
            int sy1 = 1000;
            int sx2 = width;
            int sy2 = height;
            graphics.drawImage(imageRead, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
            graphics.dispose();
            String extend = file.getName().substring(file.getName().lastIndexOf("."));
            String savePath1 = FileUtils.getStringPath(file.getParentFile().getPath(), "slice1") + extend;
            ImageIO.write(bufferedImage, extend.replace(".", ""), new File(savePath1));
            System.out.println("Slice ok : " + savePath1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 保留经纬度信息，裁切影像所有位置得到切片图片
     * @param fileType
     * @param imagePath
     * @param width
     * @param height
     * @param sliceInsertPath
     * @return
     */
    public void makeImageAllGeoSlice(SourceFileType fileType, String imagePath, int width, int height, String sliceInsertPath, int step, Boolean storage) {
        List<String> slicePathList = new ArrayList<>();

        //Image image = this.getById(id);
        String filePath = FileUtils.getStringPath(this.rootPath, imagePath);
        //this.rootPath = "C:\\Users\\dell\\Desktop";
        //String filePath = "C:\\Users\\dell\\Desktop\\image\\3.tiff";
        File file = new File(filePath);
        String savePath = FileUtils.getStringPath(this.rootPath, sliceInsertPath);
        String extend = file.getName().substring(file.getName().lastIndexOf("."));
        ImageInfo image = ParseImageInfo.parseInfo(FileUtils.getStringPath(this.rootPath,imagePath));
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        /*int imageWidth = 10000;
        int imageHeight = 10000;*/
        int currentWidth = 0;
        int currentHeight = 0;
        int nextWidth = 0;
        int nextHeight = 0;
        int count = 1;
        step = (step == 0 || step < width) ? width : step;
        for (int i = 0; i < imageWidth; i+=step) {
            nextWidth = (nextWidth + width) > imageWidth ? imageWidth : (nextWidth + width);
            for (int j = 0; j < imageHeight; j+=height) {
                nextHeight = (nextHeight + height) > imageHeight ? imageHeight : (nextHeight + height);
                sliceInsertPath = FileUtils.getStringPath(savePath, FilenameUtils.removeExtension(new File(filePath).getName()))
                        + "_slice_" + count + extend;
                double[] range = new double[]{currentWidth, currentHeight, nextWidth, nextHeight};
                SliceGenerateUtil.generateSlice(range, filePath, sliceInsertPath, true);
                slicePathList.add(sliceInsertPath);
                currentHeight = nextHeight;
                // 切片入库
                /*if (storage) {
                    Image sliceImage = this.parseFileInfo(sliceInsertPath, image);
                    this.save(sliceImage);
                }*/
                sliceInsertPath = savePath;
                count++;
            }
            currentWidth = nextWidth;
            currentHeight = 0;
            nextHeight = 0;
        }
        log.info("生成切片成功，路径：{}", savePath);
    }


    /**
     * 保留经纬度信息，裁切影像得到切片图片
     * @param fileType
     * @param imagePath
     * @param minLon
     * @param minLat
     * @param width
     * @param height
     * @param sliceInsertPath
     * @return
     */
    public void makeImageGeoSlice(SourceFileType fileType, String imagePath, double minLon, double minLat, int width, int height, String sliceInsertPath, Boolean storage) {
        String filePath = FileUtils.getStringPath(this.rootPath, imagePath);
        //String filePath = "C:\\Users\\dell\\Desktop\\image\\3.tiff";
        File file = new File(filePath);
        double minX = minLon;
        double minY = minLat;
        double maxX = minLon + width;
        double maxY = minLat + height;
        String extend = file.getName().substring(file.getName().lastIndexOf("."));
        sliceInsertPath = FileUtils.getStringPath(this.rootPath, sliceInsertPath) + extend;
        //sliceInsertPath = "C:\\Users\\dell\\Desktop\\image\\3-slice1.tiff";
        double[] range = new double[]{minX, minY, maxX, maxY};
        SliceGenerateUtil.generateSlice(range, filePath, sliceInsertPath, true);

        // 切片入库
        /*if (storage) {
            Image sliceImage = this.parseFileInfo(sliceInsertPath, image);
            this.save(sliceImage);
        }*/
        log.info("生成切片成功，路径：{}, ", sliceInsertPath);
    }


    private Image parseFileInfo(String filePath, Image sourceImage) {
        File imageFile = new File(filePath);

        String thumbnailPath = FileUtils.getStringPath(imageFile.getParentFile().getAbsolutePath(),
                "thumbnail_" + FilenameUtils.removeExtension(imageFile.getName()) + ".jpg");
        String fileSize = FileUtils.fileSizeToString(imageFile.length());
        String thumbnail = CreateThumbnail.createBase64Thumbnail(filePath, thumbnailPath, 256);

        String relativeFilePath = filePath.substring(this.rootPath.length());
        if (relativeFilePath.startsWith("/"))
            relativeFilePath = relativeFilePath.substring(1);
        ImageInfo imageInfo = ParseImageInfo.parseInfo(filePath);
        Image image = null;
        try {
            image = Image.builder().imageName(imageFile.getName()).createTime(DateUtils.nowDate()).path(relativeFilePath)
                    .thumb(thumbnail).size(fileSize).fileLength(imageFile.length()).minProjectionX(imageInfo.getProjectionRange()[0])
                    .minProjectionY(imageInfo.getProjectionRange()[1]).maxProjectionX(imageInfo.getProjectionRange()[2])
                    .maxProjectionY(imageInfo.getProjectionRange()[3]).isPublic(sourceImage.isPublic()).userId(sourceImage.getUserId())
                    .userName(sourceImage.getUserName()).keywords(sourceImage.getKeywords()).delete(false).build();
        } catch (Exception e) {
            log.error("文件 {} 影像信息解析失败");
            return null;
        }
        BeanUtils.copyProperties(imageInfo,image);

        return image;
    }


    public static void main(String[] args) {
        /*String dateTime="2020-01-13T16:00:00.000Z";
        dateTime=dateTime.replace("Z","UTC");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date parse = format.parse(dateTime);
            //Date time = format.parse(dateTime);
            String result = dateFormat.format(parse);
            System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }*/

        ImageFileServiceImpl service = new ImageFileServiceImpl();
        String path = "image\\3.tiff";
        service.makeImageGeoSlice(SourceFileType.IMAGE, path, 500, 500, 2000, 2000, "", true);
        //service.makeImageAllGeoSlice(FileType.IMAGE, 1, 2000, 2000, "image\\slice\\", 2000);
        System.out.println("后台处理中。。。");
    }

}

