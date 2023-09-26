package cn.aircas.airproject.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileBackendTransferProgressService {

    //private static Map<String, FileBackendTransferProgress> imageTransferProgressMap = new HashMap<>();

    public static void finishOneTransfer(String transferToken, String imageName){
       /* FileBackendTransferProgress fileBackendTransferProgress = imageTransferProgressMap.getOrDefault(transferToken,new FileBackendTransferProgress());
        int currentFinishedCount = fileBackendTransferProgress.getCurrentFinishedCount()+1;
        fileBackendTransferProgress.getFinishedFileNameMap().put(imageName, FileTransferStatus.FINISHED);
        fileBackendTransferProgress.setTransferringFileName(imageName);
        fileBackendTransferProgress.setCurrentFinishedCount(currentFinishedCount);*/
    }

    public static void beginOneTransfer(String srcDir, String transferToken, List<File> fileList){
        /*FileBackendTransferProgress fileBackendTransferProgress = new FileBackendTransferProgress();
        fileBackendTransferProgress.setTotalCount(fileList.size());
        Map<String, FileTransferStatus> transferFileMap = new ConcurrentHashMap<>();
        fileList.forEach(f -> transferFileMap.put(f.getAbsolutePath().replace(srcDir + "/", ""), FileTransferStatus.WAITING));
        fileBackendTransferProgress.setFinishedFileNameMap(transferFileMap);
        imageTransferProgressMap.put(transferToken,fileBackendTransferProgress);*/
    }

    public static void beginOneFileTransfer(String transferToken, String imagePath){
        /*FileBackendTransferProgress fileBackendTransferProgress = imageTransferProgressMap.getOrDefault(transferToken,new FileBackendTransferProgress());
        fileBackendTransferProgress.getFinishedFileNameMap().put(imagePath, FileTransferStatus.TRANSFERRING);
        fileBackendTransferProgress.setTransferringFileName(imagePath);*/
    }

    public static void transferError(String transferToken, String imagePath){
        /*FileBackendTransferProgress fileBackendTransferProgress = imageTransferProgressMap.getOrDefault(transferToken,new FileBackendTransferProgress());
        fileBackendTransferProgress.getFinishedFileNameMap().put(imagePath, FileTransferStatus.FAILED);*/
    }

    /*public static FileBackendTransferProgress getTransferProgress(String transferToken){
        return imageTransferProgressMap.getOrDefault(transferToken,new FileBackendTransferProgress());
    }*/
}
