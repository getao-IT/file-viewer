package cn.aircas.airproject.controller;


import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.ProgressCont;
import cn.aircas.airproject.utils.ImageUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 任务进度管理
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {


    @GetMapping
    @Log("获取图片转换进度")
    public CommonResult<ProgressCont> getConvertProgress(String progressId) {
        ProgressCont result = ImageUtil.progressMaps.get(progressId) != null ? ImageUtil.progressMaps.get(progressId) : null;
        return new CommonResult<ProgressCont>().success().data(result).message("获取转换进度成功");
    }
}
