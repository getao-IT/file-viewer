package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.common.PageResult;
import cn.aircas.airproject.entity.domain.FileSearchParam;
import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.entity.domain.ImageSearchParam;
import cn.aircas.airproject.entity.domain.Slice;
import cn.aircas.airproject.entity.emun.SourceFileType;
import cn.aircas.airproject.service.FileTypeService;
import cn.aircas.airproject.utils.ImageSliceUtils;
import cn.aircas.airproject.utils.XMLUtils;
import cn.aircas.utils.date.DateUtils;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageInfo;
import cn.aircas.utils.image.ParseImageInfo;
import cn.aircas.utils.image.geo.GeoUtils;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
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
    public void makeImageAllGeoSlice(SourceFileType fileType, String imagePath, int width, int height,
                                     String sliceInsertPath, int step, Boolean storage, Boolean retainBlankSlice,
                                     Boolean takeLabelXml, String coordType, GrayConverCallback callback) {
        List<String> slicePathList = new ArrayList<>();

        //Image image = this.getById(id);
        String filePath = FileUtils.getStringPath(this.rootPath, imagePath);

        String xmlPath = filePath.replace(FilenameUtils.getExtension(filePath), "xml");
        XMLLabelObjectInfo xmlLabelObjectInfo = null;
        if (new File(xmlPath).exists()) {
            xmlLabelObjectInfo = XMLUtils.parseXMLFromFile(XMLLabelObjectInfo.class, xmlPath);
        }

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
        int totalSlice = this.totalSliceByStep(imageWidth, imageHeight, width, height, step);
        step = (step == 0 || step < width) ? width : step;
        for (int i = 0; i < imageWidth; i+=step) {
            nextWidth = (nextWidth + width) > imageWidth ? imageWidth : (nextWidth + width);
            for (int j = 0; j < imageHeight; j+=height) {
                nextHeight = (nextHeight + height) > imageHeight ? imageHeight : (nextHeight + height);
                sliceInsertPath = FileUtils.getStringPath(savePath, FilenameUtils.removeExtension(new File(filePath).getName()))
                        + "_slice_" + count + extend;
                double[] range = new double[]{currentWidth, currentHeight, nextWidth, nextHeight};

                // 是否生成XML
                if (takeLabelXml) {
                    if (xmlLabelObjectInfo != null) {
                        // 是否保留空白切片
                        double[] newRange = range;
                        if (coordType.equalsIgnoreCase("GEOGCS")) {
                            double[] newRang1 = GeoUtils.pixel2Coordinate(newRange[0], newRange[1], filePath, GeoUtils.COORDINATE_LONLAT);
                            double[] newRang2 = GeoUtils.pixel2Coordinate(newRange[2], newRange[3], filePath, GeoUtils.COORDINATE_LONLAT);
                            newRange = new double[]{newRang1[0], newRang1[1], newRang2[0], newRang2[1]};
                        }
                        boolean blankSlece = ImageSliceUtils.isBlankSlece(xmlLabelObjectInfo, newRange[0], newRange[2], newRange[1], newRange[3]);
                        if (blankSlece && !retainBlankSlice) {
                            continue;
                        }
                        newRange = ImageSliceUtils.getBoundaryFromArr(newRange);
                        double[] labelRange = new double[]{newRange[0], newRange[1], newRange[2], newRange[3]};
                        takeSliceXml(filePath, sliceInsertPath, labelRange);
                    } else if (!retainBlankSlice) {
                        return;
                    }
                }
                SliceGenerateUtil.generateSlice(range, filePath, sliceInsertPath, true);
                slicePathList.add(sliceInsertPath);
                currentHeight = nextHeight;
                // 切片入库
                /*if (storage) {
                    Image sliceImage = this.parseFileInfo(sliceInsertPath, image);
                    this.save(sliceImage);
                }*/
                sliceInsertPath = savePath;
                callback.run(count++/(double)totalSlice);
            }
            currentWidth = nextWidth;
            currentHeight = 0;
            nextHeight = 0;
        }
        log.info("生成切片成功，路径：{}", savePath);
    }


    /**
     * 获取某范围能裁切指定大小的切片总数
     * @param imgWidth
     * @param imgHeight
     * @param sliceWidth
     * @param sliceHeight
     * @param step
     * @return
     */
    public int totalSliceByStep(int imgWidth, int imgHeight, int sliceWidth, int sliceHeight, int step) {
        sliceWidth = sliceWidth + step;
        int row = (imgWidth % sliceWidth) == 0 ? (imgWidth / sliceWidth) : (imgWidth / sliceWidth) + 1;
        int col = (imgHeight % sliceHeight) == 0 ? (imgHeight / sliceHeight) : (imgHeight / sliceHeight) + 1;
        return row * col;
    }


    /**
     * 保留经纬度信息，裁切影像得到切片图片
     * @param slice 切片存储信息
     * @return
     */
    public void makeImageGeoSlice(Slice slice) {
        String filePath = FileUtils.getStringPath(this.rootPath, slice.getImagePath());
        //String filePath = "C:\\Users\\dell\\Desktop\\image\\3.tiff";
        File file = new File(filePath);
        double minX = slice.getMinLon();
        double minY = slice.getMinLat();
        double maxX = slice.getMinLon() + slice.getWidth();
        double maxY = slice.getMinLat() + slice.getHeight();
        String extend = file.getName().substring(file.getName().lastIndexOf("."));
        String sliceInsertPath = FileUtils.getStringPath(this.rootPath, slice.getSliceInsertPath()) + extend;
        //sliceInsertPath = "C:\\Users\\dell\\Desktop\\image\\3-slice1.tiff";
        double[] range = new double[]{minX, minY, maxX, maxY};

        // 是否生成XML
        if (slice.getTakeLabelXml()) {
            // 是否保留空白切片
            String xmlPath = filePath.replace(FilenameUtils.getExtension(filePath), "xml");
            if (new File(xmlPath).exists()) {
                XMLLabelObjectInfo xmlLabelObjectInfo = XMLUtils.parseXMLFromFile(XMLLabelObjectInfo.class, xmlPath);
                double[] newRang = range;
                if (slice.getCoordinateType().equalsIgnoreCase("GEODEGREE")) {
                    double[] newRang1 = GeoUtils.pixel2Coordinate(range[0], range[1], filePath, GeoUtils.COORDINATE_LONLAT);
                    double[] newRang2 = GeoUtils.pixel2Coordinate(range[2], range[3], filePath, GeoUtils.COORDINATE_LONLAT);
                    newRang = new double[]{newRang1[0],newRang1[1],newRang2[0],newRang2[1]};
                }
                boolean blankSlece = ImageSliceUtils.isBlankSlece(xmlLabelObjectInfo, newRang[0], newRang[2], newRang[1], newRang[3]);
                if (blankSlece && !slice.getRetainBlankSlice()) {
                    return;
                }
                takeSliceXml(filePath, sliceInsertPath, newRang);
            } else if (!slice.getRetainBlankSlice()) {
                return;
            }
        }

        // 生成切片
        SliceGenerateUtil.generateSlice(range, filePath, sliceInsertPath, true);

        // 切片入库
        /*if (storage) {
            Image sliceImage = this.parseFileInfo(sliceInsertPath, image);
            this.save(sliceImage);
        }*/
        log.info("生成切片成功，路径：{}, ", sliceInsertPath);
    }


    /**
     * 生成单张切片对应XML
     * @param filePath
     * @param slicePath
     * @param range
     */
    public void takeSliceXml(String filePath, String slicePath, double[] range) {
        String xmlPath = filePath.replace(FilenameUtils.getExtension(filePath), "xml");
        XMLLabelObjectInfo xmlLabelObjectInfo = XMLUtils.parseXMLFromFile(XMLLabelObjectInfo.class, xmlPath);
        List<XMLLabelObjectInfo.XMLLabelObject> xmlLabelObjectList = xmlLabelObjectInfo.getXMLLabelObjectList();
        List<XMLLabelObjectInfo.XMLLabelObject> sliceXmlLabelObject = new ArrayList<>();
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        /*double[] newRang = range;
        if (xmlLabelObjectInfo.getCoordinate().equalsIgnoreCase("GEODEGREE")) {
            double[] newRang1 = GeoUtils.pixel2Coordinate(range[0], range[1], filePath, GeoUtils.COORDINATE_LONLAT);
            double[] newRang2 = GeoUtils.pixel2Coordinate(range[2], range[3], filePath, GeoUtils.COORDINATE_LONLAT);
            newRang = new double[]{newRang1[0],newRang1[1],newRang2[0],newRang2[1]};
        }*/
        ImageSliceUtils.getBoundaryFromArr(range);
        Polygon slicePolygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(range[0], range[1]), new Coordinate(range[0], range[3]), new Coordinate(range[2], range[3]), new Coordinate(range[2], range[1]), new Coordinate(range[0], range[1])
        });
        for (int i = 0; i < xmlLabelObjectList.size(); i++) {
            XMLLabelObjectInfo.XMLLabelObject xmlLabelObject = xmlLabelObjectList.get(i);
            List<String> point = xmlLabelObject.getPoints().getPoint();
            /*if (xmlLabelObject.getCoordinate().equalsIgnoreCase("geodegree"))
                point = ImageSliceUtils.convertPixelToLonlatFromPoints(filePath, point, GeoUtils.COORDINATE_LONLAT);
            if (xmlLabelObject.getCoordinate().equalsIgnoreCase("projection"))
                point = ImageSliceUtils.convertPixelToLonlatFromPoints(filePath, point, GeoUtils.COORDINATE_PROJECTION);*/
            //double[] boundary = ImageSliceUtils.getBoundaryFromPoints(point);
            List<double[]> doubPoint = ImageSliceUtils.stringPointsToDouble(point);
            boolean inSlice = ImageSliceUtils.isInSlice(doubPoint, slicePolygon, geometryFactory);
            if (!inSlice) {
                continue;
            }

            Coordinate[] labelCoord = new Coordinate[doubPoint.size()+1];
            for (int i1 = 0; i1 < doubPoint.size(); i1++) {
                labelCoord[i1] = new Coordinate(doubPoint.get(i1)[0], doubPoint.get(i1)[1]);
            }
            labelCoord[labelCoord.length-1] = labelCoord[0]; // g构建集合对象需要形成封闭的线
            Polygon labelPolygon = geometryFactory.createPolygon(labelCoord);
            Coordinate[] coordinates = ImageSliceUtils.getIntersections(slicePolygon, labelPolygon);
            if (coordinates != null) { // 更新与切片相交的标注的交点坐标
                point.clear();
                for (int i1 = 0; i1 < coordinates.length - 1; i1++) {
                    if (xmlLabelObject.getCoordinate().equalsIgnoreCase("pixel")) {
                        point.add(ImageSliceUtils.excursionCoordIfPixel(coordinates[i1], range));
                    } else {
                        point.add(ImageSliceUtils.coordinateToPoint(coordinates[i1]));
                    }
                    //point.add(ImageSliceUtils.coordinateToPoint(coordinates[i1]));
                }
            }
            sliceXmlLabelObject.add(xmlLabelObject);
        }
        xmlLabelObjectInfo.setXMLLabelObjectList(sliceXmlLabelObject);
        String saveXmlPath = slicePath.replace(FilenameUtils.getExtension(slicePath), "xml");
        XMLUtils.toXMLFile(saveXmlPath, xmlLabelObjectInfo);
        log.info("生成切片 {} 并保存XML文件 {} 成功 ", slicePath, saveXmlPath);
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

        /*ImageFileServiceImpl service = new ImageFileServiceImpl();
        String path = "image\\3.tiff";
        service.makeImageGeoSlice(SourceFileType.IMAGE, path, 500, 500, 2000, 2000, "", true);
        //service.makeImageAllGeoSlice(FileType.IMAGE, 1, 2000, 2000, "image\\slice\\", 2000);
        System.out.println("后台处理中。。。");*/
        /*String filePath = "C:\\Users\\dell\\Downloads\\val_13.xml";
        Slice slice = new Slice();
        slice.setTakeLabelXml(true);
        double minX = 170.0;
        double minY = 212.0;
        double maxX = 624.0;
        double maxY = 628.0;
        ImageFileServiceImpl service = new ImageFileServiceImpl();
        service.takeSliceXml(filePath, filePath, minX, maxX, minY, maxY,0,0);*/
    }

}

