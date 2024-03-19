package cn.aircas.airproject.controller;

import cn.aircas.airproject.config.aop.annotation.Log;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.DateUtils;
import cn.aircas.airproject.utils.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;



/**
 * 任务进度管理
 */
@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private HttpServletRequest request;


    @PostMapping("/createTaskById")
    @Log("创建处理任务接口")
    public CommonResult<ProgressContr> createTaskById(@RequestBody ProgressContr pcd) {
        pcd.setStartTime(new Date());
        pcd.setTaskId(HttpUtils.getClientIp(request));
        ProgressContr pc = progressService.createTaskById(pcd);
        return new CommonResult<ProgressContr>().success().data(pc).message("创建处理任务成功");
    }


    @GetMapping("/getAllTaskById")
    @Log("获取任务进度详情")
    public CommonResult<List<ProgressContr>> getAllTaskById(ProgressContrDto pcd) {
        String progressId = HttpUtils.getClientIp(request);
        pcd.setTaskId(progressId);
        List<ProgressContr> allTaskById = progressService.getAllTaskById(pcd);
        return new CommonResult<List<ProgressContr>>().success().data(allTaskById).message("获取任务进度详情成功");
    }


    @PutMapping("/updateProgress")
    @Log("更新任务进度接口")
    public CommonResult<Object> updateProgress(@RequestBody ProgressContrDto pc) {
        int flag = progressService.updateProgress(pc);
        if (flag == 0) {
            return new CommonResult<Object>().success().data(pc).message("更新任务进度成功");
        } else if (flag == 2) {
            return new CommonResult<Object>().fail().data(pc).message("缺少必须的更新参数");
        }
        return new CommonResult<Object>().fail().data(pc).message("更新任务进度失败");
    }


    @DeleteMapping("/deleteProgress")
    @Log("删除任务进度接口")
    public CommonResult<Integer> deleteProgress(String filePath, @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime) {
        String taskId = HttpUtils.getClientIp(request);
        int flag = progressService.deleteProgress(taskId, filePath, DateUtils.getHoursBeforDate(startTime, -8));
        if (flag == 0) {
            return new CommonResult<Integer>().success().data(flag).message("删除任务进度成功");
        } else if (flag == 2) {
            return new CommonResult<Integer>().fail().data(flag).message("缺少必须的更新参数");
        } else if (flag == 1) {
            return new CommonResult<Integer>().fail().data(flag).message("删除任务进失败");
        }
        return new CommonResult<Integer>().fail().data(flag).message("没有找到满足条件的任务");
    }


    @DeleteMapping("/batchDeleteProgress")
    @Log("批量删除任务进度接口")
    public CommonResult<Integer> batchDeleteProgress(ProgressContrDto pcd) {
        pcd.setTaskId(HttpUtils.getClientIp(request));
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
