package cn.aircas.airproject.service;



public interface FileProcessingService {
    Integer formatConverter(int fileId, String filePath, String outputPath, String format, String source, String keywords, boolean isPublic);
}
