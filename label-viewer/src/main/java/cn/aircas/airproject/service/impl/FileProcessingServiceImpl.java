package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.callback.impl.GrayConverCallbackImpl;
import cn.aircas.airproject.entity.domain.Image;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.ImageTransferService;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.ImageUtil;
import cn.aircas.airproject.utils.OpenCV;
import cn.aircas.utils.file.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;



@Slf4j
@Service
public class FileProcessingServiceImpl implements FileProcessingService {

    @Value("${sys.rootPath}")
    public String rootPath;

    @Autowired
    private ImageTransferService imageTransferService;


    /**
     * 图片格式转换
     * @param progressId
     * @param filePath
     * @param outputPath
     * @param format
     * @return
     */
    @Override
    @Async
    public void formatConverter(String progressId, String filePath, String outputPath, String format) { Image srcimage = this.imageTransferService.parseFileInfo(FileUtils.getStringPath(this.rootPath, filePath));
        String input = FileUtils.getStringPath(this.rootPath,srcimage.getPath());

        File outputParentPath = FileUtils.getFile(FileUtils.getStringPath(this.rootPath, outputPath));
        if (!outputParentPath.exists()){
            outputParentPath.mkdirs();
        }

        long taskStartTime = System.currentTimeMillis();
        ProgressContr progress = null;
        ProgressService service = new ProgressServiceImpl();

        try {
            Date startTime = DateUtils.parseDate(org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), new String[]{"yyyy-MM-dd HH:mm:ss"});
            progress = ProgressContr.builder().taskId(progressId).filePath(input).consumTime(0)
                    .fileName(new File(input).getName()).taskType(TaskType.CONVERTER).status(TaskStatus.WORKING)
                    .startTime(startTime).progress("0%").describe("格式转换中...").build();
            ProgressContr taskById = service.createTaskById(progress);
            log.info("创建传输任务成功：taskId {}， 任务类型 {}， [ {} ]", progressId, TaskType.CONVERTER, taskById);
            //ImageFormat.formatConvertor(input, outputParentPath.getAbsolutePath(), format);
            ImageUtil.formatConvertor(input, outputParentPath.getAbsolutePath(), format, progress);
            ProgressContrDto success = ProgressContrDto.builder().taskId(progressId).filePath(input).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("格式转换成功").status(TaskStatus.FINISH).progress("100%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(success);
            log.info("格式转换成功， 更新状态：{}, [ {} ]",i, success);
        }catch (Exception e){
            ProgressContrDto fail = ProgressContrDto.builder().taskId(progressId).filePath(input).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("格式转换失败").status(TaskStatus.FAIL).consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(fail);
            log.error("格式转换异常：{}，更新状态：{}, [ {} ]", e.getMessage(), i, fail);
        }
    }


    /**
     * 使用OpenCV进行图像灰度转换
     * @param src
     * @param dst
     * @param type
     * @param callback
     */
    @Override
    @Async
    public void opencvGrayConverter(String progressId, String src, String dst, OpenCV.NormalizeType type, GrayConverCallback callback) {
        long taskStartTime = System.currentTimeMillis();
        ProgressContr progress = null;
        ProgressService service = new ProgressServiceImpl();

        try {
            src = FileUtils.getStringPath(this.rootPath, src);
            dst = FileUtils.getStringPath(this.rootPath, dst, new File(src).getName());
            Date startTime = DateUtils.parseDate(org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), new String[]{"yyyy-MM-dd HH:mm:ss"});
            progress = ProgressContr.builder().taskId(progressId).filePath(src).consumTime(0)
                    .fileName(new File(src).getName()).taskType(TaskType.GRAY).status(TaskStatus.WORKING)
                    .startTime(startTime).progress("50%").describe("灰度转换中...").build();
            ProgressContr taskById = service.createTaskById(progress);
            log.info("创建传输任务成功：taskId {}， 任务类型 {}， [ {} ]", progressId, TaskType.CONVERTER, taskById);
            ImageUtil.opencvGrayConver(src, dst, type, callback);
            ProgressContrDto success = ProgressContrDto.builder().taskId(progressId).filePath(src).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("灰度转换成功").status(TaskStatus.FINISH).progress("100%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(success);
            log.info("灰度转换成功， 更新状态：{}, [ {} ]",i, success);
        } catch (Exception e) {
            ProgressContrDto fail = ProgressContrDto.builder().taskId(progressId).filePath(src).startTime(progress.getStartTime())
                    .endTime(new Date()).describe("灰度转换失败").status(TaskStatus.FAIL).consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(fail);
            log.error("灰度转换异常：{}，更新状态：{}, [ {} ]", e.getMessage(), i, fail);
        }
    }


