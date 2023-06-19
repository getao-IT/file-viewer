package cn.aircas.airproject.entity.domain;


import lombok.Data;

import java.util.Date;

@Data
public class FileAndFolder {
    private String UUID;
    private String name;
    private String path;
    private Boolean isFile;
    private String fileType;
    private Date lastModified;
    private String extension;
    private long fileSize;
    private String attribute;
    private String owner;
    private String linkPath;
    private boolean linkPathIsFile;
}
