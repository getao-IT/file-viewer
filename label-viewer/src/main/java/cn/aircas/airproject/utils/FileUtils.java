package cn.aircas.airproject.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.XML;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;



/**
 * 文件md5值
 */
@Slf4j
public class FileUtils {

    private static final double KB = 1024D;
    private static final double MB = 1024 * 1024D;
    private static final double GB = 1024 * 1024 * 1024D;


    /**
     * 封装paths方法，使任意类型均可转换为string
     *
     * @param first 第一个值
     * @param more  其它值
     * @return
     */
    public static Path getPath(Object first, Object... more) {
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first), strMore);
    }


    /**
     * 封装paths方法，使任意类型均可转换为string
     *
     * @param first 第一个值
     * @param more  其它值
     * @return
     */
    public static String getStringPath(Object first, Object... more) {
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first), strMore).toString();
    }


    /**
     * 封装paths方法，使任意类型均可转换为string
     *
     * @param first 第一个值
     * @param more  其它值
     * @return
     */
    public static File getFile(Object first, Object... more) {
        String[] strMore = Arrays.stream(more).map(String::valueOf).toArray(String[]::new);
        return Paths.get(String.valueOf(first), strMore).toFile();
    }


    /**
     * 读取文件
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(filePath, "r")) {
            String line;
            while ((line = randomAccessFile.readLine()) != null)
                content.append(new String(line.getBytes("ISO-8859-1"), "utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }


    /**
     * 拷贝文件
     *
     * @param srcFile
     * @param destFile
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile) throws IOException {
        org.apache.commons.io.FileUtils.copyFile(srcFile, destFile);
    }


    /**
     * 拷贝文件到某一文件夹
     *
     * @param srcFile
     * @param destDir
     * @throws IOException
     */
    public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
        org.apache.commons.io.FileUtils.copyFileToDirectory(srcFile, destDir);
    }


    /**
     * xml转json
     *
     * @param filePath
     * @return
     */
    public static JSONObject XMLToJSON(String filePath) {
        String content = FileUtils.readFile(filePath);
        org.json.JSONObject xmlJSONObj = XML.toJSONObject(content);
        return JSON.parseObject(xmlJSONObj.toString());
    }


    public static void writeListToFile(Set<String> list, File destFile) throws IOException {
        if (!destFile.exists())
            destFile.createNewFile();
        if (list.size() == 0)
            return;

        FileWriter fileWriter = new FileWriter(destFile, true);
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
     *
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(File directory) throws IOException {
        org.apache.commons.io.FileUtils.deleteDirectory(directory);
    }


    /**
     * 如果文件重名自动生产副本文件名称
     *
     * @param destFile
     */
    public static File autoMakeIfFileRepeat(File destFile) {
        String newFileName = FilenameUtils.getBaseName(destFile.getName()) + "_copy." + FilenameUtils.getExtension(destFile.getName());
        int copy_num = 1;
        while (true) {
            File newFile = new File(cn.aircas.utils.file.FileUtils.getStringPath(destFile.getParentFile().getAbsolutePath(), newFileName));
            if (newFile.exists()) {
                String baseName = FilenameUtils.getBaseName(newFile.getName());
                newFileName = baseName.replace(baseName.substring(baseName.lastIndexOf("_")), "_copy") + copy_num + "." + FilenameUtils.getExtension(newFile.getName());            } else {
                break;
            }
            copy_num++;
        }
        return new File(cn.aircas.utils.file.FileUtils.getStringPath(destFile.getParentFile().getAbsolutePath(), newFileName));
    }

    /**
     * 替换文件名称的后缀名
     * @param srcFilePath 需要替换后缀名的文件名称
     * @param newExtension 文件新的后缀名
     * @return 替换了新的后缀名的文件名称
     */
    public static String replaceFileExtension(String srcFilePath, String newExtension){
        if(false == srcFilePath.contains("."))
            return srcFilePath;

        return StringUtils.substringBeforeLast(srcFilePath, ".") +  (newExtension.contains(".")?newExtension:("."+newExtension));
    }
}
