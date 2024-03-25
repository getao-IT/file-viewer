package cn.aircas.airproject.entity.domain;

import lombok.Data;



@Data
public class LabelTagChildren {

    private int id = -1;

    private int parent_id;

    private String tag_name;

    private String properties_name;

    private String properties_color;

}
