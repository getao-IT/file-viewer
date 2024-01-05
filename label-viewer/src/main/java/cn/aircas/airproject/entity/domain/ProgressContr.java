package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;



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

    private String outputPath;

    private String fileName;

    private TaskType taskType;

    private String progress;

    private TaskStatus status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private long consumTime;

    private String describe;
}
