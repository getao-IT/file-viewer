package cn.aircas.airproject.entity.domain;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@ApiModel("ProgressSingleTon")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressInfo implements Serializable {
    private static final long serialVersionUID = 3749286754654165264L;

    // 任务标识
    private String progressId;

    // 传输速度
    private String transVelocity = "0";

    // 耗时 字符串
    private String consumTime = "0";

    // 耗时 单位秒
    private int second = 0;

    // 预计剩余时间
    private String remainTime = "0";

    // 文件大小
    private long fileSize;

    // 已传输大小
    private long transLength = 0;

    // 进度
    private String plan = "0";

    // 任务是否正常进行
    private boolean isNormal = true;

    // 是否传输完成
    private boolean isDone = false;

    // 上传传输大小
    @JSONField(serialize = false)
    private long lastTransLength;
}
