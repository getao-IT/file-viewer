package cn.aircas.airproject.utils;


import cn.aircas.airproject.entity.emun.FileType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class JudgePictureUtils {
    /**
     * 将文件头转换成16进制字符串
     *
     * @return 16进制字符串
     */
    private static String bytesToHexString(byte[] src) {

        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 得到文件头
     *文件路径
     * @return 文件头
     * @throws IOException
     */
    private static String getFileContent(InputStream is) throws IOException {

        byte[] b = new byte[28];

        InputStream inputStream = null;

        try {
            is.read(b, 0, 28);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        }
        return bytesToHexString(b);
    }

    /**
     * 判断文件类型
     *文件路径
     * @return 文件类型
     */
    public static FileType getType(InputStream is) throws IOException {

        String fileHead = getFileContent(is);
        if (fileHead == null || fileHead.length() == 0) {
            return null;
        }
        fileHead = fileHead.toUpperCase();
        FileType[] fileTypes = FileType.values();

        for (FileType type : fileTypes) {
            if (fileHead.startsWith(type.getValue())) {
                return type;
            }
        }

        return null;
    }
    /**
     *
     * @param value 表示文件类型
     * @return 1 表示图片,2 表示文档,3 表示其它
     * @return
     */
    public static Integer isFileType(FileType value) {
        Integer type = 6;// 其他
        // 图片
        FileType[] pics = { FileType.JPEG, FileType.PNG, FileType.TIFF };

        FileType[] others = {};

        // 图片
        for (FileType fileType : pics) {
            if (fileType.equals(value)) {
                type = 1;
            }
        }
        return type;
    }

    public static int getFileType(File file) throws Exception {
        return isFileType(getType(new FileInputStream(file)));
    }
}
