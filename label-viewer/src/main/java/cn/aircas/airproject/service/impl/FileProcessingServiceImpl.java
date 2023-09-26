package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.ImageTransferService;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class FileProcessingServiceImpl implements FileProcessingService {
    @Value("${sys.rootPath}")
    public String rootPath;

    @Autowired
    ImageTransferService imageTransferService;

    @Override
    public Integer formatConverter(int fileId, String filePath, String outputPath, String format, String source, String keywords, boolean isPublic) {
        int code = 0;
        //Image srcimage = this.imageMapper.selectById(fileId);
        Image srcimage = this.imageTransferService.parseFileInfo(filePath);
        String inputPath = FileUtils.getStringPath(this.rootPath,srcimage.getPath());
        /*File outputParentPath = srcimage.isPublic() ? FileUtils.getFile(this.rootPath, "file-data","image", System.currentTimeMillis()) :
                FileUtils.getFile(this.rootPath, "user", srcimage.getUserId(), "file-data", "image",System.currentTimeMillis());*/

        File outputParentPath = FileUtils.getFile(this.rootPath, outputPath);
        if (!outputParentPath.exists()){
            outputParentPath.mkdirs();
        }
        String path = null;
        try {
            path = ImageFormat.formatConvertor(inputPath, outputParentPath.getPath(), format);
        }catch (Exception e){
            code = 1;
        }

        // 外网平台不需要入库
        /*
        String filePath = FileUtils.getStringPath(path);
        Image image = this.imageTransferService.parseFileInfo(filePath);
        image.setKeywords(keywords);
        image.setSource(source);
        image.setPublic(isPublic);
        image.setDelete(false);
        image.setUserId(srcimage.getUserId());
        image.setUserName(srcimage.getUserName());
        this.imageMapper.insert(image);
        */

        log.info("影像格式转换成功");
        return code;
    }
}
