package cn.aircas.airproject.entity.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("FileManagerParams")
@Data
public class FileManagerParams implements Serializable {
    private static final long serialVersionUID = -7228994869729242019L;

    /**
     * 用于重命名
     */
    @ApiModelProperty(value = "旧文件名", required = true)
    private String oldName;
    @ApiModelProperty(value = "新文件名", required = true)
    private String newName;

    /**
     * 用于远程访问文件
     */
    /*private String host = "192.168.9.51";
    private int port = 22;
    private String userName = "dell";
    private String passWord = "dell123456";*/
    private String host;
    private int port = 22;
    private String userName;
    private String passWord;
}
