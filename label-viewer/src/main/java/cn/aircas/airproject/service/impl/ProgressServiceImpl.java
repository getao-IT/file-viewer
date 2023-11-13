package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.ImageUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
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
    public ProgressContr createTaskById(ProgressContrDto pcd) {
        ProgressContr progressContr = new ProgressContr();
        BeanUtils.copyProperties(pcd, progressContr);
        ImageUtil.progresss.get(pcd.getTaskId()).add(progressContr);
        return progressContr;
    }


    @Override
    public int deleteProgress(String taskId, String filePath) {
        List<ProgressContr> progressContrs = ImageUtil.progresss.get(taskId);
        for (ProgressContr progressContr : progressContrs) {
            if (progressContr.getFilePath().equalsIgnoreCase(filePath)) {
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
