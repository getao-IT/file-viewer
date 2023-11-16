package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.TaskType;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.ImageUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



/**
 * 进度管理实现类
 */
@Service
public class ProgressServiceImpl implements ProgressService {


    @Override
    public List<ProgressContr> getAllTaskById(ProgressContrDto pcd) {
        List<ProgressContr> result = new ArrayList<>();
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(pcd.getTaskId());
        if (pcd.getTaskType() == null && pcd.getStatus() == null) {
            return progressContrs;
        }
        for (ProgressContr pc : progressContrs) {
            if (pcd.getTaskType() != null && pcd.getStatus() != null) {
                if (pcd.getTaskType().getCode() == pc.getTaskType().getCode() && pcd.getStatus().getCode() == pc.getStatus().getCode()) {
                    result.add(pc);
                }
            }
            if (pcd.getTaskType() != null && pcd.getStatus() == null) {
                if (pcd.getTaskType().getCode() == pc.getTaskType().getCode()) {
                    result.add(pc);
                }
            }
            if (pcd.getTaskType() == null && pcd.getStatus() != null) {
                if (pcd.getStatus().getCode() == pc.getStatus().getCode()) {
                    result.add(pc);
                }
            }
        }
        return result;
    }


    @Override
    public ProgressContr createTaskById(ProgressContr pcd) {
        if (ImageUtil.progresss.get(pcd.getTaskId()) != null) {
            ImageUtil.progresss.get(pcd.getTaskId()).add(pcd);
        } else {
            List<ProgressContr> progresss = new ArrayList<>();
            progresss.add(pcd);
            ImageUtil.progresss.put(pcd.getTaskId(), progresss);
        }
        return pcd;
    }


    @Override
    public int updateProgress(ProgressContrDto pc) {
        if (StringUtils.isBlank(pc.getTaskId()) || StringUtils.isBlank(pc.getFilePath()) || pc.getStartTime() == null) {
            return 2;
        }
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(pc.getTaskId());
        if (progressContrs == null) {
            throw new RuntimeException("不存在ID " + pc.getTaskId() + " 为的任务");
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProgressContr progressContr : progressContrs) {
            if (progressContr.getFilePath().equalsIgnoreCase(pc.getFilePath()) && format.format(progressContr.getStartTime()).equals(format.format(pc.getStartTime()))) {
                //BeanUtils.copyProperties(pc, progressContr);
                if (pc.getStatus().getCode() == 1) {
                    progressContr.setProgress(pc.getProgress());
                }
                if (pc.getStatus().getCode() == 2) {
                    progressContr.setStatus(pc.getStatus());
                    progressContr.setProgress(pc.getProgress());
                    progressContr.setEndTime(pc.getEndTime());
                }
                if (pc.getStatus().getCode() == 3) {
                    progressContr.setStatus(pc.getStatus());
                    progressContr.setEndTime(pc.getEndTime());
                }

                progressContr.setConsumTime(pc.getConsumTime());
                progressContr.setDescribe(pc.getDescribe());

                return 0;
            }
        }
        return 1;
    }


    @Override
    public int deleteProgress(String taskId, String filePath, Date startTime) {
        if (StringUtils.isBlank(taskId) || StringUtils.isBlank(filePath) || startTime == null) {
            return 2;
        }
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(taskId);
        for (ProgressContr progressContr : progressContrs) {
            if (progressContr.getFilePath().equalsIgnoreCase(filePath) && progressContr.getStartTime().equals(startTime)) {
                progressContrs.remove(progressContr);
                return 0;
            }
        }
        return 1;
    }


    @Override
    public int batchDeleteProgress(ProgressContrDto pcd) {
        List<ProgressContr> deleteProgress = new ArrayList<>();
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(pcd.getTaskId());
        if (pcd.getTaskType() == null && pcd.getStatus() == null) {
            ImageUtil.progresss.remove(pcd.getTaskId());
            return 0;
        }

        for (ProgressContr pc : progressContrs) {
            if (pcd.getTaskType() != null && pcd.getStatus() != null) {
                if (pcd.getTaskType().getCode() == pc.getTaskType().getCode() && pcd.getStatus().getCode() == pc.getStatus().getCode()) {
                    deleteProgress.add(pc);
                }
            }
            if (pcd.getTaskType() != null && pcd.getStatus() == null) {
                if (pcd.getTaskType().getCode() == pc.getTaskType().getCode()) {
                    deleteProgress.add(pc);
                }
            }
            if (pcd.getTaskType() == null && pcd.getStatus() != null) {
                if (pcd.getStatus().getCode() == pc.getStatus().getCode()) {
                    deleteProgress.add(pc);
                }
            }
        }
        if (deleteProgress.size() != 0) {
            progressContrs.removeAll(deleteProgress);
            // 可能需要重新设置进度信息
            return 0;
        }

        return 1;
    }


    @Override
    public JSONObject statisProgressByStatus(ProgressContrDto pcd) {
        JSONObject result = new JSONObject();
        HashMap<String, Integer> objectObjectHashMap = new HashMap<>();
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(pcd.getTaskId());

        // 全部
        result.put("ALL", progressContrs.size());

        for (ProgressContr pc : progressContrs) {
            if (result.containsKey(pc.getTaskType().name())) {
                result.put(pc.getTaskType().name(), result.getIntValue(pc.getTaskType().name())+1);
            } else {
                result.put(pc.getTaskType().name(), 1);
            }
        }
        return result;
    }
}
