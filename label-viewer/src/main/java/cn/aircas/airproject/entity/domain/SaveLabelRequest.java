package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.LabelPointType;
import lombok.Data;

@Data
public class SaveLabelRequest {
    private String label;
    private String savePath;
    private String imagePath;
    private LabelPointType labelPointType = LabelPointType.PIXEL;
}
