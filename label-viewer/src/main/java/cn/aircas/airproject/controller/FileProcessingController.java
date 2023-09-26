package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.service.FileProcessingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件转换接口
 */
@RestController
@Api(tags = "文件转换接口")
@RequestMapping("/formatConverter")
@Slf4j
public class FileProcessingController {

    @Autowired
    FileProcessingService fileProcessingService;

    @Log(value = "图片格式转换")
    @ApiOperation("图片格式转换")
    @PostMapping()
    public CommonResult<String> formatConverter(int fileId, String filePath, String outputPath, String format, String source, String keywords, boolean isPublic) {
        Integer code = this.fileProcessingService.formatConverter(fileId, filePath, outputPath, format,source,keywords,isPublic);
        return new CommonResult<String>().success().data(String.valueOf(code)).message("格式转换成功");
    }
}
