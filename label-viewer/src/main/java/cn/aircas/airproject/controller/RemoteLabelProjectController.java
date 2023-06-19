package cn.aircas.airproject.controller;


import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.ResultCode;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.service.RemoteLabelProjectService;
import cn.aircas.airproject.utils.FtpUtils;
import com.jcraft.jsch.SftpException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.ServiceNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/rmtlabelProject")
@PropertySource(value = "classpath:/application.yml")
public class RemoteLabelProjectController {

    @Autowired
    LabelProjectService labelProjectService;

    @Autowired
    RemoteLabelProjectService remoteLabelProjectService;

    @Value("${sudo.password}")
    private String sudoPassword;


    @Log(value = "获取远程连接")
    @GetMapping("/getFtpConnection")
    public CommonResult<Boolean> getFtpConnection(FileManagerParams params){
        boolean result = FtpUtils.getSessionConnect(params.getHost(), params.getPort(), params.getUserName(), params.getPassWord());
        CommonResult<Boolean> commonResult = new CommonResult<Boolean>();
        if (!result) {
           return commonResult.data(null).success(ResultCode.FAIL)
                    .message("远程连接失败！");
        }
        return commonResult.data(null).success(ResultCode.SUCCESS)
                .message("远程连接成功！");
    }


    @Log(value = "断开远程连接")
    @DeleteMapping("/disConnect")
    public CommonResult<Boolean> disConnect(){
        FtpUtils.closeSession();
        return new CommonResult<Boolean>().data(null).success(ResultCode.SUCCESS)
                .message("远程连接已断开！");
    }


    @Log(value = "获取文件夹下的文件和子文件夹 ok")
    @GetMapping("/fileAndFolder")
    public CommonResult<List<FileAndFolder>> getFileAndFoderListFromR(String path){
        List<FileAndFolder> folderList = remoteLabelProjectService.getFileAndFolderList(path);
        CommonResult<List<FileAndFolder>> commonResult = new CommonResult<List<FileAndFolder>>();
        if (folderList == null) {
            return commonResult.data(null).success(ResultCode.FAIL)
                    .message("远程连接失败或没有访问权限！");
        }
        return commonResult.data(folderList).success(ResultCode.SUCCESS)
                .message("获取文件和文件夹数据成功!");
    }


