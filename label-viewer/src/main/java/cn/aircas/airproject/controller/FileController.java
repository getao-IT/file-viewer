package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.Slice;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.FileService;
import cn.aircas.airproject.utils.OpenCV;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
@Log("文件管理接口")
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    FileProcessingService fileProcessingService;


    @Log(value = "裁切影像指定位置得到切片图片")
    @PostMapping("/custom")
    public CommonResult<String> makeImageSlice(@RequestBody Slice slice) {
        this.fileService.makeImageSlice(slice);
        return new CommonResult<String>().success().message("裁切任务后台处理中...");
    }


    @Log(value = "根据宽高裁切影像所有位置得到切片图片")
    @PostMapping("/slice")
    public CommonResult<String> makeImageAllGeoSlice(@RequestBody Slice slice) {
        this.fileService.makeImageAllGeoSlice(slice);
        return new CommonResult<String>().success().message("裁切任务后台处理中...");
    }


    @Log(value = "返回默认保存位置")
    @GetMapping("/defaultPath")
    public CommonResult<String> defaultPath() {
        String restult = "/file-data/image_slice";
        return new CommonResult<String>().data(restult).success().message("返回默认保存位置");
    }


    /**
     * 获取子文件夹list
     * @param
     * @return
     */
    @Log(value = "获取子文件夹list")
    @GetMapping("/folder")
    public CommonResult<List<JSONObject>> getFolderList(String path) {
        List<JSONObject> folderList = fileService.listFolderFiles(path);
        return new CommonResult<List<JSONObject>>().data(folderList).success().message("获取子文件夹数据成功");
    }


    /**
     * 创建文件夹
     * @param
     * @return
     */
    @Log(value = "创建文件夹")
    @GetMapping("/createDirs")
    public CommonResult<String> createDirs(String path) {
        String result = fileService.createDirs(path);
        return new CommonResult<String>().data(result).success().message("创建文件夹成功");
    }


    /**
     * 指定位置创建切片保存路径
     * @param path 选择的位置 加上填写的名称
     * @param fileName 文件名称
     * @return
     */
    @Log("创建切片保存路径")
    @GetMapping("/createSlicePath")
    public CommonResult<String> createSlicePath(String path, String fileName) {
        String result = fileService.createSlicePath(path, fileName);
        return new CommonResult<String>().success().data(result).message("执行创建操作成功");
    }


    /**
     * 按照宽高创建切片保存路径
     * @param savePath 选择的位置
     * @param filePath 文件路径
     * @param width 切片宽度
     * @param height 切片高度
     * @param step 步长
     * @return
     */
    @Log("创建切片保存路径")
    @GetMapping("/createSlicePaths")
    public CommonResult<Boolean> createSlicePaths(String savePath, String filePath, int width, int height, int step) {
        Boolean result = fileService.createSlicePaths(savePath, filePath, width, height, step);
        return new CommonResult<Boolean>().success().data(result).message("获取是否有重复文件");
    }


    /**
     * 重命名文件、文件夹
     * @param srcPath
     * @param destPath
     * @return
     */
    @Log("重命名")
    @PutMapping("/rename")
    public CommonResult<String> rename(String srcPath, String destPath) {
        return fileService.rename(srcPath, destPath);
    }


    /**
     * 下载文件
     * @param filePath 文件路径
     * @param response 响应
     * @return
     */
    @Log("重命名")
    @GetMapping ("/download")
    public CommonResult<String> download(String filePath, HttpServletResponse response) {
        boolean result = fileService.download(filePath, response);
        return new CommonResult<String>().success().message(result ? "文件下载成功" : "文件下载失败");
    }

    @Log(value = "图片格式转换")
    @ApiOperation("图片格式转换")
    @PostMapping("/formatConverter")
    public CommonResult<String> formatConverter(String progressId, String filePath, String format) {
        Integer code = this.fileProcessingService.formatConverter(progressId, filePath, format);
        return new CommonResult<String>().success().data(String.valueOf(code)).message("格式转换后台进行中");
    }


    @Log(value = "图片灰度转换")
    @ApiOperation("图片灰度转换")
    @PostMapping("/greyConverter")
    public CommonResult<String> greyConverter(String src) {
        this.fileProcessingService.greyConverter(src, OpenCV.NormalizeType.MINMAX);
        return new CommonResult<String>().success().message("图片灰度转换成功");
    }
}
