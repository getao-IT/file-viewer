package cn.aircas.airproject.entity.domain;

import lombok.Data;

import java.util.Date;

@Data
public class FilePac {
    private String name;
    private String path;
    private Date lastModified;
    private String extension;
}
