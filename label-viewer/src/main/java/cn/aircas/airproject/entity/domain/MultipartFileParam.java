package cn.aircas.airproject.entity.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 分块上传实体类
 */
@Data
public class MultipartFileParam {

    // 任务ID
    private String taskId;

    // 文件MD5
    private String fileMd5;

    // 上传路径，暂定与之前保持一致即可
    private String destPath;

    // 上传文件大小
    private long fileSize;

    // 文件文件名称
    private String fileName;

    // 当前上传的第几块分片
    private int indexChunk;

    // 当前分片大小
    private long chunkSize;

    // 整个文件分为多少片
    private int chunkTotal;

    // 当前分片二进制流文件
    private MultipartFile file;

    private InputStream inputStream;

    public void setFile(MultipartFile file) {
        this.file = file;
        try {
            this.inputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
