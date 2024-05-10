package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.LabelFileType;
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
     * 保存的标注文件类型：xml、pic、shp
     */
    private LabelFileType labelFileType = LabelFileType.XML;

    /**
     * 保存标注文件的格式，不同标注文件类型有不同的格式：
     * xml：aircas、voc、vif
     * pic：jpg、png
     */
    private String labelFileFormat;

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
