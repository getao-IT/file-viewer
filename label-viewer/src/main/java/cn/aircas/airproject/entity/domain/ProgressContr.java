package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;



/**
 * 进度管理实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressContr {

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
