package cn.aircas.airproject.controller;


import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.entity.emun.ResultCode;
import cn.aircas.airproject.service.RemoteLabelProjectService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.sf.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import cn.aircas.airproject.service.LabelProjectService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/labelProject")
public class LabelProjectController {

    @Autowired
    LabelProjectService labelProjectService;

    @Autowired
    RemoteLabelProjectService remoteLabelProjectService;

    @ApiOperation("上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imagePath",dataType = "string",required = true,value = "路径",paramType = "query"),
            @ApiImplicitParam(name = "labelPointType",dataType = "LabelPointType",required = true,paramType = "query"),
            @ApiImplicitParam(name = "file",dataType = "MultipartFile",required = true,paramType = "query")
    })
    @Log("上传文件")
    @PostMapping(value = "/uploadFile")
    public CommonResult<String> uploadFile(String imagePath , LabelPointType labelPointType , MultipartFile file) throws Exception {
        String json = labelProjectService.uploadFile(imagePath, labelPointType ,file);
        return new CommonResult<String>().success(ResultCode.SUCCESS).data(json).message("上传文件成功!");
    }

    @ApiOperation("从服务器打开xml文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "imagePath",dataType = "string",required = true,value = "iamge路径",paramType = "query"),
            @ApiImplicitParam(name = "labelPointType",dataType = "LabelPointType",required = true,paramType = "query"),
            @ApiImplicitParam(name = "xmlPath",dataType = "String",required = true,value = "xml路径",paramType = "query")
    })
    @Log("从服务器打开xml文件")
    @PostMapping(value = "/viwXmlFile")
    public CommonResult<String> viewXmlFile(String imagePath , LabelPointType labelPointType , String xmlPath) throws Exception {
        String json = labelProjectService.viewXmlFile(imagePath, labelPointType ,xmlPath);
        return new CommonResult<String>().success(ResultCode.SUCCESS).data(json).message("从服务器打开xml文件成功!");
    }

    @ApiOperation("获取文件夹下的文件和子文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",required = true,value = "路径",paramType = "query")
    })
    @Log(value = "获取文件夹下的文件和子文件夹")
    @GetMapping("/fileAndFolder")
    public CommonResult<List<FileAndFolder>> getFileAndFoderList(String path){
        List<FileAndFolder> folderList = labelProjectService.getFileAndFolderList(path);
        if (folderList == null)
            return new CommonResult<List<FileAndFolder>>().data(new ArrayList<>()).success(ResultCode.FAIL_PERMISSION_DENIED).message("没有对该路径的访问权限!");
        else
            return new CommonResult<List<FileAndFolder>>().data(folderList).success(ResultCode.SUCCESS).message("获取文件和文件夹数据成功!");
    }

    @ApiOperation("获取文件夹下的子文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",required = true,value = "路径",paramType = "query")
    })
    @Log(value = "获取文件夹下的子文件夹")
    @GetMapping("/folder")
    public CommonResult<List<FolderPac>> getFolderList(String path){
        List<FolderPac> folderList = labelProjectService.getFolderList(path);
        return new CommonResult<List<FolderPac>>().data(folderList).success(ResultCode.SUCCESS).message("获取子文件夹数据成功!");
    }

    @ApiOperation("获取文件夹下的文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",required = true,value = "路径",paramType = "query")
    })
    @Log(value = "获取文件夹下的文件")
    @GetMapping("/file")
    public CommonResult<List<FilePac>> getFile(String path) throws Exception {
        List<FilePac> fileList = labelProjectService.getFileList(path);
        return new CommonResult<List<FilePac>>().data(fileList).success(ResultCode.SUCCESS).message("获取文件数据成功!");
    }

    @ApiOperation("解析图片")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",required = true,value = "路径",paramType = "query")
    })
    @Log(value = "解析图片")
    @GetMapping("/imageInfo")
    public CommonResult<ImageInfo> getImageInfo(String path){
        ImageInfo imageInfo = labelProjectService.getImageInfo(path);
        return new CommonResult<ImageInfo>().data(imageInfo).success(ResultCode.SUCCESS).message("解析图片成功!");
    }

