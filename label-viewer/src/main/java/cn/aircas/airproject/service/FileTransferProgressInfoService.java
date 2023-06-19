package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.domain.FileTransferProgressInfo;
import cn.aircas.airproject.entity.domain.MultipartFileParam;
import com.baomidou.mybatisplus.extension.service.IService;


public interface FileTransferProgressInfoService extends IService<FileTransferProgressInfo>{
    void updateTransferedChunk(String fileMd5);
    boolean checkFileComplete(String fileMd5);
    void mergeChunk(MultipartFileParam param, String destPath);
}