    /**
     * 图片灰度转换
     * @param src  源文件路径
     * @param outPutPath  输出文件路径
     * @return
     */
    @Override
    @Async
    public void grayConverter(String progressId, String src, String outPutPath) {
        File file = new File(FileUtils.getStringPath(this.rootPath, src));
        File destFile = new File(FileUtils.getStringPath(this.rootPath, outPutPath, file.getName()));
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (destFile.exists()) {
            destFile = cn.aircas.airproject.utils.FileUtils.autoMakeIfFileRepeat(destFile);
        }

        long taskStartTime = System.currentTimeMillis();
        ProgressContr progress = null;
        ProgressService service = new ProgressServiceImpl();

        try {
            Date startTime = DateUtils.parseDate(org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), new String[]{"yyyy-MM-dd HH:mm:ss"});
            progress = ProgressContr.builder().taskId(progressId).filePath(file.getAbsolutePath()).consumTime(0)
                    .fileName(file.getName()).taskType(TaskType.GRAY).status(TaskStatus.WORKING)
                    .startTime(startTime).progress("0%").describe("灰度转换中...").build();
            ProgressContr taskById = service.createTaskById(progress);
            log.info("创建传输任务成功：taskId {}， 任务类型 {}， [ {} ]", progressId, TaskType.GRAY, taskById);
            BufferedImage image = ImageUtil.grayConver(file, new GrayConverCallbackImpl(progress));
            ProgressContrDto pct = ProgressContrDto.builder().taskId(progressId).filePath(file.getAbsolutePath()).startTime(progress.getStartTime())
                    .describe("文件写入中...").status(TaskStatus.WORKING).progress("99.99%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(pct);
            log.info("灰度文件写入中...， 更新状态：{}, [ {} ]",i, pct);

            ImageUtil.grayImageWrite(image, FilenameUtils.getExtension(file.getName()), destFile);
            pct = ProgressContrDto.builder().taskId(progressId).filePath(file.getAbsolutePath()).startTime(progress.getStartTime()).outputPath(destFile.getAbsolutePath().replace("/home/data", ""))
                    .endTime(new Date()).describe("文件灰度转换完成").status(TaskStatus.FINISH).progress("100%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            i = service.updateProgress(pct);
            log.info("灰度文件写入成功...， 更新状态：{}, [ {} ]",i, pct);
        } catch (Exception e) {
            ProgressContrDto pct = ProgressContrDto.builder().taskId(progressId).filePath(file.getAbsolutePath()).startTime(progress.getStartTime())
                    .describe("文件灰度转换失败").status(TaskStatus.FAIL).consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = service.updateProgress(pct);
            log.info("文件灰度转换异常， 更新状态：{}, [ {} ]",i, pct);
        }
    }


    /**
     * 构建传输进程信息
     * @param taskId
     * @param filePath
     * @param consumTime
     * @param fileName
     * @param taskType
     * @param status
     * @param startTime
     * @param progress
     * @param describe
     * @return
     */
    private ProgressContr builderProgressContr(String taskId, String filePath, long consumTime, String fileName,
                                               TaskType taskType, TaskStatus status, Date startTime, String progress,
                                               String describe) {
        return ProgressContr.builder().taskId(taskId).filePath(filePath).consumTime(consumTime)
                .fileName(fileName).taskType(taskType).status(status)
                .startTime(startTime).progress(progress).describe(describe).build();
    }
}
