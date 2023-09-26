package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.FileTransferInfo;
import cn.aircas.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractFileTypeTransferService<T> implements FileTypeTransferService<T> {
    @Value("${sys.rootPath}")
    public String rootPath;

    @Value("${sys.uploadRootPath}")
    public String uploadRootPath;

    public abstract String[] getSupportFileType();

    public abstract T parseFileInfo(String filePath);

    /**
     * 遍历并解析文件
     * @param srcDir
     * @param destDir
     * @param fileTransferInfo
     * @return
     */
    public List<T> traverseFile(String srcDir, String destDir, FileTransferInfo fileTransferInfo){
        List<T> fileInfoList = new ArrayList<>();
        File srcDirFile = FileUtils.getFile(this.rootPath,srcDir);
        List<File> fileList = null;
        if (fileTransferInfo.getIsCurrentOnly()) {
            fileList = Arrays.asList(Objects.requireNonNull(srcDirFile.listFiles((dir, name) -> FilenameUtils.isExtension(name, getSupportFileType()))));
        } else {
            List<String> fileListPath = new ArrayList<>();
            FileUtils.folderFiles(srcDirFile.getAbsolutePath(), fileListPath);
            fileList = fileListPath.stream().map(File::new).collect(Collectors.toList());
        }
        Assert.notEmpty(fileList, String.format("上传文件夹：%s 为空", fileList));
        String transferToken = fileTransferInfo.getTransferToken();
        FileBackendTransferProgressService.beginOneTransfer(srcDirFile.getAbsolutePath(), transferToken, fileList);

        int count = 0;
        for (File file : fileList) {
            String filePath = file.getAbsolutePath().replace(srcDirFile.getAbsolutePath() + "/", "");
            FileBackendTransferProgressService.beginOneFileTransfer(transferToken, filePath);
            String fileName = file.getName();
            String fileRelativeSavePath = FileUtils.getStringPath(destDir, fileName);
            String fileSavePath = FileUtils.getStringPath(this.rootPath, fileRelativeSavePath);

            if (fileTransferInfo.isCopy()){
                try {
                    FileUtils.copyFile(file, FileUtils.getFile(this.rootPath, fileRelativeSavePath));
                } catch (IOException e) {
                    FileBackendTransferProgressService.transferError(transferToken, filePath);
                    log.error("拷贝文件：{} 出错", file.getAbsolutePath());
                    continue;
                }
            }else {
                fileSavePath = file.getAbsolutePath();
            }

            T fileInfo = parseFileInfo(fileSavePath);
            if (fileInfo == null) {
                FileBackendTransferProgressService.finishOneTransfer(transferToken, filePath);
                FileBackendTransferProgressService.transferError(transferToken, filePath);
                continue;
            }
            BeanUtils.copyProperties(fileTransferInfo,fileInfo,"createTime");
            fileInfoList.add(fileInfo);
            count++;

            FileBackendTransferProgressService.finishOneTransfer(transferToken, filePath);
            log.info("上传进度：{}/{}", count, fileList.size());

        }
        return fileInfoList;
    }
}
