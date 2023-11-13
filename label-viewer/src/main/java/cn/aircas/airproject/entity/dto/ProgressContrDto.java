package cn.aircas.airproject.entity.dto;

import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;


/**
 * 进度管理实体类
 */
@Data
@Builder
@AllArgsConstructor
public class ProgressContrDto {

    private String taskId;

    private String filePath;

    private String fileName;

    private TaskType taskType;

    private String progress;

    private TaskStatus status;

    private Timestamp startTime;

    private Timestamp endTime;

    private long consumTime;
}
