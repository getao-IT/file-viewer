package cn.aircas.airproject.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabelTagParent {

    private int id = -1;

    private String tag_name;

    private String tag_childrens;

}
