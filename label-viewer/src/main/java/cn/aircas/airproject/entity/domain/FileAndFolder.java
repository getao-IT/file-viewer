package cn.aircas.airproject.entity.domain;


import lombok.Data;

import java.util.Date;

@Data
public class FileAndFolder {
    private String name;
    private String path;
    private Boolean isFile;
    private String size;
    private Date lastModified;
    private String extension;
}