/*
    @Log(value = "更新xml格式")
    @GetMapping("/updateXml")
    public CommonResult<String> updateXml(MultipartFile file) throws Exception {
        String json = labelProjectService.updateXml(file);
        return new CommonResult<String>().data(json).success().message("更新xml格式成功！");
    }
*/

    @ApiOperation("Xml转换为Json")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "multipartFile",dataType = "MultipartFile",required = true,paramType = "query")
    })
    @PostMapping(value = "/toJson")
    public String xmlToJson(MultipartFile multipartFile) throws IOException {
        String result = new String(multipartFile.getBytes());
        JSONObject jsonObject = new JSONObject();
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(result);
        jsonObject.put(multipartFile.getOriginalFilename(),xmlJSONObj.toString());
        return jsonObject.toString();
    }

    @ApiOperation("保存Label")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "saveLabelRequest",dataType = "SaveLabelRequest",required = true,paramType = "body")
    })
    @PostMapping(value = "/saveLabel")
    public CommonResult<String> saveLabel(@RequestBody SaveLabelRequest saveLabelRequest) throws IOException {
        labelProjectService.saveLabel(saveLabelRequest);
        return new CommonResult<String>().data(null).success(ResultCode.SUCCESS).message("保存标注信息成功!");
    }

    @ApiOperation("importTag")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tagFilePath",dataType = "string",value = "路径",required = true,paramType = "query")
    })
    @GetMapping(value = "/importTag")
    public CommonResult<List<String>> importTag(String tagFilePath) throws IOException {
        List<String> tagList = labelProjectService.importTag(tagFilePath);
        return new CommonResult<List<String>>().data(tagList).success(ResultCode.SUCCESS).message("导入标签文件成功!");
    }

    /**
     * 复制文件夹
     * @param srcPath 要复制的根路径后的路径
     * @param destPath 复制后的根路径后的路径
     * @return
     */
    @ApiOperation("复制文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "srcPath",dataType = "string",value = "要复制的根路径后的路径",required = true,paramType = "query"),
            @ApiImplicitParam(name = "destPath",dataType = "string",value = "复制后的根路径后的路径",required = true,paramType = "query")
    })
    @Log("复制文件")
    @PostMapping(value = "/copyFileAndFolder")
    public CommonResult<List<FileAndFolder>> copyFileAndFolder(String srcPath, String destPath) {
        labelProjectService.copyFileAndFolder(srcPath, destPath);
        return new CommonResult<List<FileAndFolder>>().data(null).success(ResultCode.SUCCESS).message("复制文件夹或文件成功！");
    }

    /**
     * 删除文件或文件夹
     * @param srcPath 根路径后的目标路径
     * @return
     */
    @ApiOperation("删除文件或文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "srcPath",dataType = "string",value = "根路径后的目标路径",required = true,paramType = "query")
    })
    @DeleteMapping(value = "/deleteFileOrFolder")
    public CommonResult<Boolean> deleteFileOrFolder(String srcPath) {
        boolean result = labelProjectService.deleteFileOrFolder(srcPath);
        return new CommonResult<Boolean>().data(result).success(ResultCode.SUCCESS).message(result ? "删除成功！" : "删除失败！");
    }

    /**
     * 重命名文件或文件夹
     * @param params 根路径后的原目标路径
     * @return
     */
    @ApiOperation("重命名文件或文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldName",dataType = "string",value = "根路径后的原目标路径",required = true,paramType = "query"),
            @ApiImplicitParam(name = "newName",dataType = "string",value = "根路径后的新目标路径",required = true,paramType = "query")
    })
    @PutMapping(value = "/fileRename")
    public CommonResult<String> fileRename(@RequestBody FileManagerParams params) {
        String result = labelProjectService.fileRename(params.getOldName(), params.getNewName());
        return new CommonResult<String>().data(result).success(ResultCode.SUCCESS).message(result);
    }

    /**
     * 获取属性信息
     * @param path
     * @return 跟路径后的目标文件路径
     */
    @ApiOperation("获取属性信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",value = "跟路径后的目标文件路径",required = true,paramType = "query")
    })
    @GetMapping(value = "/fileInfo")
    public CommonResult<FileInfo> getFileInfo(String path) {
        FileInfo fileInfo = labelProjectService.getFileInfo(path);
        return new CommonResult<FileInfo>().data(fileInfo).success(ResultCode.SUCCESS).message(fileInfo == null ? "路径不存在！" : "获取属性信息成功！");
    }

    /**
     * 创建文件
     * @param path
     * @return
     */
    @ApiOperation("创建文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",value = "创建文件路径",required = true,paramType = "query")
    })
    @PostMapping(value = "/createFile")
    public CommonResult<Boolean> createFile(String path) {
        boolean result = labelProjectService.createFile(path);
        CommonResult<Boolean> commonResult = new CommonResult<>();
        commonResult.data(null);
        if (result) {
            commonResult.success(ResultCode.SUCCESS);
        } else {
            commonResult.fail(ResultCode.FAIL);
        }
        return commonResult.message(result ? "创建文件成功！" : "创建文件失败！");
    }

    /**
     * 创建文件夹
     * @param path
     * @return
     */
    @ApiOperation("创建文件夹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",value = "创建文件路径",required = true,paramType = "query")
    })
    @PostMapping(value = "/createFolder")
    public CommonResult<Boolean> createFolder(String path) {
        boolean result = labelProjectService.createFolder(path);
        CommonResult<Boolean> commonResult = new CommonResult<>();
        commonResult.data(null);
        if (result) {
            commonResult.success(ResultCode.SUCCESS);
        } else {
            commonResult.fail(ResultCode.FAIL);
        }
        return commonResult.message(result ? "创建文件夹成功！" : "创建文件夹失败！");
    }

    /**
     * 获取文件内容
     * @param path
     * @return
     */
    @ApiOperation("获取文件内容")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path",dataType = "string",value = "文件路径",required = true,paramType = "query")
    })
    @Log("获取文件内容")
    @GetMapping(value = "/fileContent")
    public CommonResult<JSONObject> getFileContent(String path) {

        //String fileContent = labelProjectService.getContent(path); // 数组获取
        JSONObject result = labelProjectService.getFileContent(path); // 输出流获取 使用版
        //String fileContent = labelProjectService.getContentByBuffer(path); // 缓存读取
        //String fileContent = labelProjectService.getContentByPosition(path, 329); // 随机获取
        //String fileContent = labelProjectService.writeByPosition(path,676, 676, "88888"); // 随机写入
        CommonResult<JSONObject> commonResult = null;
        if (result.get("content") == null) {
            commonResult = new CommonResult<JSONObject>().fail(ResultCode.FAIL_GETFILECONTENT).data(result).message("获取文件内容失败！");
        } else if (result.get("content").equals("binary")) {
            commonResult = new CommonResult<JSONObject>().fail(ResultCode.FAIL_BINARY).data(null).message("不支持对二进制文件的编辑！");
        } else if (result.get("content").equals("pos")) {
            commonResult = new CommonResult<JSONObject>().fail(ResultCode.FAIL_BINARY).data(null).message("输入的字节范围有误！");
        } else {
            commonResult = new CommonResult<JSONObject>().success(ResultCode.SUCCESS).data(result).message("获取文件成功！");
        }
        return commonResult;
    }

    /**
     * 将内容写入文件
     * @param fileInfo 根路径后的目标文件路径\写入内容\是否追加
     * @return
     */
    @ApiOperation("将内容写入文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileInfo",dataType = "FileInfo",value = "根路径后的目标文件路径\\写入内容\\是否追加",required = true,paramType = "body")
    })
    @Log
    @PutMapping(value = "/writeFile")
    public CommonResult<String> writeFile(@RequestBody FileInfo fileInfo) {
        boolean result = labelProjectService.writeFile(fileInfo.getPath(), fileInfo.getContent(), false);
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.data(fileInfo.getContent());
        if (result) {
            commonResult.success(ResultCode.SUCCESS);
        } else {
            commonResult.fail(ResultCode.FAIL);
        }
        return commonResult.message(result ? "保存文件成功！" : "保存文件失败！");
    }

    @ApiOperation("下载文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "src_file_path",dataType = "string",value = "下载目标路径",required = true,paramType = "query")
    })
    @PostMapping("/download")
    public CommonResult<String> downLoad(String src_file_path) {
        CommonResult result = labelProjectService.downLoad(src_file_path);
        return result;
    }

    @ApiOperation("上传文件到服务器")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "srcFile",dataType = "MultipartFile",value = "源文件",required = true,paramType = "query"),
            @ApiImplicitParam(name = "destPath",dataType = "string",value = "服务器中的路径",required = true,paramType = "query")
    })
    @PostMapping("/upload")
    public CommonResult<String> upload(@RequestBody(required = true) MultipartFile srcFile, String destPath) throws IOException {
        String progressId = UUID.randomUUID().toString().replace("-", "");
        labelProjectService.upload(progressId, srcFile.getInputStream(), destPath, srcFile.getOriginalFilename());
        return new CommonResult<String>().data(progressId).success(ResultCode.SUCCESS).message("上传文件操作成功！");
    }


    /*@ApiOperation("分块上传文件")
    @PostMapping("/chunkUpload")
    public CommonResult<String> chunkUpload(MultipartFileParam param, HttpServletRequest request, HttpResponse response) {
        String progressId = UUID.randomUUID().toString().replace("-", "");
        // 判断前端表单格式是否支持文件上传
        boolean ismultipartContent = ServletFileUpload.isMultipartContent(request);
        if (!ismultipartContent) {
            return new CommonResult<String>().fail(ResultCode.FAIL).message("不支持的表单格式");
        }
        // 开始上传
        String taskId = labelProjectService.chunkUpload(param);
        return new CommonResult<String>().data(progressId).success(ResultCode.SUCCESS).message("上传文件操作成功！");
    }*/

}
