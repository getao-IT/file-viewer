package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.VifLabelOjectInfo;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.CoordinateConvertType;
import cn.aircas.airproject.entity.emun.CoordinateSystemType;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.utils.FileUtils;
import cn.aircas.airproject.utils.LabelPointTypeConvertor;
import cn.aircas.airproject.utils.ParseImageInfo;
import cn.aircas.airproject.utils.XMLUtils;
import cn.aircas.utils.image.geo.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LabelProjectServiceImpl implements LabelProjectService {

    @Value( value = "${sys.rootDir}")
    private String rootDir;

    /**
     * 判断影像文件是否包含金字塔
     * @param imagePath
     * @return
     */
    @Override
    public boolean hasOverview(String imagePath) {
        File imageFile = FileUtils.getFile(this.rootDir,imagePath);
        if (!imageFile.exists()){
            log.error("文件：{} 不存在",imagePath);
            return true;
        }

        return cn.aircas.utils.image.ParseImageInfo.hasOverview(imageFile.getAbsolutePath());
    }

    /**
     * 对影像创建金字塔
     * @param imagePath
     */
    @Override
    public void buildOverviews(String imagePath) {
        File imageFile = FileUtils.getFile(this.rootDir,imagePath);
        if (!imageFile.exists()){
            log.error("文件：{} 不存在",imagePath);
            return ;
        }

        cn.aircas.utils.image.ParseImageInfo.buildOverviews(imageFile.getAbsolutePath());
    }

    @Override
    public String uploadFile(String imagePath , LabelPointType labelPointType ,MultipartFile file) throws Exception {
        File tempFileDir = FileUtils.getFile(this.rootDir,"/temp");
        if (!tempFileDir.exists())
            tempFileDir.mkdirs();
        File labelFile = FileUtils.getFile(this.rootDir,"/temp",file.getOriginalFilename());
        file.transferTo(labelFile);

        String label =  viewXmlFile(imagePath,labelPointType,"/temp/"+file.getOriginalFilename());
        labelFile.delete();
        return label;
    }



    @Override
    public String viewXmlFile(String imagePath , LabelPointType labelPointType , String labelFilePath) throws Exception {
        String labelPath = FileUtils.getStringPath(this.rootDir,labelFilePath);
        String imageFilePath = FileUtils.getStringPath(this.rootDir,imagePath);
        Class clazz = labelPath.endsWith("xml") ? XMLLabelObjectInfo.class : VifLabelOjectInfo.class;
        LabelObject labelObject = (LabelObject) XMLUtils.parseXMLFromFile(clazz,labelPath);
        String coordinate = labelObject.getCoordinate();
        CoordinateConvertType coordinateConvertType = CoordinateConvertType.NO_ACTION;
        //如果标注点类型与图像坐标系不同
        if (!labelPointType.name().equalsIgnoreCase(coordinate)){
            //如果图像坐标为经纬度，标注坐标为像素，则将像素转为经纬度
            if (labelPointType == LabelPointType.GEODEGREE){
                coordinateConvertType = GeoUtils.isProjection(imageFilePath) ? CoordinateConvertType.PIXEL_TO_PROJECTION : CoordinateConvertType.PIXEL_TO_LONLAT;
            }else
                //如果图像坐标为像素，标注坐标为经纬度，则将经纬度转为像素（不可能出现）
                coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;
        }else{
            //如果图像坐标为投影坐标，标注坐标为经纬度，则将经纬度转为投影坐标
            if (LabelPointType.GEODEGREE ==labelPointType && GeoUtils.isProjection(imageFilePath))
                coordinateConvertType = CoordinateConvertType.LONLAT_TO_PROJECTION;
            //如果图像坐标为像素，标注坐标为像素，则将像素进行翻转
            if (LabelPointType.PIXEL == labelPointType)
                coordinateConvertType = CoordinateConvertType.PIXEL_REVERSION;
        }
        LabelPointTypeConvertor.convertLabelPointType(imageFilePath,labelObject,coordinateConvertType);
        return labelObject.toJSONObject().toString();
    }


    @Override
    public List<FileAndFolder> getFileAndFolderList(String path) {
        String relativePath = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        File[] files = new File(path).listFiles();
        List<FileAndFolder> fileAndFolderList = new ArrayList<>();

        if (files == null || files.length == 0){
            log.error("路径：{} 不存在",path);
            return new ArrayList<>();
        }
        for (File file : files){
            if (file.isFile()||file.isDirectory()){
                FileAndFolder fileAndFolder = new FileAndFolder();
                fileAndFolder.setName(file.getName());
                fileAndFolder.setIsFile(file.isFile());
                fileAndFolder.setSize(cn.aircas.utils.file.FileUtils.fileSizeToString(file.length()));
                fileAndFolder.setPath(relativePath);
                fileAndFolder.setLastModified(new Date(file.lastModified()));
                if (file.isFile()){
                    fileAndFolder.setIsFile(true);
                    fileAndFolder.setExtension(file.getName().substring(file.getName().lastIndexOf(".")+1));
                }
                else {
                    fileAndFolder.setIsFile(false);
                    fileAndFolder.setExtension("文件夹");
                }
                fileAndFolderList.add(fileAndFolder);
            }
        }
//        List<FileAndFolder> fileAndFolders = fileAndFolderList.stream().sorted((fileName1,fileName2)->{
//            Pattern pattern = Pattern.compile("\\d+");
//            Matcher matcher1 = pattern.matcher(fileName1.getName());
//            Matcher matcher2 = pattern.matcher(fileName2.getName());
//            if (matcher1.find() && matcher2.find()){
//                int num1 = Integer.parseInt(matcher1.group());
//                int num2 = Integer.parseInt(matcher2.group());
//                return num1 - num2;
//            }else{
//                return fileName1.getName().compareTo(fileName2.getName());
//            }
//        }).collect(Collectors.toList());
        List<FileAndFolder> fileAndFolders = fileAndFolderList.stream().sorted((fileName1,fileName2)->
            fileName1.getName().compareTo(fileName2.getName())).collect(Collectors.toList());
        return fileAndFolders;
    }

    @Override
    public List<FolderPac> getFolderList(String path) {
        path = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        File[] files = new File(path).listFiles();

        if (files == null){
            log.error("路径：{} 不存在",path);
            return new ArrayList<>();
        }
        List<FolderPac> folderList = new ArrayList<>();
        for (File file : files){
            if (file.isDirectory()){
                FolderPac folderPac = new FolderPac();
                folderPac.setName(file.getName());
                folderPac.setLastModified(new Date(file.lastModified()));
                folderList.add(folderPac);
            }
        }
        return folderList;
    }


    @Override
    public List<FilePac> getFileList(String path) throws Exception {
        System.out.println(System.currentTimeMillis());
        String finalPath = path;
        path = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        List<FilePac> filePacList = new ArrayList<>();

        File[] files = new File(path).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isFile()&&(file.getName().endsWith("jpg") || file.getName().endsWith("tif") || file.getName().endsWith("tiff") || file.getName().endsWith("png")))
                {
                    FilePac filePac = new FilePac();
                    filePac.setName(file.getName());
                    filePac.setPath(finalPath);
                    filePac.setLastModified(new Date(file.lastModified()));
                    filePac.setExtension(file.getName().split(".")[1]);
                    filePacList.add(filePac);
                    return true;
                }

                return false;
            }
        });
        if (files == null) {
            log.error("路径：{} 不存在", path);
            return new ArrayList<>();
        }
        List<FilePac> fileList = filePacList.stream().sorted((e1,e2)->e1.getName().compareTo(e2.getName())).collect(Collectors.toList());
        System.out.println(System.currentTimeMillis());
        return fileList;
    }

    @Override
    public ImageInfo getImageInfo(String path) {
        path = FileUtils.getStringPath(rootDir,path);
        return ParseImageInfo.parseInfo(path);
    }

    @Override
    public String updateXml(MultipartFile multipartFile) throws Exception {
        return null;
    }


    @Override
    public void saveLabel(SaveLabelRequest saveLabelRequest) {
        String label = saveLabelRequest.getLabel();
        LabelPointType labelPointType = saveLabelRequest.getLabelPointType();
        String labelFileSavePath = rootDir + File.separator + saveLabelRequest.getSavePath();
        String imagePath = FileUtils.getStringPath(this.rootDir,saveLabelRequest.getImagePath());

        com.alibaba.fastjson.JSONObject jsonLabel = com.alibaba.fastjson.JSONObject.parseObject(label);
        LabelObject labelObject = com.alibaba.fastjson.JSONObject.toJavaObject(jsonLabel,XMLLabelObjectInfo.class);
        String coordinate = labelObject.getCoordinate();
        ImageInfo imageInfo = ParseImageInfo.parseInfo(imagePath);
        CoordinateSystemType coordinateSystemType = imageInfo.getCoordinateSystemType();

        CoordinateConvertType coordinateConvertType = CoordinateConvertType.NO_ACTION;

        /**
         * 如果图像坐标是投影坐标
         * 目标坐标为经纬度：投影->经纬度
         * 目标坐标为像素：投影->像素
         */
        if (coordinateSystemType == CoordinateSystemType.PROJCS){
            if (labelPointType == LabelPointType.GEODEGREE)
                coordinateConvertType = CoordinateConvertType.PROJECTION_TO_LONLAT;
            else
                coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;
        }

        //如果图像坐标系为像素坐标：像素坐标翻转
        if (coordinateSystemType == CoordinateSystemType.PIXELCS)
            coordinateConvertType = CoordinateConvertType.PIXEL_REVERSION;

        /**
         * 如果图像坐标是经纬度坐标
         * 目标坐标为经纬度：no_action
         * 目标坐标为像素：经纬度->像素
         */
        if (coordinateSystemType == CoordinateSystemType.GEOGCS && labelPointType == LabelPointType.PIXEL)
            coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;


//        //如果标注的坐标类型与需要保存的标注类型不一致: 经纬度->像素；像素->经纬度（一般不可能）
//        if (!coordinate.equalsIgnoreCase(labelPointType.name())){
//            //标注的类型为经纬度，保存的坐标为像素，则进行经纬度转像素
//            if (labelPointType == LabelPointType.PIXEL)
//                coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;
//
//            //标注的类型为像素，保存的坐标为经纬度，则进行像素转经纬度，此种情况一般不大可能
//            if (labelPointType == LabelPointType.GEODEGREE)
//                coordinateConvertType = CoordinateConvertType.PIXEL_TO_LONLAT;
//        }else{
//            //如果标注的坐标类型与需要保存的标注类型一致: 像素进行翻转；投影坐标->经纬度坐标
//            //如果标注类型为像素，保存类型也为像素，则进行像素坐标翻转
//            if (labelPointType == LabelPointType.PIXEL)
//                coordinateConvertType = CoordinateConvertType.PIXEL_REVERSION;
//            else{
//                //如果坐标类型为经纬度，且为投影坐标，则将投影坐标转换为经纬度坐标
//                if (GeoUtils.isProjection(imagePath))
//                    coordinateConvertType = CoordinateConvertType.PROJECTION_TO_LONLAT;
//            }
//        }
        LabelPointTypeConvertor.convertLabelPointType(imagePath,labelObject,coordinateConvertType);
        XMLUtils.toXMLFile(labelFileSavePath,labelObject);
    }




    @Override
    public List<String> importTag(String tagFilePath) {
        List<String> tagList = new ArrayList<>();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtils.getStringPath(rootDir,tagFilePath),"r")){
            String line;
            while((line = randomAccessFile.readLine())!=null)
                if (!line.equals("")) {
                    tagList.add(new String(line.getBytes("ISO8859-1"), "utf8"));
                }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(tagList);
        return tagList;
    }
}
