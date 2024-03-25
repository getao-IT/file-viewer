package cn.aircas.airproject.entity.dto;

import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import lombok.Data;

import java.util.List;


@Data
public class LabelTagDto {

    private int id = -1;

    private String tag_name;

    private String tag_childrens;

    private List<Object> tagChildrenValues;
}
