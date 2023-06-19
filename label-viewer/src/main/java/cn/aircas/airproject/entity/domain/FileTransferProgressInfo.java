package cn.aircas.airproject.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "file_transfer_progress_info")
public class FileTransferProgressInfo {

    @TableId(value = "id",type = IdType.AUTO)
    private int id;


    /**
     * 总分片数量
     */
    private int chunks;

    /**
     * MD5
     */
    private String md5;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    /**
     * 已上传的分片数
     */
    private int transferredChunk;

    /**
     * 文件上传信息id
     */
    private int fileTransferId;
}
