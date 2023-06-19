package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.domain.FileManagerParams;
import cn.aircas.airproject.entity.domain.MultipartFileParam;
import cn.aircas.airproject.entity.domain.ProgressInfo;
import cn.aircas.airproject.entity.domain.ProgressResponseSingleTon;
import cn.aircas.airproject.service.impl.ProgressMonitorImpl;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;

@Slf4j
public class FtpUtils {

    private static ChannelSftp sftp = null;

    private static Map<String, ProgressInfo> progressSingleTon = ProgressResponseSingleTon.getInstance();
    
    // Ftp连接单例
    public static Session session;

    /**
     * 获取JSCH Session连接
     * @param host IP
     * @param port 端口
     * @param userName 用户名
     * @param passWord 密码
     * @return SFTP连接
     */
    public static boolean getSessionConnect(String host, int port, String userName, String passWord) {
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(userName, host, port);
            if (passWord != null) {
                session.setPassword(passWord);
            }
            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);
            session.setTimeout(1000 * 1800);
            session.connect();
        } catch (JSchException e) {
            log.error("com.jcraft.jsch.JSchException: Auth fail 远程认证失败！\n" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取FTP通道连接
     * @return SFTP连接
     */
    public static ChannelSftp getSftpConnect() {
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (JSchException e) {
            log.error("com.jcraft.jsch.JSchException: Auth fail 远程认证失败！\n" + e.getMessage());
            return null;
        }
        return sftp;
    }

    /**
     * 关闭连接
     */
    public static void closeChannel (Channel channel) {
        if (!channel.isClosed()) {
            channel.disconnect();
        }
    }

    /**
     * 关闭连接
     */
    public static void closeSession () {
        if (session.isConnected()) {
            session.disconnect();
        }
    }

    /**
     * 复制文件的具体实现方法
     * @param sftp
     * @param srcPath
     * @param destPath
     */
    public static void copyFile(ChannelSftp sftp, String srcPath, String destPath) {
        OutputStream out = null;
        try {
            out = sftp.put(destPath);
            //out = new FileOutputStream("C:\\Users\\dell\\Desktop\\abc.txt");
            //sftp.get(srcPath, out);
            InputStream inputStream = sftp.get(srcPath);
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len=inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (SftpException e) {
            log.error("Sftp异常：{} ", e.getMessage());
        } catch (IOException e) {
            log.error("IO异常：{} ", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 如果目标路径下存在该路径，返回新的路径
     * @param sftp
     * @param destPath
     * @param fileName
     * @return 已存在路径 + -copy 的文件名路径
     */
    public static String getDestPath(ChannelSftp sftp, String destPath, String fileName) throws SftpException {
        String newName = "";
        String oldName = "";
        String targetPath = "";
        try {
            targetPath = FileUtils.getStringPath(destPath, fileName).replace("\\", "/");
            while (true) {
                SftpATTRS attrs = sftp.stat(targetPath);
                if (attrs.isDir()) {
                    oldName = fileName.substring(0);
                    newName = oldName + "-copy";
                    targetPath = FileUtils.getStringPath(destPath, newName).replace("\\", "/");
                } else {
                    oldName = fileName.substring(0,fileName.lastIndexOf("."));
                    String suffix = fileName.substring(fileName.lastIndexOf("."));
                    newName = oldName + "-copy" + suffix;
                    targetPath = FileUtils.getStringPath(destPath, newName).replace("\\", "/");
                }
                fileName = newName;
            }
        } catch (SftpException e) {
            log.info("创建上传路径：{} ", targetPath);
        }
        return targetPath;
    }

    /**
     * 复制文件或者文件夹
     * @param sftp
     * @param srcPath
     * @param destPath
     */
    public static void copyFileAndFolder(ChannelSftp sftp, String srcPath, String destPath ) {
        try {
            SftpATTRS attrs = sftp.stat(srcPath);
            // 是否存在相同文件，存在返回新的路径，否则返回当前路径
            destPath = FtpUtils.getDestPath(sftp, destPath, new File(srcPath).getName());
            if (attrs.isDir()) { // 如果是文件夹
                sftp.mkdir(destPath); //创建文件夹
                String targetPath = "";
                Vector ls = sftp.ls(srcPath);
                if (ls.size() > 0) {
                    Iterator iterator = ls.iterator();
                    while (iterator.hasNext()) {
                        ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) iterator.next();
                        String filename = lsEntry.getFilename();
                        if (!filename.endsWith(".") && ! filename.endsWith("..") && !filename.startsWith(".")) {
                            String tempPath = FileUtils.getStringPath(srcPath, filename).replace("\\", "/");
                            copyFileAndFolder(sftp, tempPath, destPath);
                        }
                    }
                }
            } else { // 如果是文件
                FtpUtils.copyFile(sftp, srcPath, destPath);
            }

        } catch (SftpException e) {
            log.error("Sftp异常：{} ", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 判断是文件直接调用上传接口，不是则递归判断
     * @param progressId
     * @param params
     * @param srcFile
     * @param destPath
     */
    @Deprecated
    public static void uploadFile(ChannelSftp sftp, String progressId, FileManagerParams params, File srcFile,  String destPath) throws SftpException {

        // 根据上传是文件还是文件夹
        if (srcFile.isFile()) {
            //new FtpUtils().excuteUpload(progressId, sftp, srcFile, destPath);
        } else {
            // 目标路径是否存在
            destPath = FtpUtils.getDestPath(sftp, destPath, srcFile.getName());
            sftp.mkdir(destPath);
            // 是文件夹 遍历文件上传
            File[] files = srcFile.listFiles();
            for (File file : files) {
                uploadFile(sftp, progressId, params, file, destPath);
            }
        }
    }

    /**
     * 分块上传文件 分快逻辑处理 TODO
     * @param progressInfo
     * @param param
     */
    public static void chunkUploadFile(ProgressInfo progressInfo, MultipartFileParam param) {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            log.error("SftpException异常：Sftp连接失败异常 ");
        }

        // 上传操作
        try {
            String tempPath = FileUtils.getStringPath(param.getDestPath(), param.getFileMd5()).replace("\\", "/");
            try {
                sftp.ls(tempPath);
            } catch (SftpException e) {
                sftp.mkdir(tempPath);
            }
            String destPath = FileUtils.getStringPath(
                    tempPath, param.getFileMd5() + param.getIndexChunk() + "_" + param.getFileName() +".temp")
                    .replace("\\", "/");

            // 创建传输属性对象，供获取传输进度用
            progressInfo.setFileSize(param.getInputStream().available());
            // 创建进度任务
            ProgressResponseSingleTon.getInstance().put(progressInfo.getProgressId(), progressInfo);
            // 开始传输并监听进度
            InputStream inputStream = param.getInputStream();
            OutputStream outputStream = sftp.put(destPath, new ProgressMonitorImpl(progressInfo), ChannelSftp.OVERWRITE);
            if (outputStream != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            } else {
                log.error("目标输出流创建失败：路径为 {} ", destPath);
            }
            outputStream.flush();
            inputStream.close();
        } catch (SftpException | ArithmeticException | IOException e) {
            progressSingleTon.get(progressInfo.getProgressId()).setNormal(false);
            log.error("异常：{} ，文件 {} 上传出错，请重新上传！", e.getMessage(), progressInfo.getProgressId());
            return;
        }  finally {
            FtpUtils.closeChannel(sftp);
        }
    }

    /**
     * 执行远程上传
     * @param inputStream
     * @param destPath
     */
    public void excuteUpload(String progressId,  InputStream inputStream, String destPath, String fileName) {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            log.error("SftpException异常：Sftp连接失败异常 ");
        }

        // 上传操作
        try {
            destPath = FtpUtils.getDestPath(sftp, destPath, fileName);
            // 拿到输入流
//            InputStream inputStream = srcFile.getInputStream();

            // 创建传输属性对象，供获取传输进度用
            ProgressInfo progressInfo = new ProgressInfo();
            progressInfo.setProgressId(progressId);
            progressInfo.setFileSize(inputStream.available());
            // 创建进度任务
            ProgressResponseSingleTon.getInstance().put(progressId, progressInfo);
            // 开始传输并监听进度
            OutputStream outputStream = sftp.put(destPath, new ProgressMonitorImpl(progressInfo), ChannelSftp.OVERWRITE);
            if (outputStream != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            } else {
                log.error("目标输出流创建失败：路径为 {} ", destPath);
            }
            outputStream.flush();
            inputStream.close();
        } catch (SftpException | ArithmeticException | IOException e) {
            progressSingleTon.get(progressId).setNormal(false);
            log.error("异常：{} ，文件 {} 上传出错，请重新上传！", e.getMessage(), progressId);
            return;
        }  finally {
            FtpUtils.closeChannel(sftp);
        }
    }

    /**
     * 执行命令 无返回值
     * @param session
     * @param cmd
     * @return
     */
    public static boolean excuteShell(Session session, String cmd, String sudoPassword) {
        log.info("执行sudo命令：{} ", cmd);
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            OutputStream outputStream = channel.getOutputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            outputStream.write((sudoPassword + "\n").getBytes());
            outputStream.flush();
            String line = "";
            while (true) {
                if ((line = input.readLine()) == null) {
                    log.info("命令执行完成！");
                    break;
                }
                log.info("正在执行处理：{} ", line);
            }
            return true;
        } catch (JSchException e) {
            log.error("com.jcraft.jsch.JSchException: Auth fail 远程认证失败，需重新进入终端：" + e.getMessage());
        } catch (IOException e) {
            log.error("IO异常：" + e.getMessage());
        }
        return false;
    }

    /**
     * 执行命令 有返回值
     * @param session
     * @param cmd
     * @return
     */
    public static BufferedReader excuteShellAndOutput(Session session, String cmd, String sudoPassword) {
        log.info("执行sudo命令：{} ", cmd);
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            OutputStream outputStream = channel.getOutputStream();
            InputStream inputStream = channel.getInputStream();
            channel.connect();
            outputStream.write((sudoPassword + "\n").getBytes());
            outputStream.flush();
            BufferedReader input = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            return input;
        } catch (JSchException e) {
            log.error("com.jcraft.jsch.JSchException: Auth fail 远程认证失败，需重新进入终端：" + e.getMessage());
        } catch (IOException e) {
            log.error("IO异常：" + e.getMessage());
        }
        return null;
    }
}
