package cn.aircas.airproject.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiModel("ProgressSingleTon")
@Data
@AllArgsConstructor
public class ProgressResponseSingleTon implements Serializable {
    private static final long serialVersionUID = 5746653275492414672L;

    private static final Map<String, ProgressInfo> PROGRESSMAPS = new ConcurrentHashMap<>();

    // 获取传输进展响应单例对象
    public static Map<String, ProgressInfo> getInstance() {
        return PROGRESSMAPS;
    }
}
