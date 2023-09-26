package cn.aircas.airproject.service;


import cn.aircas.airproject.entity.FileTransferInfo;

import java.util.List;

public interface FileTypeTransferService<T> {
    T transferFromWeb(String fileRelativePath, FileTransferInfo fileTransferInfo);
    List<T> transferFromBackend(String srcDir, String destDir, FileTransferInfo fileTransferInfo);
}
