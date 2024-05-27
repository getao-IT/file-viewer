package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.config.labelParser.parsers.LabelFileParserComposite;
import cn.aircas.airproject.config.labelParser.parsers.LabelParserComposite;
import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.VifLabelOjectInfo;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.*;
import cn.aircas.airproject.service.FileService;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.service.LabelTagService;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.*;
import cn.aircas.utils.image.geo.GeoUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class LabelProjectServiceImpl implements LabelProjectService {

    @Value(value = "${sys.rootDir}")
    private String rootDir;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private LabelTagChildrenServiceImpl childrenService;

    @Autowired
    private LabelTagParentServiceImpl parentService;

    @Autowired
    private LabelFileParserComposite labelparser;


    /**
     * 判断影像文件是否包含金字塔
     *
     * @param imagePath
     * @return
     */
    @Override
    public boolean hasOverview(String imagePath) {
        File imageFile = FileUtils.getFile(this.rootDir, imagePath);
        if (!imageFile.exists()) {
            log.error("文件：{} 不存在", imagePath);
            return true;
        }

        return cn.aircas.utils.image.ParseImageInfo.hasOverview(imageFile.getAbsolutePath());
    }


    /**
     * 对影像创建金字塔
     *
     * @param imagePath
     */
    @Override
    @Async
    public void buildOverviews(String progressId, String imagePath) {
        File imageFile = FileUtils.getFile(this.rootDir, imagePath);
        if (!imageFile.exists()) {
            log.error("文件：{} 不存在", imagePath);
            return;
        }

        ProgressService service = new ProgressServiceImpl();
        final long[] callBackTime = {System.currentTimeMillis()};
        long taskStartTime = callBackTime[0];
        ProgressContr progress = null;

        try {
            Date startTime = DateUtils.parseDate(org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), new String[]{"yyyy-MM-dd HH:mm:ss"});
            progress = ProgressContr.builder().taskId(progressId).filePath(imageFile.getAbsolutePath()).outputPath(imagePath + ".ovr").consumTime(0)
                    .fileName(imageFile.getName()).taskType(TaskType.OVERVIEWS).status(TaskStatus.WORKING)
                    .startTime(startTime).progress("0%").describe("金字塔构建中...").build();
            ProgressContr taskById = service.createTaskById(progress);
            log.info("创建传输任务成功：taskId {}， 任务类型 {}， [ {} ]", progressId, TaskType.OVERVIEWS, taskById);
            ImageUtil.buildOverviews(imageFile.getAbsolutePath(), progress);
            ProgressContrDto success = ProgressContrDto.builder().taskId(progressId).filePath(imageFile.getAbsolutePath()).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("金字塔构建成功").status(TaskStatus.FINISH).progress("100%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(success);
            log.info("金字塔构建成功， 更新状态：{}, [ {} ]", i, success);
        } catch (Exception e) {
            ProgressContrDto fail = ProgressContrDto.builder().taskId(progressId).filePath(imageFile.getAbsolutePath()).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("金字塔构建失败").status(TaskStatus.FAIL).consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(fail);
            log.error("金字塔构建异常：{}，更新状态：{}, [ {} ]", e.getMessage(), i, fail);
        }

    }

    @Override
    public String uploadFile(String imagePath, LabelPointType labelPointType, MultipartFile file) throws Exception {
        File tempFileDir = FileUtils.getFile(this.rootDir, "/temp");
        if (!tempFileDir.exists())
            tempFileDir.mkdirs();
        File labelFile = FileUtils.getFile(this.rootDir, "/temp", file.getOriginalFilename());
        file.transferTo(labelFile);

        String label = viewXmlFile(imagePath, labelPointType, "/temp/" + file.getOriginalFilename());
        labelFile.delete();
        return label;
    }


    @Override
    public String viewXmlFile(String imagePath, LabelPointType labelPointType, String labelFilePath) throws Exception {

        String parseResult = null;

        LabelFileType fileType = LabelFileType.XML;
        LabelFileFormat fileFormat = labelFilePath.endsWith("xml") ? LabelFileFormat.AIRCAS: LabelFileFormat.VIF;


        if(labelparser.support(fileType, fileFormat)){
            parseResult = labelparser.parseLabelFile(labelFilePath, imagePath);
        }
        return parseResult;


//        String labelPath = FileUtils.getStringPath(this.rootDir, labelFilePath);
//        if (!new File(labelPath).exists()) {
//            return null;
//        }
//
//        String imageFilePath = FileUtils.getStringPath(this.rootDir, imagePath);
//        Class clazz = labelPath.endsWith("xml") ? XMLLabelObjectInfo.class : VifLabelOjectInfo.class;
//        LabelObject labelObject = (LabelObject) XMLUtils.parseXMLFromFile(clazz, labelPath);
//        if(null == labelObject){
//            return "label info format error";
//        }
//        if(VifLabelOjectInfo.class == clazz){
//            Method vifJsonToLabelInfo = clazz.getMethod("vifJsonToLabelInfo");
//            JSONObject vifObject = (JSONObject) vifJsonToLabelInfo.invoke(labelObject);
//            return vifObject.toJSONString();
//        }
//
//        String coordinate = labelObject.getCoordinate();
//        CoordinateConvertType coordinateConvertType = CoordinateConvertType.NO_ACTION;
//        //如果标注点类型与图像坐标系不同
//        if (!labelPointType.name().equalsIgnoreCase(coordinate)) {
//            if (labelPointType == LabelPointType.GEODEGREE) {
//                if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name()))
//                    coordinateConvertType = CoordinateConvertType.PROJECTION_TO_LONLAT;
//                else
//                    coordinateConvertType = CoordinateConvertType.PIXEL_TO_LONLAT;
//            }
//            if (labelPointType == LabelPointType.PROJECTION) {
//                if (coordinate.equalsIgnoreCase(LabelPointType.GEODEGREE.name()))
//                    coordinateConvertType = CoordinateConvertType.LONLAT_TO_PROJECTION;
//                else
//                    coordinateConvertType = CoordinateConvertType.PIXEL_TO_PROJECTION;
//            }
//            if (labelPointType == LabelPointType.PIXEL) {
//                if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name()))
//                    coordinateConvertType = CoordinateConvertType.PROJECTION_TO_PIXEL;
//                else
//                    coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;
//            }
//        } else {
//            //如果图像坐标为像素，标注坐标为像素，则将像素进行翻转
//            if (LabelPointType.PIXEL == labelPointType)
//                coordinateConvertType = CoordinateConvertType.PIXEL_REVERSION;
//        }
//        LabelPointTypeConvertor.convertLabelPointType(imageFilePath, labelObject, coordinateConvertType);
//        return labelObject.toJSONObject().toString();
    }

    @Override
    public String viewSelectedLabelFile(String imagePath, String labelPath, LabelFileType fileType, LabelFileFormat fileFormat) throws Exception {

        String parseResult = null;

        if(labelparser.support(fileType, fileFormat)){
            parseResult = labelparser.parseLabelFile(labelPath, imagePath);
        }
        return parseResult;
    }


    @Override
    public List<FileAndFolder> getFileAndFolderList(String path) {
        String relativePath = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        File[] files = new File(path).listFiles();
        List<FileAndFolder> fileAndFolderList = new ArrayList<>();

        if (files == null || files.length == 0) {
            log.error("路径：{} 不存在", path);
            return new ArrayList<>();
        }
        for (File file : files) {
            if (file.isFile() || file.isDirectory()) {
                FileAndFolder fileAndFolder = new FileAndFolder();
                fileAndFolder.setName(file.getName());
                fileAndFolder.setIsFile(file.isFile());
                fileAndFolder.setPath(relativePath);
                fileAndFolder.setLastModified(new Date(file.lastModified()));
                if (file.isFile()) {
                    fileAndFolder.setIsFile(true);
                    fileAndFolder.setExtension(file.getName().substring(file.getName().lastIndexOf(".") + 1));
                    fileAndFolder.setSize(cn.aircas.utils.file.FileUtils.fileSizeToString(file.length()));
                } else {
                    fileAndFolder.setIsFile(false);
                    fileAndFolder.setExtension("文件夹");
                    fileAndFolder.setSize("0B");

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
        List<FileAndFolder> fileAndFolders = fileAndFolderList.stream().sorted((fileName1, fileName2) ->
                fileName1.getName().compareTo(fileName2.getName())).collect(Collectors.toList());
        return fileAndFolders;
    }

    @Override
    public List<FolderPac> getFolderList(String path) {
        path = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        File[] files = new File(path).listFiles();

        if (files == null) {
            log.error("路径：{} 不存在", path);
            return new ArrayList<>();
        }
        List<FolderPac> folderList = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
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
                if (file.isFile() && (file.getName().endsWith("jpg") || file.getName().endsWith("tif") || file.getName().endsWith("tiff") || file.getName().endsWith("png"))) {
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
        List<FilePac> fileList = filePacList.stream().sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());
        System.out.println(System.currentTimeMillis());
        return fileList;
    }

    @Override
    public ImageInfo getImageInfo(String path) {
        path = FileUtils.getStringPath(rootDir, path);
        return ParseImageInfo.parseInfo(path);
    }

    @Override
    public String updateXml(MultipartFile multipartFile) throws Exception {
        return null;
    }


    @Override
    public String saveAsLabel(SaveLabelRequest saveLabelRequest) {
        String label = saveLabelRequest.getLabel();
        LabelPointType labelPointType = saveLabelRequest.getLabelPointType();
        String labelFileSavePath = rootDir + File.separator + saveLabelRequest.getSavePath();
        String imagePath = FileUtils.getStringPath(this.rootDir, saveLabelRequest.getImagePath());
        com.alibaba.fastjson.JSONObject jsonLabel = com.alibaba.fastjson.JSONObject.parseObject(label);
        LabelObject labelObject = com.alibaba.fastjson.JSONObject.toJavaObject(jsonLabel, XMLLabelObjectInfo.class);
        String coordinate = labelObject.getCoordinate();
        ImageInfo imageInfo = ParseImageInfo.parseInfo(imagePath);
        CoordinateSystemType coordinateSystemType = imageInfo.getCoordinateSystemType();

        CoordinateConvertType coordinateConvertType = CoordinateConvertType.NO_ACTION;

        /**
         * 如果图像坐标是投影坐标
         * 目标坐标为经纬度：投影->经纬度
         * 目标坐标为像素：投影->像素
         */
        if (coordinateSystemType == CoordinateSystemType.PROJCS) {
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
        LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, coordinateConvertType);
        XMLUtils.toXMLFile(labelFileSavePath, labelObject);
        return "success";
    }

    @Override
    public boolean saveLabel(SaveLabelRequest saveLabelRequest) {
        String imagePath = FileUtils.getStringPath(this.rootDir, saveLabelRequest.getImagePath());
        String savePath = FileUtils.getStringPath(this.rootDir, saveLabelRequest.getSavePath());
        String label = saveLabelRequest.getLabel();
        LabelPointType labelPointType = saveLabelRequest.getLabelPointType();
        LabelPointType targetPointType = saveLabelRequest.getTargetPointType();
        LabelObject labelObject = JSONObject.toJavaObject(JSONObject.parseObject(label), XMLLabelObjectInfo.class);

        File saveFile = new File(savePath);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }

        try {
            if (labelPointType.equals(LabelPointType.PIXEL)) {
                labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.PIXEL_REVERSION);
            }

            if (labelPointType.equals(LabelPointType.GEODEGREE)) {
                if (targetPointType.equals(LabelPointType.PROJECTION)) {
                    labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.LONLAT_TO_PROJECTION);
                }
                if (targetPointType.equals(LabelPointType.PIXEL)) {
                    labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.LONLAT_TO_PIXEL);
                }
            }

            if (labelPointType.equals(LabelPointType.PROJECTION)) {
                if (targetPointType.equals(LabelPointType.GEODEGREE)) {
                    labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.PROJECTION_TO_LONLAT);
                }
                if (targetPointType.equals(LabelPointType.PIXEL)) {
                    labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.PROJECTION_TO_PIXEL);
                }
                //labelObject = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelObject, CoordinateConvertType.PROJECTION_TO_LONLAT);
            }
            XMLUtils.toXMLFile(savePath, labelObject);
            log.info("保存标注信息成功：坐标类型 {}，保存路径 {} ", targetPointType, savePath);
            return true;
        } catch (Exception e) {
            log.error("保存标注信息失败：坐标类型 {}，保存路径 {} ", targetPointType, savePath);
        }
        return false;
    }


    /**
     * 导出XML文件
     *
     * @param labelRequest
     * @return
     */
    @Override
    public String exportLabel(SaveLabelRequest labelRequest) {
        LabelPointType labelPointType = labelRequest.getLabelPointType();
        LabelPointType targetPointType = labelRequest.getTargetPointType();

        String labelInfo = labelRequest.getLabel();
        if (labelInfo == null) {
            log.error("无标注信息");
            return null;
        }

        String filePath = FileUtils.getStringPath(this.rootDir, labelRequest.getImagePath());
        LabelObject xmlLabelObjectInfo = JSONObject.toJavaObject(JSONObject.parseObject(labelInfo), XMLLabelObjectInfo.class);
        if (labelPointType == LabelPointType.GEODEGREE) {
            if (targetPointType == LabelPointType.PIXEL) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.LONLAT_TO_PIXEL);
            }
            if (targetPointType == LabelPointType.PROJECTION) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.LONLAT_TO_PROJECTION);
            }
        }
        if (labelPointType == LabelPointType.PROJECTION) {
            if (targetPointType == LabelPointType.PIXEL) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.PROJECTION_TO_PIXEL);
            }
            if (targetPointType == LabelPointType.GEODEGREE) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.PROJECTION_TO_LONLAT);
            }
        }
        if (labelPointType == LabelPointType.PIXEL) {
            if (targetPointType == LabelPointType.GEODEGREE) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.PIXEL_TO_LONLAT);
            }
            if (targetPointType == LabelPointType.PROJECTION) {
                xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.PIXEL_TO_PROJECTION);
            }
        }
        if (labelPointType == LabelPointType.PIXEL && targetPointType == LabelPointType.PIXEL) {
            xmlLabelObjectInfo = LabelPointTypeConvertor.convertLabelPointType(filePath, xmlLabelObjectInfo, CoordinateConvertType.PIXEL_REVERSION);
        }
        String xmlString = XMLUtils.toXMLString(xmlLabelObjectInfo);

        OutputStream os = null;
        try {
            File file = new File(filePath);
            String xmlName = file.getName().replace(FilenameUtils.getExtension(file.getName()), "xml");
            response.setCharacterEncoding("UTF-8");
            //response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + new String(xmlName.getBytes(), "UTF-8"));
            byte[] bytes = xmlString.getBytes("UTF-8");
            os = response.getOutputStream();

            os.write(bytes);
            os.close();
        } catch (Exception ex) {
            log.error("导出失败:", ex);
            throw new RuntimeException("导出失败");
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (IOException ioEx) {
                log.error("导出失败:", ioEx);
            }
        }

        return filePath;
    }


    /**
     * 导入标注
     *
     * @param imagePath
     * @param labelPointType
     * @param file
     * @return
     */
    @Override
    public String importLabel(String imagePath, LabelPointType labelPointType, MultipartFile file) {
        if (file.isEmpty()) {
            log.error("导入数据为空：{}", file.getOriginalFilename());
            return null;
        }
        LabelObject labelInfo = null;
        try {
            labelInfo = XMLUtils.parseXMLFromStream(file.getInputStream(), XMLLabelObjectInfo.class);

            parentService.setIpAndDriver();
            childrenService.setIpAndDriver();
            JSONObject jsonLabel = JSONObject.parseObject(labelInfo.toJSONObject().toJSONString());
            JSONArray object = jsonLabel.getJSONArray("object");
            LabelTagParent parent = new LabelTagParent();
            parent.setTag_name("其他");
            LabelTagParent parentInfo = (LabelTagParent) parentService.queryList(LabelTagParent.class, parent).get(0);
            JSONArray newObject = new JSONArray();
            for (Object o : object) {
                JSONObject oJson = JSONObject.parseObject(o.toString());
                JSONObject possibleresult = oJson.getJSONArray("possibleresult").getJSONObject(0);
                String name = possibleresult.getString("name");
                LabelTagChildren params = new LabelTagChildren();
                params.setTag_name(name);
                List<Object> childrens = childrenService.queryList(LabelTagChildren.class, params);
                if (childrens == null || childrens.size() == 0) {
                    possibleresult.put("basicname", "其他");
                    int parentId = parentInfo.getId();
                    LabelTagChildren children = new LabelTagChildren();
                    children.setParent_id(parentId);
                    children.setTag_name(possibleresult.getString("name"));
                    children.setProperties_name(possibleresult.getString("name"));
                    children.setParenttag_name("其他");
                    children.setProperties_color(SQLiteUtils.takeColorHex());
                    childrenService.insert(children);
                } else {
                    LabelTagChildren children = (LabelTagChildren) childrens.get(0);
                    possibleresult.put("basicname", children.getParenttag_name());
                }
                newObject.add(oJson);
            }
            jsonLabel.put("object", newObject);
            labelInfo = jsonLabel.toJavaObject(XMLLabelObjectInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String coordinate = labelInfo.getCoordinate();
        imagePath = FileUtils.getStringPath(this.rootDir, imagePath);
        if (labelPointType == LabelPointType.GEODEGREE) {
            if (coordinate.equalsIgnoreCase(LabelPointType.PIXEL.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PIXEL_TO_LONLAT);
            }
            if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PROJECTION_TO_LONLAT);
            }
        }
        if (labelPointType == LabelPointType.PROJECTION) {
            if (coordinate.equalsIgnoreCase(LabelPointType.PIXEL.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PIXEL_TO_PROJECTION);
            }
            if (coordinate.equalsIgnoreCase(LabelPointType.GEODEGREE.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.LONLAT_TO_PROJECTION);
            }
        }
        if (labelPointType == LabelPointType.PIXEL) {
                /*if (coordinate.equalsIgnoreCase(LabelPointType.PIXEL.name())) {
                    labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PIXEL_REVERSION);
                }*/
            if (coordinate.equalsIgnoreCase(LabelPointType.GEODEGREE.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.LONLAT_TO_PIXEL);
            }
            if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name())) {
                labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PROJECTION_TO_PIXEL);
            }
        }
        if (labelPointType == LabelPointType.PIXEL && coordinate.equalsIgnoreCase(LabelPointType.PIXEL.name())) {
            labelInfo = LabelPointTypeConvertor.convertLabelPointType(imagePath, labelInfo, CoordinateConvertType.PIXEL_REVERSION);
        }
        return labelInfo.toJSONObject().toJSONString();
    }

    @Override
    public List<String> importTag(String tagFilePath) {
        List<String> tagList = new ArrayList<>();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(FileUtils.getStringPath(rootDir, tagFilePath), "r")) {
            String line;
            while ((line = randomAccessFile.readLine()) != null)
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
