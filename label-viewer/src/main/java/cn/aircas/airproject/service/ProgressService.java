package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import com.alibaba.fastjson.JSONObject;
import java.util.List;



/**
 * 进度管理接口类
 */
public interface ProgressService {

    List<ProgressContr> getAllTaskById(ProgressContrDto pcd);

    ProgressContr createTaskById(ProgressContrDto pcd);

    int deleteProgress(String taskId, String filePath);

    int batchDeleteProgress(ProgressContrDto pcd);

    JSONObject statisProgressByStatus(ProgressContrDto pcd);

}
