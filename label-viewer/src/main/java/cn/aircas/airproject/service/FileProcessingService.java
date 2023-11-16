package cn.aircas.airproject.service;

import cn.aircas.airproject.utils.OpenCV;

import java.text.ParseException;


public interface FileProcessingService {

    Integer formatConverter(String progressId ,String filePath, String outputPath, String format) throws ParseException;

    void grayConverter(String progressId, String src,String outPutPath);
}