    /**
     * 复制文件夹
     * @param srcPath 要复制的根路径后的路径
     * @param destPath 复制后的根路径后的路径
     * @return
     */
    @ApiOperation("复制文件 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "srcPath",dataType = "string",value = "要复制的根路径后的路径",required = true,paramType = "query"),
            @ApiImplicitParam(name = "destPath",dataType = "string",value = "复制后的根路径后的路径",required = true,paramType = "query")
    })


    @Log("复制文件")
    @PostMapping(value = "/copyFileAndFolder")
    public CommonResult<List<FileAndFolder>> copyFileAndFolderR(FileManagerParams params, String srcPath, String destPath) {
        remoteLabelProjectService.copyFileAndFolder(params, srcPath, destPath);
        return new CommonResult<List<FileAndFolder>>().data(null).success(ResultCode.SUCCESS).message("复制文件夹或文件成功！");
    }


    /**
     * 删除文件夹
     * @param srcPath 根路径后的目标路径
     * @return
     */
    @ApiOperation("删除文件夹 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "srcPath",dataType = "string",value = "根路径后的目标路径",required = true,paramType = "query")
    })
    @Log(value = "删除文件夹")
    @DeleteMapping(value = "/deleteFolder")
    public CommonResult<Boolean> deleteFolder(FileManagerParams params, String srcPath) {
        boolean result = remoteLabelProjectService.deleteFileOrFolder(params, srcPath);
        return new CommonResult<Boolean>().data(null).success(result ? ResultCode.SUCCESS : ResultCode.FAIL)
                .message(result ? "删除文件夹成功！" : "删除文件夹失败！");
    }


    /**
     * 删除文件
     * @param srcPath 根路径后的目标路径
     * @return
     */
    @ApiOperation("删除文件 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "srcPath",dataType = "string",value = "根路径后的目标路径",required = true,paramType = "query")
    })
    @Log(value = "删除文件")
    @DeleteMapping(value = "/deleteFile")
    public CommonResult<Boolean> deleteFile(FileManagerParams params, String srcPath) {
        boolean result = remoteLabelProjectService.deleteFileOrFolder(params, srcPath);
        return new CommonResult<Boolean>().data(null).success(result ? ResultCode.SUCCESS : ResultCode.FAIL)
                .message(result ? "删除文件成功！" : "删除文件失败！");
    }


    /**
     * 重命名文件或文件夹
     * @param params 根路径后的原目标路径
     * @return
     */
    @ApiOperation("重命名文件或文件夹 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "oldName",dataType = "string",value = "根路径后的原目标路径",required = true,paramType = "query"),
            @ApiImplicitParam(name = "newName",dataType = "string",value = "根路径后的新目标路径",required = true,paramType = "query")
    })
    @Log(value = "重命名文件或文件夹")
    @PutMapping(value = "/fileRename")
    public CommonResult<Boolean> fileRename(@RequestBody FileManagerParams params) {
        boolean result = remoteLabelProjectService.fileRename(params);
        return new CommonResult<Boolean>().data(null).success(result ? ResultCode.SUCCESS : ResultCode.FAIL)
                .message(result ? "重命名成功！" : "重命名成功失败！");
    }


    /**
     * 获取属性信息
     * @param path
     * @return 跟路径后的目标文件路径
     */
    @ApiOperation("获取属性信息 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "path",dataType = "string",value = "跟路径后的目标文件路径",required = true,paramType = "query")
    })
    @Log("获取文件属性")
    @GetMapping(value = "/fileInfo")
    public CommonResult<FileInfo> getFileInfo(FileManagerParams params, String path) {
        FileInfo fileInfo = remoteLabelProjectService.getFileInfo(params, path);
        return new CommonResult<FileInfo>().data(fileInfo).success(fileInfo == null ? ResultCode.FAIL_NO_SUCH_FILE : ResultCode.SUCCESS)
                .message(fileInfo == null ? "路径不存在！" : "获取属性信息成功！");
    }


    /**
     * 创建文件
     * @param path
     * @return
     */
    @ApiOperation("创建文件 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "path",dataType = "string",value = "创建文件路径",required = true,paramType = "query")
    })
    @Log("创建文件")
    @PostMapping(value = "/createFile")
    public CommonResult<Boolean> createFile(FileManagerParams params, String path) {
        boolean result = remoteLabelProjectService.createFile(params, path);
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
    @ApiOperation("创建文件夹 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "path",dataType = "string",value = "创建文件路径",required = true,paramType = "query")
    })
    @Log("创建文件夹")
    @PostMapping(value = "/createFolder")
    public CommonResult<Boolean> createFolderR(FileManagerParams params, String path) {
        boolean result = remoteLabelProjectService.createFolder(params, path);
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
    @ApiOperation("获取文件内容 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "path",dataType = "string",value = "文件路径",required = true,paramType = "query")
    })
    @Log("获取文件内容")
    @GetMapping(value = "/fileContent")
    public CommonResult<String> getFileContentR(FileManagerParams params, String path) {
        String fileContent = remoteLabelProjectService.getFileContent(params, path); // 输出流获取 使用版
        CommonResult<String> commonResult = null;
        if (fileContent == null) {
            commonResult = new CommonResult<String>().fail(ResultCode.FAIL_GETFILECONTENT).data(fileContent).message("获取文件内容失败！");
        } else if (fileContent.equals("binary")) {
            commonResult = new CommonResult<String>().fail(ResultCode.FAIL_BINARY).data(fileContent).message("不支持对二进制文件的编辑！");
        } else if (fileContent.equals("pos")) {
            commonResult = new CommonResult<String>().fail(ResultCode.FAIL_BINARY).data(fileContent).message("输入的字节范围有误！");
        } else {
            commonResult = new CommonResult<String>().success(ResultCode.SUCCESS).data(fileContent).message("获取文件内容成功！");
        }
        return commonResult;
    }


    /**
     * 将内容写入文件
     * @param fileInfo 根路径后的目标文件路径\写入内容\是否追加
     * @return
     */
    @ApiOperation("将内容写入文件 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "fileInfo",dataType = "FileInfo",value = "根路径后的目标文件路径\\写入内容\\是否追加",required = true,paramType = "body")
    })
    @Log("将内容写入文件")
    @PutMapping(value = "/writeFile")
    public CommonResult<String> writeFileR(@RequestBody FileInfo fileInfo, FileManagerParams params) {
        boolean result = remoteLabelProjectService.writeFile(params, fileInfo.getPath(), fileInfo.getContent(), false);
        CommonResult<String> commonResult = new CommonResult<>();
        commonResult.data(fileInfo.getContent());
        if (result) {
            commonResult.success(ResultCode.SUCCESS);
        } else {
            commonResult.fail(ResultCode.FAIL);
        }
        return commonResult.message(result ? "保存文件成功！" : "保存文件失败！");
    }


    @ApiOperation("下载文件 Step 1")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "src_file_path",dataType = "string",value = "下载目标路径",required = true,paramType = "query")
    })
    @Log("下载文件")
    @PostMapping("/downloadStepOne")
    public CommonResult<String> downLoadStepOne(FileManagerParams params, String src_file_path) throws SftpException {
        String progressId = UUID.randomUUID().toString().replace("-", "");
        // 权限验证
        String cmd = "sudo -S ls -l -d " + src_file_path;
        BufferedReader bufferedReader = FtpUtils.excuteShellAndOutput(FtpUtils.session, cmd, sudoPassword);
        try {
            String readLine = bufferedReader.readLine();
            String[] split = readLine.split(" +");
            String user = split[2];
            String group = split[3];
            char[] chmodArr = split[0].toCharArray();
            if (params.getUserName().equals(user) && chmodArr[1] != 'r' && !params.getUserName().equals("root")
                    || params.getUserName().equals(group) && chmodArr[4] != 'r' && !params.getUserName().equals("root")
                    || chmodArr[7] != 'r' && !params.getUserName().equals("root")) {
                return new CommonResult<String>().data(progressId).success(ResultCode.FAIL)
                        .message("权限拒绝：该用户没有对该路径 " + src_file_path + " 的操作权限");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        CommonResult result = remoteLabelProjectService.downLoadStepOne(progressId, params, src_file_path);
        return new CommonResult<String>().data(progressId).success(ResultCode.SUCCESS).message("下载文件操作成功！");
    }


    @Log("下载文件 Step 2")
    @PostMapping("/downLoadStipTwo")
    public CommonResult<String> downLoadStipTwo(String fileName, boolean isFile) throws SftpException {
        CommonResult result = remoteLabelProjectService.downLoadStipTwo(fileName, isFile);
        // 清空临时文件目录
        remoteLabelProjectService.clearTempFile(fileName);
        return result;
    }


    @ApiOperation("上传文件到服务器，并监听上传进度 ok")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "srcFile",dataType = "MultipartFile",value = "源文件",required = true,paramType = "query"),
            @ApiImplicitParam(name = "params",dataType = "FileManagerParams",value = "远程连接信息",required = true,paramType = "query"),
            @ApiImplicitParam(name = "destPath",dataType = "string",value = "服务器中的路径，包含上传文件名",required = true,paramType = "query")
    })
    @Log("上传文件到远程主机")
    @PostMapping("/upload")
    public CommonResult<Object> uploadR(@RequestBody MultipartFile srcFile, FileManagerParams params, String destPath) throws SftpException, IOException {
        String progressId = UUID.randomUUID().toString().replace("-", "");
        // 权限验证
        String cmd = "sudo -S ls -l -d " + destPath;
        BufferedReader bufferedReader = FtpUtils.excuteShellAndOutput(FtpUtils.session, cmd, sudoPassword);
        try {
            String readLine = bufferedReader.readLine();
            String user = readLine.split(" +")[2];
            if (!params.getUserName().equals("root") && !params.getUserName().equals(user)) {
                return new CommonResult<>().data(progressId).success(ResultCode.FAIL)
                    .message("权限拒绝：该用户没有对该路径 " + destPath + " 的操作权限");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // 异步上传文件
        remoteLabelProjectService.upload(progressId, srcFile.getInputStream(), params, destPath, srcFile.getOriginalFilename());
        return new CommonResult<>().data(progressId).success(ResultCode.SUCCESS).message("上传文件操作成功！");
    }


    @GetMapping("/getUploadProgress")
    @Log("获取文件上传进度")
    public CommonResult<ProgressInfo> getUploadProgress(String progressId) {
        ProgressInfo progressInfo = remoteLabelProjectService.getProgressById(progressId);
        return new CommonResult<ProgressInfo>().success(ResultCode.SUCCESS).data(progressInfo).message("获取上传进度成功");
    }

    @Log("分块上传文件到远程主机")
    @PostMapping("/chunkUploadFile")
    public CommonResult<Object> chunkUploadFile(ChunkFileParam param) throws IOException, ServiceNotFoundException {
        String result = labelProjectService.chunkUploadFile(param);
        return new CommonResult<>().success(ResultCode.SUCCESS).message("分块上传文件成功").data(result);
    }

    @Log("分块上传文件到远程主机")
    @PostMapping("/chunkUpload")
    public CommonResult<Object> chunkUpload(MultipartFileParam param, HttpServletRequest request) throws IOException, ServiceNotFoundException {
        boolean isMulitipart = ServletFileUpload.isMultipartContent(request);
        if (!isMulitipart) {
            return new CommonResult<>().fail(ResultCode.FAIL).message("不是MultipartContent类型请求，重新发送请求");
        }
        labelProjectService.chunkUploadFile(param);
        return new CommonResult<>().success(ResultCode.SUCCESS).message("分块上传文件成功").data(null);
    }

}
