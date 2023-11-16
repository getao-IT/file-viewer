package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.LabelPointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.aircas.airproject.service.LabelProjectService;
import java.io.IOException;
import java.util.List;



@RestController
@RequestMapping("/labelProject")
public class LabelProjectController {

    @Autowired
    LabelProjectService labelProjectService;


    @Log("上传文件")
    @PostMapping(value = "/uploadFile")
    public CommonResult<String> uploadFile(String imagePath , LabelPointType labelPointType , MultipartFile file) throws Exception {
        String json = labelProjectService.uploadFile(imagePath, labelPointType ,file);
        return new CommonResult<String>().success().data(json).message("上传文件成功!");
    }


    @Log("从服务器打开xml文件")
    @PostMapping(value = "/viwXmlFile")
    public CommonResult<String> viewXmlFile(String imagePath , LabelPointType labelPointType , String xmlPath) throws Exception {
        String json = labelProjectService.viewXmlFile(imagePath, labelPointType ,xmlPath);
        return new CommonResult<String>().success().data(json).message("从服务器打开xml文件成功!");
    }


    @Log(value = "获取文件夹下的文件和子文件夹")
    @GetMapping("/fileAndFolder")
    public CommonResult<List<FileAndFolder>> getFileAndFoderList(String path){
        List<FileAndFolder> folderList = labelProjectService.getFileAndFolderList(path);
        return new CommonResult<List<FileAndFolder>>().data(folderList).success().message("获取文件和文件夹数据成功!");
    }


    @Log(value = "获取文件夹下的子文件夹")
    @GetMapping("/folder")
    public CommonResult<List<FolderPac>> getFolderList(String path){
        List<FolderPac> folderList = labelProjectService.getFolderList(path);
        return new CommonResult<List<FolderPac>>().data(folderList).success().message("获取子文件夹数据成功!");
    }


    @Log(value = "获取文件夹下的文件")
    @GetMapping("/file")
    public CommonResult<List<FilePac>> getFile(String path) throws Exception {
        List<FilePac> fileList = labelProjectService.getFileList(path);
        return new CommonResult<List<FilePac>>().data(fileList).success().message("获取文件数据成功!");
    }


    @Log(value = "解析图片")
    @GetMapping("/imageInfo")
    public CommonResult<ImageInfo> getImageInfo(String path){
        ImageInfo imageInfo = labelProjectService.getImageInfo(path);
        return new CommonResult<ImageInfo>().data(imageInfo).success().message("解析图片成功!");
    }


    @PostMapping(value = "/saveLabel")
    public CommonResult<String> saveLabel(@RequestBody SaveLabelRequest saveLabelRequest) throws IOException {
        labelProjectService.saveLabel(saveLabelRequest);
        return new CommonResult<String>().data(null).success().message("保存标注信息成功!");
    }


    @PostMapping(value = "/saveAsLabel")
    public CommonResult<String> saveAsLabel(@RequestBody SaveLabelRequest saveLabelRequest) throws IOException {
        labelProjectService.saveAsLabel(saveLabelRequest);
        return new CommonResult<String>().data(null).success().message("保存标注信息成功!");
    }


    @GetMapping(value = "/importTag")
    public CommonResult<List<String>> importTag(String tagFilePath) throws IOException {
        List<String> tagList = labelProjectService.importTag(tagFilePath);
        return new CommonResult<List<String>>().data(tagList).success().message("导入标签文件成功!");
    }


    @GetMapping(value = "/hasOverviews")
    public CommonResult<Boolean> hasOverviews(String imagePath) throws IOException {
        boolean hasOverview = labelProjectService.hasOverview(imagePath);
        return new CommonResult<Boolean>().data(hasOverview).success().message("判断文件是否包含金字塔成功");
    }


    @PostMapping(value = "/buildOverviews")
    public CommonResult<String> buildOverviews(String progressId, String imagePath) {
        labelProjectService.buildOverviews(progressId, imagePath);
        return new CommonResult<String>().success().message("影像文件创建金字塔成功");
    }
}
