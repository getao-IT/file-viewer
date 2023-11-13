package cn.aircas.airproject.service;

import cn.aircas.airproject.utils.OpenCV;



public interface FileProcessingService {

    Integer formatConverter(String progressId ,String filePath, String outputPath, String format);

    void greyConverter(String src,String outPutPath, OpenCV.NormalizeType type);
}
