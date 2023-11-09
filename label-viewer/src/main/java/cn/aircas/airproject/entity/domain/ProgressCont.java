package cn.aircas.airproject.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 进度管理实体类
 */
@Data
@Builder
@AllArgsConstructor
public class ProgressCont {

    private String progressId;

    private String filePath;

    private String progressValue;
}
