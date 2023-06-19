package cn.aircas.airproject.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("SaveLabelRequest")
@Data
public class SaveLabelRequest {

    @ApiModelProperty(value = "保存的label", required = true)
    private String label;
    @ApiModelProperty(value = "保存的路径", required = true)
    private String savePath;
    @ApiModelProperty(value = "image路径 ", required = true)
    private String imagePath;
}
