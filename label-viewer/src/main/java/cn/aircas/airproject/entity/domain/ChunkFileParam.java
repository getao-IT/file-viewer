package cn.aircas.airproject.entity.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 分块上传实体类
 */
@Data
public class ChunkFileParam {

    private String taskId;

    private long size;

    private int chunk;

    private int chunkTotal;

    private MultipartFile file;

}
