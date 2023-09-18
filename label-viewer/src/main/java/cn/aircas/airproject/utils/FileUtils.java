package cn.aircas.airproject.utils;

import cn.aircas.airproject.config.aop.FileTransTimerTask;
import cn.aircas.airproject.entity.domain.ProgressInfo;
import cn.aircas.airproject.entity.domain.ProgressResponseSingleTon;
import cn.aircas.airproject.service.impl.ProgressMonitorImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.json.XML;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sun.misc.Cleaner;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 文件md5值
 */
@Slf4j
@Component
public class FileUtils {

    @Value("#{'${value.allow-access.air-studio}'.split(',')}")
    private List<String> airStudioAccess;

    @Value("#{'${value.allow-access.air-pai}'.split(',')}")
    private List<String> airPaiAccess;

    private static final double KB = 1024D;
    private static final double MB = 1024 * 1024D;
    private static final double GB = 1024 * 1024 * 1024D;
    private static Map<String, ProgressInfo> progressSingleTon = ProgressResponseSingleTon.getInstance(); // 进度单例类
    private static Timer timer;

    /**
     * 封装paths方法，使任意类型均可转换为string
     * @param first 第一个值
     * @param more 其它值
     * @return
     */
    public static Path getPath(Object first, Object... more){
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first),strMore);
    }

    /**
     * 封装paths方法，使任意类型均可转换为string
     * @param first 第一个值
     * @param more 其它值
     * @return
     */
    public static String getStringPath(Object first, Object... more){
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first),strMore).toString();
    }

    /**
     * 封装paths方法，使任意类型均可转换为string
     * @param first 第一个值
     * @param more 其它值
     * @return
     */
    public static File getFile(Object first, Object... more){
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first),strMore).toFile();
    }

    /**
     * 读取文件
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String filePath){
        StringBuilder content = new StringBuilder();
        try(RandomAccessFile randomAccessFile = new RandomAccessFile(filePath,"r")){
            String line;
            while((line = randomAccessFile.readLine())!=null)
                content.append(new String(line.getBytes("ISO-8859-1"),"utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    /**
     * 拷贝文件
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        org.apache.commons.io.FileUtils.copyFile(srcFile,destFile);
    }


    /**
     * 拷贝文件到某一文件夹
     * @param srcFile
     * @param destDir
     * @throws IOException
     */
    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile,destDir);
    }

    /**
     * xml转json
     * @param filePath
     * @return
     */
    public static JSONObject XMLToJSON(String filePath){
        String content = FileUtils.readFile(filePath);
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(content);
        return JSON.parseObject(xmlJSONObj.toString());
    }

    /**
     * 将文件输入流转为File
     * @param in
     * @param filePath
     * @return
     */
    public static File inputStreamToFile (InputStream in, String filePath) {
        File file = new File(filePath);
        try {
            FileOutputStream out = new FileOutputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }



    public static void writeListToFile(Set<String> list, File destFile) throws IOException {
        if (!destFile.exists())
            destFile.createNewFile();
        if (list.size()==0)
            return;

        FileWriter fileWriter = new FileWriter(destFile,true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (String value : list) {
            bufferedWriter.write(String.valueOf(value));
            bufferedWriter.newLine();
        }

        bufferedWriter.flush();
        bufferedWriter.close();
        fileWriter.close();
    }

    /**
     * 删除文件夹
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
    }


    /**
     * 获取路径的文件大小
     * @param file
     * @return
     */
    public static long getFileLength(File file) {
        if (file.isFile()) {
            return file.length();
        }
        long total = 0l;
        File[] files = file.listFiles();
        if (files != null && files.length != 0) {
            for (File childrenFile : files) {
                total += getFileLength(childrenFile);
            }
        }
        return total;
    }


    /**
     * 返回路径中包含的文件个数
     * @param file
     * @return
     */
    public static int getInFileNum(File file) {
        int num = 0;
        if (file.isFile()) {
            return 1;
        }
        for (File children : file.listFiles()) {
            num += getInFileNum(children);
        }
        return num;
    }

    /**
     * 返回包含的文件夹个数
     * @param file
     * @return
     */
    public static int getInFolderNum(File file) {
        int num = 1;
        for (File children : file.listFiles()) {
            if (children.isDirectory()) {
                num += getInFolderNum(children);
            }
        }
        return num;
    }

    /**
     * 返回不含转义字符的字符串
     * @param str 目标字符串
     * @return
     */
    public static String getUnTropeStr(String str) {
        String[] pattern = {"\n", "\r", "\t", "\r\n", "\f"};
        for (String s : pattern) {
            switch (s) {
                case "\n":
                    str = str.replaceAll(s," ");
                    break;
                case "\r":
                    str = str.replaceAll(s," ");
                    break;
                case "\t":
                    str = str.replaceAll(s," ");
                    break;
                case "\r\n":
                    str = str.replaceAll(s," ");
                    break;
                case "\f":
                    str = str.replaceAll(s," ");
                    break;
            }
        }
        return str;
    }

    /**
     * 判断是否为二进制文件
     * @param file
     * @return
     */
    public static boolean isBinary(File file) {
        boolean flag = false;
        long len = file.length();
        try {
            FileInputStream in = new FileInputStream(file);
            for (long i = 0; i < len; i++) {
                int read = in.read();
                if (read < 32 && read != 9 && read != 10 && read != 13) {
                    flag = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 上传文件到服务器 MultipartFile类型文件
     * @param progressInfo 进度
     * @param inputStream 源文件
     * @param destPath 上传路径
     * @param fileName 文件名
     * @return
     */
    public static boolean uploadFile (ProgressInfo progressInfo, InputStream inputStream, String destPath, String fileName) {
        File targetDir = new File(destPath);
        // 创建上传文件夹
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        // 如果存在，先删除
        File destFile = new File(targetDir + File.separator + fileName);
        if (destFile.exists()) {
            destFile.delete();
        }

        try {
            log.info("--------------------- 远程上传文件 {} start --------------- ", progressInfo.getProgressId());
            // 存储进度对象
            long transLength = 0;
            progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            // 定时更新上传进度
            timer = new Timer();
            timer.schedule(new FileTransTimerTask(progressInfo), 0, 1000);

            FileOutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                transLength = transLength + len;
                progressInfo.setTransLength(transLength);
                progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            }
            // 设置传输完成
            progressInfo.setPlan("100");
            progressInfo.setRemainTime("0");
            progressInfo.setDone(true);
            progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException异常：{} ", e.getMessage());
            progressInfo.setNormal(false);
            return false;
        } catch (IOException e) {
            log.error("IOException：{} ", e.getMessage());
            progressInfo.setNormal(false);
            return false;
        } finally {
            stopTimer();
        }

        return true;
    }

    /**
     * 上传文件到服务器 File类型文件
     * @param progressInfo 下载任务ID
     * @param srcFile 源文件
     * @param destPath 上传路径
     * @return
     */
    public static boolean uploadFileFromFile (ProgressInfo progressInfo, File srcFile, String relateName, String destPath) {
        File targetDir = new File(destPath);
        // 创建上传文件夹
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        // 如果存在，先删除
        File destFile = new File(targetDir + File.separator + relateName);
        if (destFile.exists()) {
            destFile.delete();
        }

        try {
            log.info("--------------------- 远程上传文件 {} start --------------- ", progressInfo.getProgressId());
            // 存储进度对象
            long transLength = 0;
            progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            // 定时更新上传进度
            timer = new Timer();
            timer.schedule(new FileTransTimerTask(progressInfo), 0, 1000);

            FileOutputStream outputStream = new FileOutputStream(destFile);
            //FileInputStream inputStream = new FileInputStream(String.valueOf(srcFile.getInputStream()));
            FileInputStream inputStream = new FileInputStream(srcFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                transLength = transLength + len;
                progressInfo.setTransLength(transLength);
                progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            }
            // 设置传输完成
            progressInfo.setPlan("100");
            progressInfo.setRemainTime("0");
            progressInfo.setDone(true);
            progressSingleTon.put(progressInfo.getProgressId(), progressInfo);
            outputStream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopTimer();
        }

        return true;
    }

    /**
     * 获取剩余预估时间 单位秒
     * @return
     */
    private static int getRemainTime(long fileSize, long transLength, String transVelocity) {
        long remainSize = fileSize - transLength;
        double remainTime = 0.0;
        double velocity = 0.0;
        if (transVelocity.contains("MB/s")) {
            velocity = Double.parseDouble(transVelocity.replace("MB/s", ""));
            remainTime = (double) remainSize / 1024 / 1024 / velocity;
        }
        if (transVelocity.contains("KB/s")) {
            velocity = Double.parseDouble(transVelocity.replace("KB/s", ""));
            remainTime = (double) remainSize / 1024 / velocity;
        }
        if (transVelocity.contains("Byte/s")) {
            velocity = Double.parseDouble(transVelocity.replace("Byte/s", ""));
            remainTime = (double) remainSize / velocity;
        }

        return (int) Math.ceil(remainTime);
    }

    /**
     * 开启定时器
     */
    public static void startTimer() {
        timer = new Timer();
        //timer.schedule(new FileTransTimerTask(), 0, 1000);
    }

    /**
     * 关闭定时器
     */
    public static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    /**
     * 为了系统稳定性，释放前检查是否还有线程在读或写
     * @param mappedByteBuffer
     */
    public static void freeMappedByteBuffer(MappedByteBuffer mappedByteBuffer) {
        try {
            if (mappedByteBuffer == null) {
                return;
            }
            mappedByteBuffer.force();
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    try {
                        Method getCleanerMethod = mappedByteBuffer.getClass().getMethod("clear", new Class[0]);
                        // 可以访问private权限
                        getCleanerMethod.setAccessible(true);
                        // 在具有指定参数的方法对象上调用此方法对象表示的底层方法
                        sun.misc.Cleaner cleaner = (sun.misc.Cleaner) getCleanerMethod.invoke(mappedByteBuffer, new Object[0]);
                        cleaner.clear();
                    } catch (Exception e) {
                        log.error("clean MappedByteBuffer error!!!", e);
                    }
                    log.info("clean MappeByteBuffer completed!!!");
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将文件类型转化为byte类型并返回
     * @param file
     * @return
     */
    public static byte[] getBytesByFile(File file) {
        byte[] bytes = null;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * 判断平台2.0 AirStudio / AirPai 是否具有访问权限
     * @return
     */
    public boolean isAllowAccess(String path, String userId) {
        if (path.startsWith(airStudioAccess.get(0)) || path.startsWith(airStudioAccess.get(1))) {
            return true;
        }

        if (!path.contains(airPaiAccess.get(0)) || !path.contains(airPaiAccess.get(1))) {
            return false;
        }
        String findUserId = path.substring(airPaiAccess.get(0).length(), path.lastIndexOf(airPaiAccess.get(1)));
        String regx = airPaiAccess.get(0)+"[0-9]+"+airPaiAccess.get(1)+"[0-9]"+".*";
        if (Pattern.matches(regx, path) && userId.equalsIgnoreCase(findUserId)) {
            return true;
        }

        return false;
    }
}

