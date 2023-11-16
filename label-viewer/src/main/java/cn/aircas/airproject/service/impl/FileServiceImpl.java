package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.callback.impl.GrayConverCallbackImpl;
import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.common.PageResult;
import cn.aircas.airproject.entity.domain.FileSearchParam;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.domain.Slice;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.FileType;
import cn.aircas.airproject.entity.emun.SourceFileType;
import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import cn.aircas.airproject.service.FileService;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.utils.ImageUtil;
import cn.aircas.utils.file.FileUtils;
import cn.aircas.utils.image.ImageInfo;
import cn.aircas.utils.image.ParseImageInfo;
import cn.aircas.utils.image.slice.SliceGenerateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;



/**
 * @Vanishrain
 * 文件管理服务实现类
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${sys.uploadRootPath}")
    String uploadRootPath;

    @Value("${sys.rootPath}")
    String rootPath;

    @Autowired
    private ImageFileServiceImpl service;


    @Override
    public List<String> getFileType() {
        List<String> fileTypeList = new ArrayList<>();
        for (FileType fileType : FileType.values()) {
            fileTypeList.add(fileType.getValue().toUpperCase());
        }
        return fileTypeList;
    }


    /**
     * 列出文件夹下的文件夹
     * @param path
     * @return
     */
    @Override
    public List<JSONObject> listFolderFiles(String path) {
        path = StringUtils.isBlank(path) ? File.separator : path;
        path = FilenameUtils.normalizeNoEndSeparator(this.uploadRootPath + File.separator + path);
        File[] files = new File(path).listFiles();

        if (files == null){
            log.error("路径：{} 不存在",path);
            return new ArrayList<>();
        }

        // 过滤掉文件路径，只显示文件夹
        /*List<String> filePathList = Arrays.stream(files)
                .filter(file -> file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toList());*/

        List<JSONObject> filePathList = Lists.newArrayList();
        for (File file : files) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name" , file.getName());
            jsonObject.put("isFile" , !file.isDirectory());
            filePathList.add(jsonObject);
        }

        return filePathList;
    }


    @Override
    public PageResult<JSONObject> getContent(int pageSize, int pageNo, FileType fileType, int fileId) {
        return null;
    }


    /**
     * 裁切影像得到切片图片
     * @param slice
     */
    @Override
    @Async
    public void makeImageSlice(Slice slice) {
        service.makeImageGeoSlice(slice);
    }


    /**
     * 裁切影像得到切片图片
     * @Params slice
     * @return
     */
    @Override
    @Async
    public void makeImageAllGeoSlice(Slice slice) {
        ProgressService pservice = new ProgressServiceImpl();
        final long[] callBackTime = {System.currentTimeMillis()};
        long taskStartTime = callBackTime[0];
        String filePath = FileUtils.getStringPath(this.rootPath, slice.getImagePath());
        ProgressContr progress = null;

        try {
            Date startTime = DateUtils.parseDate(org.apache.http.client.utils.DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"), new String[]{"yyyy-MM-dd HH:mm:ss"});
            progress = ProgressContr.builder().taskId(slice.getProgressId()).filePath(filePath)
                    .consumTime(0).fileName(new File(filePath).getName()).taskType(TaskType.SLICE).status(TaskStatus.WORKING)
                    .startTime(startTime).progress("0%").describe("裁切任务进行中...").build();
            ProgressContr taskById = pservice.createTaskById(progress);
            log.info("创建裁切任务成功：taskId {}， 任务类型 {}， [ {} ]", slice.getProgressId(), TaskType.SLICE, taskById);
            service.makeImageAllGeoSlice(slice.getFileType(), slice.getImagePath(), slice.getWidth(), slice.getHeight(),
                    slice.getSliceInsertPath(), slice.getStep(), slice.getStorage(), slice.getRetainBlankSlice(),
                    slice.getTakeLabelXml(),slice.getCoordinateType(),new GrayConverCallbackImpl(progress));
            ProgressContrDto success = ProgressContrDto.builder().taskId(slice.getProgressId()).filePath(filePath)
                    .startTime(progress.getStartTime()).endTime(new Date()).describe("裁切成功").status(TaskStatus.FINISH)
                    .progress("100%").consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = pservice.updateProgress(success);
            log.info("裁剪任务执行成功， 更新状态：{}, [ {} ]",i, success);
        }catch (Exception e){
            ProgressContrDto fail = ProgressContrDto.builder().taskId(slice.getProgressId()).filePath(filePath)
                    .startTime(progress.getStartTime()).endTime(new Date()).describe("裁剪失败").status(TaskStatus.FAIL)
                    .consumTime(System.currentTimeMillis() - taskStartTime).build();
            int i = pservice.updateProgress(fail);
            log.error("裁剪任务执行异常：{}，更新状态：{}, [ {} ]", e.getMessage(), i, fail);
        }
    }


    /**
     * 创建文件夹
     * @param path
     * @return
     */
    @Override
    public String createDirs(String path) {
        path = FileUtils.getStringPath(rootPath, path);
        File file = new File(path);
        if (file.exists()) {
            return "文件夹已存在";
        }
        file.mkdirs();
        return path;
    }


    /**
     * 创建切片保存路径
     * @param path 选择的位置 加上填写的名称
     * @param fileName 文件名称
     * @return
     */
    @Override
    public String createSlicePath(String path, String fileName) {
        path = FileUtils.getStringPath(this.rootPath, path) + "." + FilenameUtils.getExtension(fileName);
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        return null;
    }


    /**
     * 创建切片保存路径
     * @param savePath 选择的位置
     * @param filePath 文件路径
     * @param width 切片宽度
     * @param height 切片高度
     * @param step 步长
     * @return
     */
    @Override
    public Boolean createSlicePaths(String savePath, String filePath, int width, int height, int step) {
        filePath = FileUtils.getStringPath(this.rootPath,filePath);
        ImageInfo image = ParseImageInfo.parseInfo(filePath);
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        int nextWidth = 0;
        int count = 1;
        savePath = FileUtils.getStringPath(this.rootPath, savePath);
        for (int i = 0; i < imageWidth; i+=step) {
            nextWidth = (nextWidth + width) > imageWidth ? imageWidth : (nextWidth + width);
            for (int j = 0; j < imageHeight; j+=height) {
                String slicePath = FileUtils.getStringPath(savePath, FilenameUtils.removeExtension(new File(filePath).getName()))
                        + "_slice_" + count + "." + FilenameUtils.getExtension(new File(filePath).getName());
                File slice = new File(slicePath);
                if (slice.exists()) {
                    return true;
                }
            }
        }
        return false;
    }


    public void addEndTimeOneDay(FileSearchParam fileSearchParam){
        Date endTime = fileSearchParam.getEndTime();
        if (endTime == null)
            return;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endTime);
        calendar.add(Calendar.DATE,1);
        fileSearchParam.setEndTime(calendar.getTime());
    }


    @Override
    public CommonResult<String> rename(String srcPath, String destPath) {
        String src = FileUtils.getStringPath(this.rootPath, srcPath);
        File srcFile = new File(src);
        String dest = FileUtils.getStringPath(this.rootPath, destPath);
        File destFile = new File(dest);
        if (destFile.exists()) {
            return new CommonResult<String>().setCode("500").data("destPath: "+destPath).message("文件名称已存在");
        }
        if (srcFile.renameTo(destFile)) {
            return new CommonResult<String>().success().data("destPath: "+destPath).message("重命名成功");
        }
        return new CommonResult<String>().setCode("500").data("destPath: "+destPath).message("重命名失败或没有操作权限");
    }

    @Override
    public boolean download(String filePath, HttpServletResponse response) {
        String downloadPath = FileUtils.getStringPath(this.rootPath, filePath);
        File file = new File(downloadPath);
        try {
            if (!file.getParentFile().exists()) {
                Files.createDirectory(Paths.get(file.getParent()));
            }
            // 下载服务器文件
            response.setContentType("application/octet-stream");
            response.setHeader("content-type","application/octet-stream");
            response.setHeader("Content-Disposition","attachment;filename=users_info.csv");
            response.setCharacterEncoding("UTF-8");
            BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len=inputStream.read(buffer))!= -1) {
                outputStream.write(buffer, 0, len);
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            // 删除临时文件
            //Files.delete(Paths.get(fileSavePath));
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException 文件不存在异常：{} ", e.getMessage());
            return false;
        } catch (IOException e) {
            log.error("IOException IO异常：{} ", e.getMessage());
            return false;
        }
        return true;
    }
}
