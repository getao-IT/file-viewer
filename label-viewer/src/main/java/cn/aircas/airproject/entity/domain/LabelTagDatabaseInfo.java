package cn.aircas.airproject.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabelTagDatabaseInfo {

    private String ip;

    private String name;

    private String path;

    private Date createTime;

    private Date modifyTime;

}
