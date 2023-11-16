package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.TaskType;
import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;



/**
 * 进度管理接口类
 */
public interface ProgressService {

    List<ProgressContr> getAllTaskById(ProgressContrDto pcd);

    ProgressContr createTaskById(ProgressContr pcd);

    int updateProgress(ProgressContrDto pc);

    int deleteProgress(String taskId, String filePath, Date startTime);

    int batchDeleteProgress(ProgressContrDto pcd);

    JSONObject statisProgressByStatus(ProgressContrDto pcd);

}
