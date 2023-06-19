package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.LabelPointType;
import com.jcraft.jsch.SftpException;
import net.sf.json.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.management.ServiceNotFoundException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface LabelProjectService {
    String uploadFile(String imagePath, LabelPointType labelPointType, MultipartFile file) throws Exception;
    List<FolderPac> getFolderList(String path);
    List<FileAndFolder> getFileAndFolderList(String path);
    List<FilePac> getFileList(String path) throws Exception;
    ImageInfo getImageInfo(String path);
    String updateXml(MultipartFile multipartFile) throws Exception;
    String viewXmlFile(String imagePath , LabelPointType labelPointType , String xmlPath) throws Exception;
    void saveLabel(SaveLabelRequest saveLabelRequest) throws IOException;
    List<String> importTag(String tagFilePath);
    void copyFileAndFolder(String srcPath, String destPath);
    boolean deleteFileOrFolder(String srcPath);
    String fileRename(String oldName, String newName);
    FileInfo getFileInfo(String path);
    boolean createFile(String path);
    boolean createFolder(String path);
    JSONObject getFileContent(String path);
    String getContent(String path);
    String getContentByBuffer(String path);
    String getContentByPosition(String path, long startPos);
    String writeByPosition(String path, long startPos, long endPos, String content);
    boolean writeFile(String path, String content, boolean append);
    CommonResult downLoad(String src_file_path);
    void upload(String progressId, InputStream inputStream, String destPath, String fileName) throws IOException;

    String chunkUploadFile(ChunkFileParam param) throws IOException, ServiceNotFoundException;
    void chunkUploadFile(MultipartFileParam param);
}
