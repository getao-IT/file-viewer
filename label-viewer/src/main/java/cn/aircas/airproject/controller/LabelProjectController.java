package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.entity.emun.LabelPointType;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
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
        if (StringUtils.isBlank(json)) {
            return new CommonResult<String>().success().data(json).message("该影像无标注信息");
        }
        return new CommonResult<String>().success().data(json).message("从服务器打开xml文件成功!");
    }

    @Log("打开指定位置的标注文件")
    @PostMapping(value = "/viwLabelFile")
    public CommonResult<String> viewSelectedLabelFile(String imagePath, String labelPath, LabelFileType fileType, LabelFileFormat fileFormat) throws Exception {

        String json = labelProjectService.viewSelectedLabelFile(imagePath, labelPath, fileType, fileFormat);
        String msg = StringUtils.isBlank(json) ? "该影像无标注信息":"从服务器打开指定标注文件成功!";
        return new CommonResult<String>().success().data(json).message(msg);
    }


    @Log(value = "获取文件夹下的文件和子文件夹")
    @GetMapping("/fileAndFolder")
    public CommonResult<List<FileAndFolder>> getFileAndFolderList(String path){
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


    @Log(value = "保存标注信息")
    @PostMapping(value = "/saveAsLabel")
    public CommonResult<String> saveLabel(@RequestBody SaveLabelRequest saveLabelRequest) throws IOException {
        boolean result = labelProjectService.saveLabel(saveLabelRequest);
        if (!result)
            return new CommonResult<String>().data(JSONObject.toJSON(saveLabelRequest).toString()).fail().message("保存标注信息失败!");
        return new CommonResult<String>().data(null).success().message("保存标注信息成功!");
    }


    @Log(value = "保存标注信息")
    @PostMapping(value = "/saveLabel")
    public CommonResult<String> saveAsLabel(@RequestBody SaveLabelRequest saveLabelRequest) {
        String error = labelProjectService.saveAsLabel(saveLabelRequest);
        if (error != null) {
            return new CommonResult<String>().data(null).fail().message(error);
        }
        return new CommonResult<String>().data(null).success().message("保存标注信息成功!");
    }


    @PostMapping(value = "/importLabel")
    @Log("导入标注信息")
    public CommonResult<String> importLabel(String imagePath, LabelPointType labelPointType, MultipartFile file) {
//        labelPointType = LabelPointType.GEODEGREE;
        String labelInfo = labelProjectService.importLabel(imagePath, labelPointType, file);
        if (labelInfo == null) {
            return new CommonResult<String>().fail().message("导入标注信息失败");
        }
        return new CommonResult<String>().data(labelInfo).success().message("导入标注信息成功!");
    }


    @PostMapping(value = "/exportLabel")
    @Log("导出标注信息")
    public CommonResult<String> exportLabel(@RequestBody SaveLabelRequest labelRequest) {
        String result = labelProjectService.exportLabel(labelRequest);
        if (result == null) {
            return new CommonResult<String>().data(result).fail().message("导出标注信息失败");
        }
        return new CommonResult<String>().data(result).success().message("导出标注信息成功!");
    }


    @GetMapping(value = "/importTag")
    public CommonResult<List<String>> importTag(String tagFilePath) {
        List<String> tagList = labelProjectService.importTag(tagFilePath);
        return new CommonResult<List<String>>().data(tagList).success().message("导入标签文件成功!");
    }


    @GetMapping(value = "/hasOverviews")
    public CommonResult<Boolean> hasOverviews(String imagePath) {
        boolean hasOverview = labelProjectService.hasOverview(imagePath);
        return new CommonResult<Boolean>().data(hasOverview).success().message("判断文件是否包含金字塔成功");
    }


    @PostMapping(value = "/buildOverviews")
    public CommonResult<String> buildOverviews(String progressId, String imagePath) {
        labelProjectService.buildOverviews(progressId, imagePath);
        return new CommonResult<String>().success().message("影像文件创建金字塔成功");
    }
}
