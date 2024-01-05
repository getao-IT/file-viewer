package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.LabelPointType;
import lombok.Data;



@Data
public class SaveLabelRequest {

    /**
     * 标注信息
     */
    private String label;

    /**
     * 保存路径
     */
    private String savePath;

    /**
     * 影像路径
     */
    private String imagePath;

    /**
     * 原图坐标类型
     */
    private LabelPointType labelPointType = LabelPointType.PIXEL;

    /**
     * 目标转换的坐标类型
     */
    private LabelPointType targetPointType;

    /**
     * 标注XML路径
     */
    private String xmlPath;
}
