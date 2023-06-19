package cn.aircas.airproject.entity.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class FileInfo {

    /**
     * 名称
     */
    @ApiModelProperty(value = "文件名称", required = true)
    private String name;

    /**
     * 类型
     */
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    /**
     * 位置
     */
    @ApiModelProperty(value = "文件位置")
    private String location;

    /**
     * 大小
     */
    @ApiModelProperty(value = "文件大小")
    private long fileSize;

    /**
     * 最后修改时间
     */
    @ApiModelProperty(value = "最后修改时间")
    private Date modifyTime;

    /**
     * 包含文件夹和文件个数
     */
    @ApiModelProperty(value = "包含的文件夹个数")
    private int inFolderNum;
    @ApiModelProperty(value = "包含的文件个数")
    private int inFileNum;

    /**
     * 文件操作权限
     */
    @ApiModelProperty(value = "文件操作权限")
    private String permissions;

    /**
     * 属组ID
     */
    @ApiModelProperty(value = "文件属组ID")
    private int GroupId;

    /**
     * 所有者ID
     */
    @ApiModelProperty(value = "文件所有者ID")
    private int UserId;

    /**
     * 写入文件参数
     */
    @ApiModelProperty(value = "文件路径")
    private String path;
    @ApiModelProperty(value = "要修改的文件内容")
    private String content;
    @ApiModelProperty(value = "为TRUE时，表示追加内容到文件，反之，覆盖源文件内容")
    private boolean append;
}
