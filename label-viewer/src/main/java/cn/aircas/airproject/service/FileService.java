package cn.aircas.airproject.service;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.common.PageResult;
import cn.aircas.airproject.entity.domain.Slice;
import cn.aircas.airproject.entity.emun.FileType;
import cn.aircas.airproject.entity.emun.SourceFileType;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;



public interface FileService {
    List<String> getFileType();
    List<JSONObject> listFolderFiles(String path);
    PageResult<JSONObject> getContent(int pageSize, int pageNo, FileType fileType, int fileId);
    void makeImageSlice(Slice slice);
    void makeImageAllGeoSlice(Slice slice);
    String createDirs(String path);
    String createSlicePath(String path, String fileName);
    Boolean createSlicePaths(String path, String fileName, int width, int height, int step);
    CommonResult<String> rename(String srcPath, String destPath);
    boolean download(String filePath, HttpServletResponse response);
}
