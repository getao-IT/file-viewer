package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.ImageTransferService;
import cn.aircas.airproject.utils.ImageUtil;
import cn.aircas.airproject.utils.OpenCV;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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
    @Async
    public Integer formatConverter(String progressId, String filePath, String format) {
        int code = 0;
        //Image srcimage = this.imageMapper.selectById(fileId);
        Image srcimage = this.imageTransferService.parseFileInfo(FileUtils.getStringPath(this.rootPath, filePath));
        String input = FileUtils.getStringPath(this.rootPath,srcimage.getPath());
        /*File outputParentPath = srcimage.isPublic() ? FileUtils.getFile(this.rootPath, "file-data","image", System.currentTimeMillis()) :
                FileUtils.getFile(this.rootPath, "user", srcimage.getUserId(), "file-data", "image",System.currentTimeMillis());*/

        String outputPath = input.replace(FilenameUtils.getExtension(input), format);
        File outputParentPath = FileUtils.getFile(new File(input).getParentFile());
        if (!outputParentPath.exists()){
            outputParentPath.mkdirs();
        }
        //String path = null;
        try {
            //ImageFormat.formatConvertor(input, outputParentPath.getAbsolutePath(), format);
            ImageUtil.formatConvertor(progressId, input, outputParentPath.getAbsolutePath(), format);
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


    /**
     * 图片灰度转换
     * @param src  源文件路径
     * @param type 归一化公式类型
     * @return
     */
    @Override
    public void greyConverter(String src, OpenCV.NormalizeType type) {
        //OpenCV.normalize(src, type);
        File file = new File(FileUtils.getStringPath(this.rootPath, src));
        ImageUtil.normalization(file);
    }

}
