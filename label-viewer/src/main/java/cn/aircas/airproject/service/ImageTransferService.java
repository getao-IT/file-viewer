package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.FileTransferInfo;
import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.utils.date.DateUtils;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageInfo;
import cn.aircas.utils.image.ParseImageInfo;
import cn.aircas.utils.image.slice.CreateThumbnail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author vanishrain
 * 影像文件传输服务
 */
@Slf4j
@Service("IMAGE-TRANSFER-SERVICE")
public class ImageTransferService extends AbstractFileTypeTransferService<Image> {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Value("${sys.rootPath}")
    private String rootPath;

    @Override
    public String[] getSupportFileType() {
        return new String[]{"jpg", "JPG", "tiff", "tif", "png", "PNG", "TIF","TIFF","jpeg"};
    }

    @Override
    public Image parseFileInfo(String filePath) {
        File imageFile = new File(filePath);

        String thumbnailPath = FileUtils.getStringPath(imageFile.getParentFile().getAbsolutePath(),
                "thumbnail_" + FilenameUtils.removeExtension(imageFile.getName()) + ".jpg");
        String fileSize = FileUtils.fileSizeToString(imageFile.length());
        String thumbnail = CreateThumbnail.createBase64Thumbnail(filePath, thumbnailPath, 256);

        String relativeFilePath = filePath.substring(this.rootPath.length());
        if (relativeFilePath.startsWith("/"))
            relativeFilePath = relativeFilePath.substring(1);
        ImageInfo imageInfo = ParseImageInfo.parseInfo(filePath);
        Image image = null;
        try {
            image = Image.builder().imageName(imageFile.getName()).createTime(DateUtils.nowDate()).path(relativeFilePath)
                    .thumb(thumbnail).size(fileSize).fileLength(imageFile.length()).minProjectionX(imageInfo.getProjectionRange()[0])
                    .minProjectionY(imageInfo.getProjectionRange()[1]).maxProjectionX(imageInfo.getProjectionRange()[2])
                    .maxProjectionY(imageInfo.getProjectionRange()[3]).delete(false).build();
        } catch (Exception e) {
            log.error("文件 {} 影像信息解析失败");
            return null;
        }
        BeanUtils.copyProperties(imageInfo,image);

        return image;
    }

    @Override
    public Image transferFromWeb(String fileRelativePath, FileTransferInfo fileTransferInfo) {
        return null;
    }

    @Override
    public List<Image> transferFromBackend(String srcDir, String destDir, FileTransferInfo fileTransferInfo) {
        return null;
    }
}
