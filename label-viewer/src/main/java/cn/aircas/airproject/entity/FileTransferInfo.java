package cn.aircas.airproject.entity;

import cn.aircas.airproject.entity.emun.FileType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "file_transfer_info")
public class FileTransferInfo {
    /**
     * id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private int id;
    private int userId;
    private String source;
    private String userName;
    private String fileSaveDir;
    private String keywords;
    private int batchNumber;
    private Date createTime;
    private FileType fileType;
    @TableField(exist = false)
    private boolean copy = true;
    @TableField(exist = false)
    private String transferToken;
    @TableField(exist = false)
    private boolean createDataset;
    @TableField(exist = false)
    private String token;
    @TableField(exist = false)
    private Boolean isCurrentOnly;
    /**
     * 是否公开
     */
    private boolean isPublic;

    /**
     * 传感器类型
     */
    private String sensorType;

    /**
     * 卫星名称
     */
    private String satelliteName;
}
