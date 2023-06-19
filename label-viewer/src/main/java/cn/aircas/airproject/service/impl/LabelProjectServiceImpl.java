package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.service.FileTransferProgressInfoService;
import cn.aircas.airproject.utils.*;
import cn.aircas.airproject.entity.emun.LabelPointType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jcraft.jsch.UserInfo;
import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.xml.XMLSerializer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import cn.aircas.airproject.service.LabelProjectService;
import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LabelProjectServiceImpl implements LabelProjectService {

    @Value( value = "${sys.rootDir}")
    private String rootDir;

    @Value( value = "${value.api.download}")
    private String download;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileTransferProgressInfoService fileTransferProgressInfoService;

    @Override
    public String uploadFile(String imagePath , LabelPointType labelPointType ,MultipartFile file) throws Exception {
        XMLSerializer xmlSerializer = new XMLSerializer();

        if (file.getOriginalFilename().endsWith("xml")){
            JSONObject jsonLabel = (JSONObject) xmlSerializer.readFromStream(file.getInputStream());
            return processXml(imagePath,labelPointType,jsonLabel);
        }
        else {
            String result = new String(file.getBytes());
            return processVif(imagePath , labelPointType , result.toString());
        }

    }

    public String processXml(String imagePath , LabelPointType labelPointType , JSONObject jsonLabel) throws Exception {
        JSONArray objects = new JSONArray();
        if (jsonLabel.get("objects") instanceof JSONObject){
            objects.add(jsonLabel.getJSONObject("objects"));
        }
        else if (jsonLabel.get("objects") instanceof JSONArray){
            objects = jsonLabel.getJSONArray("objects");
        }
        else {
            return "无标注信息";
        }

        for (int index = 0, size = objects.size(); index < size; index++) {
            JSONObject object = objects.getJSONObject(index);
            if (object.get("possibleresult") instanceof JSONObject){
                JSONArray jsonArray = new JSONArray();
                jsonArray.add(object.get("possibleresult"));
                object.put("possibleresult",jsonArray);
            }
            coordinateTransform(imagePath,labelPointType,object);
        }

        JSONObject result = new JSONObject();
        result.put("annotation",jsonLabel);
        return result.toString();
    }

    public String processVif(String imagePath , LabelPointType labelPointType , String result) throws Exception {

        org.json.JSONObject xmlJSONObj = XML.toJSONObject(result);
        JSONObject res = JSONObject.fromObject(FormatJson.vifToJson(xmlJSONObj));
        JSONArray resArray = new JSONArray();
        if (res.getJSONObject("annotation").get("objects") instanceof JSONObject){
            resArray.add(res.getJSONObject("annotation").getJSONObject("objects"));
        }
        else if (res.getJSONObject("annotation").get("objects") instanceof JSONArray){
            resArray = res.getJSONObject("annotation").getJSONArray("objects");
        }
        else {
            return "无标注信息";
        }
        //JSONArray resArray = res.getJSONObject("annotation").getJSONArray("objects");
        List<JSONObject> objects = new ArrayList<>();
        for (int index = 0, size = resArray.size(); index < size; index++) {
            JSONObject object = resArray.getJSONObject(index);
            objects.add(coordinateTransform(imagePath,labelPointType,object));

        }
        JSONObject resultFinal = new JSONObject();
        res.getJSONObject("annotation").remove("objects");
        res.getJSONObject("annotation").put("objects",objects);
        resultFinal.put("annotation",JSONObject.fromObject(res).getJSONObject("annotation"));
        return resultFinal.toString();
    }

    @Override
    public String viewXmlFile(String imagePath , LabelPointType labelPointType , String xmlPath) throws Exception {
        String path = rootDir + File.separator + xmlPath ;
        File file = new File(path);
        if (xmlPath.endsWith("xml")){
            XMLSerializer xmlSerializer = new XMLSerializer();
            JSONObject jsonLabel = JSONObject.fromObject(xmlSerializer.readFromFile(file));
            return processXml(imagePath,labelPointType,jsonLabel);
        }
        else{
            String copyPath = rootDir + File.separator + new Date().getTime() + "copyFile.xml";
            File copyFile = new File(copyPath);
            try {
                Files.copy(file.toPath(),copyFile.toPath());
            }catch (Exception e){
                e.printStackTrace();
            }
            String result = FileUtils.readFile(copyPath);
            String resultFinal = processVif(imagePath,labelPointType,result);
            copyFile.delete();
            return resultFinal;
        }



    }

    public String xmlToJson(MultipartFile multipartFile) throws Exception {
        String result = new String(multipartFile.getBytes());
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(result);
        return xmlJSONObj.toString();
    }


    @Override
    public List<FileAndFolder> getFileAndFolderList(String path) {
        CommonResult<com.alibaba.fastjson.JSONObject> userInfo = userService.getUserInfoByToken(request.getHeader("token"));
        boolean isAllowAccess = fileUtils.isAllowAccess(path, userInfo.getData().getString("id"));
        if (!isAllowAccess) {
            return null;
        }
        String relativePath = StringUtils.isBlank(path) ? File.separator : path;
        //path = FilenameUtils.normalizeNoEndSeparator(this.rootDir + File.separator + path);
        //path = "/var/nfs/general/data/AirPAI/AirPAI_Data/components";
        File[] files = new File(path).listFiles();
        List<FileAndFolder> fileAndFolderList = new ArrayList<>();

        if (files == null){
            log.error("路径：{} 不存在",path);
            return new ArrayList<>();
        }
        for (File file : files){
            if (file.isFile()||file.isDirectory()){
                FileAndFolder fileAndFolder = new FileAndFolder();
                fileAndFolder.setName(file.getName());
                fileAndFolder.setIsFile(file.isFile());
                fileAndFolder.setPath(relativePath);
                fileAndFolder.setLastModified(new Date(file.lastModified()));
                fileAndFolder.setFileSize(FileUtils.getFileLength(file));
                if (file.isFile()){
                    fileAndFolder.setExtension(file.getName().substring(file.getName().lastIndexOf(".")+1));
                }
                else {
                    fileAndFolder.setExtension("文件夹");
                }
                fileAndFolderList.add(fileAndFolder);
            }
        }
        List<FileAndFolder> fileAndFolders = fileAndFolderList.stream().sorted((e1,e2)->e1.getName().compareTo(e2.getName())).collect(Collectors.toList());
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
        path = rootDir + File.separator + path;
        return ParseImageInfo.parseInfo(path);
    }

    @Override
    public String updateXml(MultipartFile multipartFile) throws Exception {
        return null;
    }

/*    @Override
    public String updateXml(MultipartFile multipartFile) throws Exception {
        String json = this.xmlToJson(multipartFile);
        JSONObject jsonObject = JSONObject.fromObject(json);
        return (new FormatJson()).updateJson(jsonObject);
    }*/


    public JSONObject coordinateTransform(String imagePath ,LabelPointType labelPointType,JSONObject object) throws Exception {
        String pointType = object.getString("coordinate").toUpperCase();

        List<String> pointList = new ArrayList<>();
        imagePath = rootDir + File.separator +imagePath ;
        JSONArray points = object.getJSONArray("points");
        gdal.AllRegister();
        Dataset dataSet = gdal.Open(imagePath);
        for (int index = 0; index < points.size(); index++) {
            String point = points.getString(index);
            double x = Double.parseDouble(point.split(",")[0]);
            double y = Double.parseDouble(point.split(",")[1]);
            double[] coordinates = new double[]{x,y};
            log.info("pointType is: {}",pointType);
            if (!pointType.equals(labelPointType.toString())){
                if (labelPointType.getValue() == 0) {
                    log.info("pointType is be converted to : geodegree");
                    coordinates = GeoUtils.pixel2Coordinate(x, y, imagePath, GeoUtils.COORDINATE_LONLAT);
                    object.put("coordinate",LabelPointType.GEODEGREE.toString().toLowerCase());
                } else {
                    log.info("pointType is be converted to : pixel");
                    coordinates = GeoUtils.convertCoordinateToPixel(x, y, imagePath, GeoUtils.COORDINATE_LONLAT);
                    coordinates[1] = dataSet.getRasterYSize() - coordinates[1];
                    object.put("coordinate",LabelPointType.PIXEL.toString().toLowerCase());
                }
            }else {
                if (labelPointType == LabelPointType.GEODEGREE){
                    if (dataSet.GetGeoTransform()[0] > 180){
                        double[] projectionCoordination = GeoUtils.coordinateConvertor(x,y,dataSet,GeoUtils.COORDINATE_PROJECTION);
                        coordinates[0] = projectionCoordination[0];
                        coordinates[1] = projectionCoordination[1];
                    }
                }else {
                    //如果是像素，则将左上角转换为左下角供标注页面使用
                    coordinates[1] = dataSet.getRasterYSize() - coordinates[1];
                }
            }
            point = coordinates[0] + "," + coordinates[1];
            pointList.add(point);
        }
        JSONArray possibleresults = object.getJSONArray("possibleresult");
        JSONArray possibleResultTemp = new JSONArray();
        for (Object possibleresult : possibleresults){
            if (possibleresult instanceof String){
                JSONObject possibleJSONObject = new JSONObject();
                possibleJSONObject.put("name",possibleresults);
                possibleResultTemp.add(possibleJSONObject);
                //JSONObject.fromObject(possibleresult).put("name",possibleresult);
            }
            else{
                String name = JSONObject.fromObject(possibleresult).getString("name");
                JSONObject.fromObject(possibleresult).put("name", name);
            }

        }
        if (!possibleResultTemp.isEmpty())
            possibleresults = possibleResultTemp;
        object.remove("possibleresult");
        object.put("possibleresult", possibleresults);
        object.remove("points");
        JSONObject point = new JSONObject();
        point.put("point",pointList);
        object.put("points",point);
        return object;
    }


    @Override
    public void saveLabel(SaveLabelRequest saveLabelRequest) throws IOException {
        String absolutePath = rootDir + File.separator + saveLabelRequest.getSavePath();
        String label = saveLabelRequest.getLabel();
        String xml = labelJsonToXml(label,saveLabelRequest.getImagePath());
        try {
            FileWriter fileWriter = new FileWriter(absolutePath);
            fileWriter.write(xml);
            fileWriter.close();
        }catch (Exception e){
            log.info(String.valueOf(e));
        }
    }

    private String labelJsonToXml(String label,String imagePath){
        String xml;
        imagePath = FileUtils.getStringPath(this.rootDir,imagePath);
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObjectObj = new JSONObject();

        JSONObject labelInfo = JSONObject.fromObject(label);
        JSONArray jsonArray = labelInfo.getJSONArray("object");
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imagePath);

        for (int index = 0; index < jsonArray.size(); index++) {
            JSONObject object = jsonArray.getJSONObject(index);
            String coordinate = object.getString("coordinate");
            JSONArray pointObjects = object.getJSONObject("points").getJSONArray("point");
            List<String> points = new ArrayList<>();

            for(Object point : pointObjects){
                double lon = Double.parseDouble(point.toString().split(",")[0]);
                double lat = Double.parseDouble(point.toString().split(",")[1]);
                if ("pixel".equalsIgnoreCase(coordinate)){
                    lat = dataset.getRasterYSize() - lat;
                }
                if ("geodegree".equalsIgnoreCase(coordinate) ){
                    if (dataset.GetGeoTransform()[0] > 180){
                        double[] coordinates = GeoUtils.coordinateConvertor(lon,lat,imagePath, GeoUtils.COORDINATE_LONLAT);
                        lon = coordinates[0];
                        lat = coordinates[1];
                    }
                }
                point = lon + "," + lat;
                points.add((String) point);
            }
            object.getJSONObject("points").put("point",points);
        }

        jsonObjectObj.put("object",jsonArray);
        Annotation annotation = new Annotation();
        annotation.setObjects(jsonObjectObj);
        JSONObject imageInfo = new JSONObject();
        imageInfo.put("filename",imagePath.substring(imagePath.lastIndexOf(File.separator)+1));
        annotation.setSource(imageInfo);
        jsonObject.put("annotation",annotation);

        StringReader input = new StringReader(jsonObject.toString());
        StringWriter output = new StringWriter();
        JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).repairingNamespaces(false).build();
        try {
            XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);
            XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(output);
            writer = new PrettyXMLEventWriter(writer);
            writer.add(reader);
            reader.close();
            writer.close();
        } catch( Exception e){
            e.printStackTrace();
        } finally {
            try {
                output.close();
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(output.toString().length()>=38){//remove <?xml version="1.0" encoding="UTF-8"?>
            xml = output.toString().substring(39);
        }
        else {
            xml = output.toString();
        }
        return xml;
    }


    public static void main(String[] args) throws Exception {
        File file = new File("d:\\10.xml");
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSONObject jsonLabel = JSONObject.fromObject(xmlSerializer.readFromFile(file));
        LabelProjectServiceImpl labelProjectService = new LabelProjectServiceImpl();
        labelProjectService.processXml("Z:\\Fair1M\\imgs\\10.tif",LabelPointType.GEODEGREE,jsonLabel);
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

    /**
     * 复制文件
     * @param srcPath
     * @param destPath
     */
    @Log("复制文件")
    @Override
    public void copyFileAndFolder(String srcPath, String destPath) {
        //srcPath = FileUtils.getStringPath(this.rootDir, srcPath);
        //destPath = FileUtils.getStringPath(this.rootDir, destPath);
        File file = new File(srcPath);
        if (!file.exists()) {
            return;
        }

        // 获取目标文件
        destPath = destPath + File.separator + file.getName();
        File destFile = new File(destPath);

        if (file.isFile()) {// 文件直接复制写入
            try {
                if (destFile.exists()) {
                    String name = destFile.getName();
                    String oldName = name.substring(0,name.lastIndexOf("."));
                    String suffix = name.substring(name.lastIndexOf("."));
                    String newName = oldName + "-copy" + suffix;
                    destPath = destFile.getParentFile().getAbsolutePath() + File.separator + newName;
                    destFile = new File(destPath);
                    FileUtils.copyFile(file, destFile);
                } else {
                    FileUtils.copyFileToDirectory(file, destFile.getParentFile());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (file.isDirectory()) { // 文件夹递归写入
            if (destFile.exists()) {
                String oldName = destFile.getName();
                String newName = oldName + "-copy";
                destPath = destFile.getParentFile().getAbsolutePath() + File.separator + newName;
                destFile = new File(destPath);
            }
            if (!destFile.exists()) {
                destFile.mkdir();
            }

            File[] files = file.listFiles();
            for (File subFile : files) {
                copyFileAndFolder(subFile.getAbsolutePath(), destFile.getAbsolutePath());
            }
        }

    }

    /**
     * 删除文件或文件夹
     * @param srcPath
     * @return
     */
    @Override
    public boolean deleteFileOrFolder(String srcPath) {
        //String path = FileUtils.getStringPath(this.rootDir, srcPath);
        File file = new File(srcPath);
        if (!file.exists()) {
            return false;
        }

        try {
            if (file.isFile()) {
                if (file.delete()) {
                    return true;
                }
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                // 删除子文件
                for (File subFile : files) {
                    deleteFileOrFolder(subFile.getAbsolutePath());
                }
                // 删除子目录
                file.delete();
                return true;
            }
        } catch (Exception e) {
            log.error("删除失败：异常信息： {}", e.getMessage());
            return false;
        }
        return false;
    }

    /**
     * 重命名文件获取文件夹
     * @param oldName 若是修改文件，是带后缀的全路径
     * @param newName 无论文件还是文件夹，都是全路径
     * @return
     */
    @Override
    public String fileRename(String oldName, String newName) {
        //String srcPath = FileUtils.getStringPath(this.rootDir, oldName);
        File oldFile = new File(oldName);
        if (!oldFile.exists()) {
            return "名称为'" + oldName + "'的文件不存在！";
        }

        String destPath = newName;
        File newFile = new File(destPath);

        // 是文件还是文件夹
        if (oldFile.isFile()) {
            // 获取修改目标后缀
            String name = oldFile.getName();
            String suffix = name.substring(name.lastIndexOf("."));

            destPath = newName + suffix;
            newFile = new File(destPath);

            if (newFile.exists()) {
                return "名称为'" + newName + "'的文件已存在！";
            }
        } else if (oldFile.isDirectory()) {
            if (newFile.exists()) {
                return "名称为'" + newName + "'的文件夹已存在！";
            }
        }

        // 重命名
        if (oldFile.renameTo(newFile)) {
            return "重命名成功！";
        }

        return "重命名失败！";
    }

    @Override
    public FileInfo getFileInfo(String path) {
        //path = FileUtils.getStringPath(this.rootDir, path);
        File file = new File(path);
        if (!file.exists()) {
            log.error("名称为'" + FilenameUtils.getName(file.getAbsolutePath()) + "'的文件不存在");
            return null;
        }

        FileInfo fileInfo = new FileInfo();

        String fileName = FilenameUtils.getName(file.getAbsolutePath());
        if (file.isFile()) {
            fileInfo.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
        } else {
            fileInfo.setFileType("文件夹");
            fileInfo.setInFileNum(FileUtils.getInFileNum(file));
            fileInfo.setInFolderNum(FileUtils.getInFolderNum(file));
        }
        fileInfo.setName(fileName);
        fileInfo.setLocation(path.replace(fileName, ""));
        fileInfo.setFileSize(FileUtils.getFileLength(file));
        fileInfo.setModifyTime(new Date(file.lastModified()));

        return fileInfo;
    }

    @Override
    public boolean createFile(String path) {
        //path = FileUtils.getStringPath(this.rootDir, path);
        File file = new File(path);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean createFolder(String path) {
        //path = FileUtils.getStringPath(this.rootDir, path);
        try {
            Files.createDirectory(Paths.get(path));
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    ///////////////////////////////////////////////////读写文件////////////////////////////////////////////////////////
    /**
     * 读取整个文件，byte数组形式
     * @param path
     * @return
     */
    @Override
    public String getContent(String path) {
        //path = FileUtils.getStringPath(this.rootDir, path);
        //path = path.replace("/", "\\");
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        // 若为二进制文件，不处理
        if (FileUtils.isBinary(file)) {
            return "binary";
        }

        byte[] bytes = null;
        String content = "";
        try {
            long length = file.length();
            bytes = new byte[(int) length];
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            content = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content;
    }

    /**
     * 按照字节范围获取数据
     * @param path
     * @param startPos 十进制字节数
     * @return
     */
    public String getContentByPosition(String path, long startPos) {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        // 限制字节范围
        long pos = 0;
        if (startPos < 0 || startPos > file.length()) {
            return "pos";
        }
        pos = startPos;

        String result = "";
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            // 默认返回1KB的数据 0X400 十六进制的Byte表示
            int len = 0X400 * 1024 * 2;
            long size = len <= (file.length()-pos) ? len : (file.length()-pos);

            MappedByteBuffer byteBuffer = randomAccessFile.getChannel()
                    .map(FileChannel.MapMode.READ_ONLY, pos, size);
            byte[] bytes = new byte[(int) size];
            for (int i = 0; i < byteBuffer.capacity(); i++) {
                System.out.println(i);
                bytes[i] = byteBuffer.get(i);
            }
            result = new String(bytes, "UTF-8").trim();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 按照字节范围写入数据
     * @param path 目标文件
     * @param startPos 十进制字节数
     * @return
     */
    @Override
    public String writeByPosition(String path, long startPos, long endPos, String content) {
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        // 限制字节范围
        if (startPos < 0 || startPos > endPos) {
            return "pos";
        }
        if (startPos > file.length()) {
            startPos = file.length();
        }
        if (endPos > file.length()) {
            endPos = file.length();
        }

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // 清空原文件修改内容
            byte[] nullBytes = new byte[(int) (endPos-startPos)];
            randomAccessFile.seek(startPos);
            randomAccessFile.write(nullBytes);

            // 创建临时文件保存指针后文件内容
            randomAccessFile.seek(endPos); // 跳转到结束指针位置
            File tempFile = File.createTempFile("temp", null);
            FileInputStream inputStream = new FileInputStream(tempFile);
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            // 将文件内容读入临时文件
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = randomAccessFile.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            // 清空原文件多余的byte位
            int surPlusByte = nullBytes.length - content.length();
            if (surPlusByte > 0) {
                randomAccessFile.setLength(randomAccessFile.length()-surPlusByte);
            }

            // 回到指针处，保存更新内容
            randomAccessFile.seek(startPos);
            randomAccessFile.write(content.getBytes());
            // 追加临时文件中的内容
            while ((len = inputStream.read(buffer)) > 0) {
                randomAccessFile.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 利用缓冲区分段获取文件内容
     * @param path
     * @return
     */
    @Override
    public String getContentByBuffer(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        String string = "";

        // 构建缓冲区大小为0.5M
        final int BUFFER_SIZE = 0X5000;

        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            //randomAccessFile.skipBytes(100);
            //randomAccessFile.seek(100);
            //randomAccessFile.write(new String("456456").getBytes());
            MappedByteBuffer buffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE,0, 0X400 * 1024 * 2);
            byte[] dst = new byte[buffer.capacity()];
//            for (int i = buffer.capacity() / 2; i < buffer.capacity() / 2 + 100; i++) {
//                System.out.println(buffer.get(i));
//                dst[i] = buffer.get(i);
//            }

            for (int i = 0; i < buffer.capacity(); i++) {
                System.out.println(buffer.get(i));
                dst[i] = buffer.get(i);
            }
            string = new String(dst, "UTF-8").trim();

            /*// 获取某个范围的文件数据
            MappedByteBuffer buffer = new RandomAccessFile(file, "rw").getChannel().map(FileChannel.MapMode.READ_WRITE,0, file.length());

            // 文件存储介质
            byte[] dst = new byte[BUFFER_SIZE];
            // 开始处理数据
            long startTime = System.currentTimeMillis();
            for (int offset = 0; offset < buffer.capacity(); offset+=BUFFER_SIZE) {
                if ((buffer.capacity() - offset) > BUFFER_SIZE) {
                    for (int i = 0; i < BUFFER_SIZE; i++) {
                        dst[i] = buffer.get(offset+i);
                    }
                } else {
                    for (int i = 0; i < buffer.capacity() - offset; i++) {
                        dst[i] = buffer.get(offset+i);
                    }
                }
            }
            int length = buffer.capacity() % BUFFER_SIZE == 0 ? BUFFER_SIZE : buffer.capacity() % BUFFER_SIZE;
            string = new String(dst, 0, BUFFER_SIZE);
            log.info("缓存数据获取执行时间：{}", System.currentTimeMillis() - startTime);*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 获取整个文件内容，输出流方式
     * @param path
     * @return
     */
    @Override
    public JSONObject getFileContent(String path) {
        JSONObject result = new JSONObject();
        //path = FileUtils.getStringPath(this.rootDir, path);
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }

        // 如果为图片
        String[] supportFileType = {"JPG", "PNG", "TIF", "TIFF", "JPEG"};;
        if (FilenameUtils.isExtension(file.getName().toUpperCase(), supportFileType)) {
            String imgBase64 = Base64Utils.encodeToString(FileUtils.getBytesByFile(file));
            result.put("isImg", true);
            result.put("content", "图片类型文件使用OTHER进行处理");
            result.put("filePath", file.getAbsolutePath());
            return result;
        }
        // 若为二进制文件，不处理
        if (FileUtils.isBinary(file)) {
            result.put("isImg", false);
            result.put("content", "binary");
            result.put("filePath", file.getAbsolutePath());
            return result;
        }
        // 如果为文件类型
        InputStream is = null;
        ByteArrayOutputStream os = null;
        byte[] buffer = new byte[1024];
        int len = 0;
        try {
            is = new FileInputStream(file);
            os = new ByteArrayOutputStream();
            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            result.put("isImg", false);
            result.put("content", os.toString("UTF-8"));
            result.put("filePath", file.getAbsolutePath());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 写入文件
     * @param path
     * @param content
     * @param append
     * @return
     */
    @Override
    public boolean writeFile(String path, String content, boolean append) {
        //path = FileUtils.getStringPath(this.rootDir, path);
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return false;
        }

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {
            fileWriter = new FileWriter(file.getAbsolutePath(), append);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    ///////////////////////////////////////////////////读写文件////////////////////////////////////////////////////////


    /**
     * 下载文件
     * @param src_file_path 文件路径
     * @return
     */
    @Override
    public CommonResult downLoad(String src_file_path) {
        File file = new File(src_file_path);
        if (!file.exists()) {
            return null;
        }

        String url = this.download;
        int service_id = ConnectServiceManager.serviceId;
        String token = request.getHeader("token");
        Map<String,Object> params = new HashMap<>();
        params.put("src_file_path", src_file_path);
        params.put("service_id", service_id);
        params.put("download_file_name", null);

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Map<String,Object>> entity = new HttpEntity<>(params, headers);
        CommonResult body = null;
        try {
            body = restTemplate.exchange(url, HttpMethod.POST, entity, CommonResult.class).getBody();
        } catch (Exception e) {
            log.error("接口 {} 响应出错", url);
            body = new CommonResult();
            body.setMessage("接口 " + url + " 响应出错");
            return body;
        }
        return body;
    }


    /**
     * 上传文件到服务器
     * @param progressId
     * @param inputStream
     * @param destPath
     * @param fileName
     * @return
     */
    @Override
    @Async
    public void upload(String progressId, InputStream inputStream, String destPath, String fileName) throws IOException {

        ProgressInfo progressInfo = new ProgressInfo();
        progressInfo.setProgressId(progressId);
        progressInfo.setSecond(0);
        progressInfo.setTransVelocity("0");
        progressInfo.setTransLength(0);
        progressInfo.setPlan("0");
        progressInfo.setFileSize(inputStream.available());
        progressInfo.setRemainTime("0");
        progressInfo.setConsumTime("0");

        FileUtils.uploadFile(progressInfo, inputStream, destPath, fileName);
    }


    /**
     * 分块上传文件 TODO 写完了，没测试呢，下一步测试该接口怎么使用，有没有问题
     * @param param
     * @return
     */
    @Override
    public String chunkUploadFile(ChunkFileParam param) throws IOException, ServiceNotFoundException {
        if (param.getTaskId() == null || param.getTaskId().equals("")) {
            param.setTaskId(UUID.randomUUID().toString());
        }

        String fileName = param.getFile().getOriginalFilename();
        String tempFileName = param.getTaskId() + fileName.substring(fileName.lastIndexOf(".")) + "_temp";
        String filePath = rootDir + "/original";
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File tempFile = new File(filePath, tempFileName);

        RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
        FileChannel channel = raf.getChannel();
        long offset = param.getChunk() * param.getSize();
        byte[] fileData = param.getFile().getBytes();
        MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
        mappedByteBuffer.put(fileData);
        FileUtils.freeMappedByteBuffer(mappedByteBuffer);
        channel.close();
        raf.close();
        boolean isComplete = checkUploadStatus(param, fileName, filePath);
        if (isComplete) {
            renameFile(tempFile, fileName);
        }

        return null;
    }


    /**
     * 分块上传文件 分块调用时，已生成数据库记录 TODO
     * 进度实例上传进度这里，比较复杂再说
     * 目前已完成文件上传到临时目录，并且将文件合并到目标位置，合并完成后将临时文件删除，都已完成
     * 但是不确定，实际部署后能否上传文件成功，需要在进行测试
     * @param param
     * @return
     */
    @Override
    @Async
    public void chunkUploadFile(MultipartFileParam param) {
        // 文件上传进度实例
        ProgressInfo progressInfo = null;
        FileTransferProgressInfo one = this.fileTransferProgressInfoService.getOne(new QueryWrapper<FileTransferProgressInfo>()
                .eq("md5", param.getFileMd5()));
        if (one == null) {
            // 生成文件上传记录
            FileTransferProgressInfo transferRcord = FileTransferProgressInfo.builder()
                    .chunks(param.getChunkTotal()).md5(param.getFileMd5()).createTime(new Date()).build();
            this.fileTransferProgressInfoService.save(transferRcord);
            // 新建进度实例
            progressInfo = new ProgressInfo();
            progressInfo.setProgressId(param.getFileMd5());
            progressInfo.setFileSize(param.getFileSize());
        } else {
            progressInfo = ProgressResponseSingleTon.getInstance().get(param.getFileMd5());
        }

        // 若获取的进度为空，则创建一个，理论上这种情况不会存在
        if (progressInfo == null) {
            progressInfo = new ProgressInfo();
            progressInfo.setProgressId(param.getFileMd5());
            progressInfo.setFileSize(param.getFileSize());
        }

        // 执行上传
        FtpUtils.chunkUploadFile(progressInfo, param);
        // 更新已完成传输分片数
        this.fileTransferProgressInfoService.updateTransferedChunk(param.getFileMd5());
        // 校验文件是否传输完成
        boolean isComplete = this.fileTransferProgressInfoService.checkFileComplete(param.getFileMd5());
        // 合并文件
        if (isComplete) {
            String destPath = FileUtils.getStringPath(param.getDestPath(), param.getFileMd5()).replace("\\", "/");
            this.fileTransferProgressInfoService.mergeChunk(param, destPath);
        }
    }

    /**
     * 重命名文件
     * @param file
     * @param fileName
     * @return
     */
    private boolean renameFile(File file, String fileName) {
        // 检查要命名的文件是否存在，是否是文件
        if (!file.exists() || file.isDirectory()) {
            return false;
        }
        String p = file.getParent();
        File newFile = new File(p + File.separator + fileName);
        return file.renameTo(newFile);
    }


    /**
     * 检查文件上传进度
     * @param param
     * @param fileName
     * @param filePath
     * @return
     */
    private boolean checkUploadStatus(ChunkFileParam param, String fileName, String filePath) throws IOException {
        File confFile = new File(filePath, fileName + ".conf");
        RandomAccessFile confAccessFile = new RandomAccessFile(confFile, "rw");
        // 设置文件长度
        confAccessFile.setLength(param.getChunkTotal());
        // 设置起始偏移量
        confAccessFile.seek(param.getChunk());
        // 将指定的一个字节写入文件中 127
        confAccessFile.write(Byte.MAX_VALUE);
        byte[] completeStatusList = org.apache.commons.io.FileUtils.readFileToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeStatusList.length && isComplete == Byte.MAX_VALUE; i++) {
            isComplete = (byte)(isComplete & completeStatusList[i]);
            System.out.println("check part " + i + "complete?:" + completeStatusList[i]);
        }
        if (isComplete == Byte.MAX_VALUE) {
            // 如果全部完成，删除conf文件
            confFile.delete();
            return true;
        }
        return false;
    }
}
