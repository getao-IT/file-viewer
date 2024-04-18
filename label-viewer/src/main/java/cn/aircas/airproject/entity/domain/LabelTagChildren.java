package cn.aircas.airproject.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelTagChildren {

    private int id = -1;

    private int parent_id = -1;

    private String tag_name;

    private String parenttag_name;

    private String properties_name;

    private String properties_color;

}
