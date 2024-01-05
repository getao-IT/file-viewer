package cn.aircas.airproject.service;

import cn.aircas.airproject.callback.GrayConverCallback;
import cn.aircas.airproject.utils.OpenCV;

import java.text.ParseException;


public interface FileProcessingService {

    void formatConverter(String progressId ,String filePath, String outputPath, String format) throws ParseException;

    void grayConverter(String progressId, String src,String outPutPath);

    void opencvGrayConverter(String progressId, String src, String dst, OpenCV.NormalizeType type, GrayConverCallback callback);
}
