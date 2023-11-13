package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.service.ProgressService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;



/**
 * 任务进度管理
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;


    @PostMapping("/createTaskById")
    @Log("创建处理任务接口")
    public CommonResult<ProgressContr> createTaskById(@RequestBody ProgressContrDto pcd) {
        ProgressContr pc = progressService.createTaskById(pcd);
        return new CommonResult<ProgressContr>().success().data(pc).message("创建处理任务成功");
    }


    @GetMapping("/getAllTaskById")
    @Log("获取任务进度详情")
    public CommonResult<List<ProgressContr>> getAllTaskById(ProgressContrDto pcd) {
        List<ProgressContr> allTaskById = progressService.getAllTaskById(pcd);
        return new CommonResult<List<ProgressContr>>().success().data(allTaskById).message("获取任务进度详情成功");
    }


    @DeleteMapping("/deleteProgress")
    @Log("删除任务进度接口")
    public CommonResult<Integer> deleteProgress(String taskId, String filePath) {
        int flag = progressService.deleteProgress(taskId, filePath);
        return new CommonResult<Integer>().success().data(flag).message("删除任务进度成功");
    }


    @DeleteMapping("/batchDeleteProgress")
    @Log("批量删除任务进度接口")
    public CommonResult<Integer> batchDeleteProgress(ProgressContrDto pcd) {
        int flag = progressService.batchDeleteProgress(pcd);
        return new CommonResult<Integer>().success().data(flag).message("批量删除任务进度成功");
    }


    @GetMapping("/statisProgressByStatus")
    @Log("统计任务进度接口")
    public CommonResult<JSONObject> statisProgressByStatus(ProgressContrDto pcd) {
        JSONObject statis = progressService.statisProgressByStatus(pcd);
        return new CommonResult<JSONObject>().success().data(statis).message("获取统计任务进度成功");
    }
}
