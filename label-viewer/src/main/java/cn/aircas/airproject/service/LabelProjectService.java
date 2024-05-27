package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.entity.emun.LabelPointType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;



@Service
public interface LabelProjectService {
    /**
     * 判断影像是否含有金字塔
     * @param imagePath
     * @return
     */
    boolean hasOverview(String imagePath);

    /**
     * 对影像创建金字塔
     * @param imagePath
     */
    void buildOverviews(String progressId, String imagePath);

    String uploadFile(String imagePath, LabelPointType labelPointType, MultipartFile file) throws Exception;

    List<FolderPac> getFolderList(String path);

    List<FileAndFolder> getFileAndFolderList(String path);

    List<FilePac> getFileList(String path) throws Exception;

    ImageInfo getImageInfo(String path);

    String updateXml(MultipartFile multipartFile) throws Exception;

    String viewXmlFile(String imagePath , LabelPointType labelPointType , String xmlPath) throws Exception;

    String viewSelectedLabelFile(String imagePath, String labelPath, LabelFileType fileType, LabelFileFormat fileFormat) throws Exception;

    boolean saveLabel(SaveLabelRequest saveLabelRequest) throws IOException;

    String saveAsLabel(SaveLabelRequest saveLabelRequest);

    List<String> importTag(String tagFilePath);

    String importLabel(String imagePath, LabelPointType labelPointType, MultipartFile file);

    String exportLabel(SaveLabelRequest labelRequest);
}
