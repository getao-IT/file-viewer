package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.common.PageResult;
import cn.aircas.airproject.entity.domain.FileSearchParam;
import cn.aircas.airproject.entity.domain.Slice;
import cn.aircas.airproject.entity.emun.FileType;
import cn.aircas.airproject.entity.emun.SourceFileType;
import cn.aircas.airproject.service.FileService;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageInfo;
import cn.aircas.utils.image.ParseImageInfo;
import cn.aircas.utils.image.slice.SliceGenerateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.*;



/**
 * @Vanishrain
 * 文件管理服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${sys.uploadRootPath}")
    String uploadRootPath;

    @Value("${sys.rootPath}")
    String rootPath;

    @Autowired
    private ImageFileServiceImpl service;


    @Override
    public List<String> getFileType() {
        List<String> fileTypeList = new ArrayList<>();
        for (FileType fileType : FileType.values()) {
            fileTypeList.add(fileType.getValue().toUpperCase());
        }
        return fileTypeList;
    }


    /**
     * 列出文件夹下的文件夹
     * @param path
     * @return
     */
    @Override
    public List<JSONObject> listFolderFiles(String path) {
        path = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.uploadRootPath + File.separator + path);
        File[] files = new File(path).listFiles();

        if (files == null){
            log.error("路径：{} 不存在",path);
            return new ArrayList<>();
        }

        // 过滤掉文件路径，只显示文件夹
        /*List<String> filePathList = Arrays.stream(files)
                .filter(file -> file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());*/

        List<JSONObject> filePathList = Lists.newArrayList();
        for (File file : files) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name" , file.getName());
            jsonObject.put("isFile" , !file.isDirectory());
            filePathList.add(jsonObject);
        }

        return filePathList;
    }


    @Override
    public PageResult<JSONObject> getContent(int pageSize, int pageNo, FileType fileType, int fileId) {
        return null;
    }


    /**
     * 裁切影像得到切片图片
     * @param slice
     */
    @Override
    public void makeImageSlice(Slice slice) {
        service.makeImageGeoSlice(slice);
    }


    /**
     * 裁切影像得到切片图片
     * @param slice
     */
    @Override
    public void makeImageAllGeoSlice(Slice slice) {
        service.makeImageAllGeoSlice(slice.getFileType(), slice.getImagePath(), slice.getWidth(), slice.getHeight(),
                slice.getSliceInsertPath(), slice.getStep(), slice.getStorage(), slice.getRetainBlankSlice(), slice.getTakeLabelXml(),slice.getCoordinateType());
    }


    /**
     * 创建文件夹
     * @param path
     * @return
     */
    @Override
    public String createDirs(String path) {
        path = FileUtils.getStringPath(rootPath, path);
        File file = new File(path);
        if (file.exists()) {
            return "文件夹已存在";
        }
        file.mkdirs();
        return path;
    }


    /**
     * 创建切片保存路径
     * @param path 选择的位置 加上填写的名称
     * @param fileName 文件名称
     * @return
     */
    @Override
    public String createSlicePath(String path, String fileName) {
        path = FileUtils.getStringPath(this.rootPath, path) + "." + FilenameUtils.getExtension(fileName);
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        return null;
    }


    /**
     * 创建切片保存路径
     * @param savePath 选择的位置
     * @param filePath 文件路径
     * @param width 切片宽度
     * @param height 切片高度
     * @param step 步长
     * @return
     */
    @Override
    public Boolean createSlicePaths(String savePath, String filePath, int width, int height, int step) {
        filePath = FileUtils.getStringPath(this.rootPath,filePath);
        ImageInfo image = ParseImageInfo.parseInfo(filePath);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int nextWidth = 0;
        int count = 1;
        savePath = FileUtils.getStringPath(this.rootPath, savePath);
        for (int i = 0; i < imageWidth; i+=step) {
            nextWidth = (nextWidth + width) > imageWidth ? imageWidth : (nextWidth + width);
            for (int j = 0; j < imageHeight; j+=height) {
                String slicePath = FileUtils.getStringPath(savePath, FilenameUtils.removeExtension(new File(filePath).getName()))
                        + "_slice_" + count + "." + FilenameUtils.getExtension(new File(filePath).getName());
                File slice = new File(slicePath);
                if (slice.exists()) {
                    return true;
                }
            }
        }
        return false;
    }


    public void addEndTimeOneDay(FileSearchParam fileSearchParam){
        Date endTime = fileSearchParam.getEndTime();
        if (endTime == null)
            return;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE,1);
        fileSearchParam.setEndTime(calendar.getTime());
    }

}
