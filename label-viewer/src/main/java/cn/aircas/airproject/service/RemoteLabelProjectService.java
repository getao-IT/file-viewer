package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.FileAndFolder;
import cn.aircas.airproject.entity.domain.FileInfo;
import cn.aircas.airproject.entity.domain.FileManagerParams;
import cn.aircas.airproject.entity.domain.ProgressInfo;
import com.jcraft.jsch.SftpException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface RemoteLabelProjectService {

    List<FileAndFolder> getFileAndFolderList(String path);

    void copyFileAndFolder(FileManagerParams params, String srcPath, String destPath);

    String deleteFolder(FileManagerParams params, String srcPath);

    String deleteFile(FileManagerParams params, String srcPath);

    boolean deleteFileOrFolder(FileManagerParams params, String srcPath);

    boolean fileRename(FileManagerParams params);

    FileInfo getFileInfo(FileManagerParams params, String path);

    boolean createFile(FileManagerParams params, String path);

    boolean createFolder(FileManagerParams params, String path);

    String getFileContent(FileManagerParams params, String path);

    boolean writeFile(FileManagerParams params, String path, String content, boolean b);

    CommonResult downLoadStepOne(String progressId, FileManagerParams params, String srcFile) throws SftpException;

    void upload(String progressId, InputStream srcFile, FileManagerParams params, String destPath, String fileName) throws SftpException;

    ProgressInfo getProgressById(String progressId);

    CommonResult downLoadStipTwo(String fileName, boolean isFile);

    void clearTempFile(String fileName);
}
