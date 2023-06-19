package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.common.CommonResult;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.emun.ResultCode;
import cn.aircas.airproject.service.RemoteLabelProjectService;
import cn.aircas.airproject.utils.FileUtils;
import cn.aircas.airproject.utils.FtpUtils;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@PropertySource(value = "classpath:/application.yml")
public class RemoteLabelProjectServiceImpl implements RemoteLabelProjectService {

    @Value("${sudo.password}")
    private String sudoPassword;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${temp.download-path}")
    private String downloadPath;

    private Map<String, ProgressInfo> progressSingleTon = ProgressResponseSingleTon.getInstance();


    @Override
    public List<FileAndFolder> getFileAndFolderList(String path) {
        log.info("----------------------------------------- 线程 {} 开始执行 ---------------------- ", Thread.currentThread().getName());
        // 获取远程连接
        //FtpUtils.getSessionConnect(params.getHost(), params.getPort(), params.getUserName(), params.getPassWord());
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return new ArrayList<>();
        }
        try {
            String cmd = "sudo -S ls -l " + path;
            BufferedReader bufferedReader = FtpUtils.excuteShellAndOutput(FtpUtils.session, cmd, sudoPassword);
            List<FileAndFolder> fileList = new ArrayList<>();
            String line = "";
            String fileFullName = "";
            String filePath = "";
            String modifyTime = "";
            String fileType = "";
            while ((line = bufferedReader.readLine()) != null) {
                String[] split = line.split(" +");
                if (split.length < 8) {
                    continue;
                }
                fileFullName = split[8];
                if (!fileFullName.endsWith(".") && !fileFullName.endsWith("..") && !fileFullName.startsWith(".")) {
                    // 设置路径下文件或文件夹属性
                    filePath = path + "/" + fileFullName;
                    FileAndFolder fileAndFolder = new FileAndFolder();
                    fileAndFolder.setUUID(UUID.randomUUID().toString().replace("-", ""));
                    fileAndFolder.setName(fileFullName);
                    fileAndFolder.setPath(path);
                    fileAndFolder.setLastModified(new Date());
                    modifyTime = Calendar.getInstance().get(Calendar.YEAR) + "/" + split[5].substring(0, split[5].length() - 1)
                            + "/" + split[6] + " " + split[7];
                    try {
                        fileAndFolder.setLastModified(new Date(modifyTime));
                    } catch (IllegalArgumentException e) {
                        modifyTime = split[7] + "/" + split[5].substring(0, split[5].length() - 1)
                                + "/" + split[6];
                        fileAndFolder.setLastModified(new Date(modifyTime));
                    }
                    fileType = split[0].replace(" ", "").substring(0, 1);
                    // 文件类型判定
                    if (fileType.equals("d")) {
                        fileAndFolder.setFileType("d");
                        fileAndFolder.setExtension("文件夹");
                        fileAndFolder.setIsFile(false);
                    } else if (fileType.equals("-")) {
                        fileAndFolder.setFileType("-");
                        fileAndFolder.setIsFile(true);
                        fileAndFolder.setExtension(fileFullName.substring(fileFullName.lastIndexOf(".") + 1));
                    } else if (fileType.equals("l")) {
                        fileAndFolder.setFileType("l");
                        fileAndFolder.setExtension("符号链接");
                        fileAndFolder.setLinkPath(split[10]);
                        try {
                            sftp.cd(split[10]);
                            fileAndFolder.setLinkPathIsFile(false);
                        } catch (SftpException e) {
                            fileAndFolder.setLinkPathIsFile(true);
                        }
                    } else if (fileType.equals("s")) {
                        fileAndFolder.setFileType("s");
                        fileAndFolder.setExtension("套接字文件");
                    } else if (fileType.equals("b")) {
                        fileAndFolder.setFileType("b");
                        fileAndFolder.setExtension("块设备文件，二进制文件");
                    } else if (fileType.equals("c")) {
                        fileAndFolder.setFileType("c");
                        fileAndFolder.setExtension("字符设备文件");
                    } else if (fileType.equals("p")) {
                        fileAndFolder.setFileType("p");
                        fileAndFolder.setExtension("命名管道文件");
                    }
                    fileAndFolder.setFileSize(Long.parseLong(split[4]));
                    fileAndFolder.setAttribute(split[0]);
                    fileAndFolder.setOwner(split[2]);
                    fileList.add(fileAndFolder);
                }
            }
            List<FileAndFolder> fileAndFolders = fileList.stream().filter(f -> !isStartWithFromList(f.getPath())).sorted((e1, e2) -> e1.getName().compareTo(e2.getName())).collect(Collectors.toList());
            log.info("----------------------------------------- 线程 {} 执行结束 ---------------------- ", Thread.currentThread().getName());

            return fileAndFolders;
        } catch (IOException e) {
            log.error("IOException：{} ", e.getMessage());
            return null;
        } finally {
            FtpUtils.closeChannel(sftp);
        }
    }

    /**
     *
     * @param folder
     * @return
     */
    private boolean isStartWithFromList(String folder) {
        return false;
    }
    @Override
    public void copyFileAndFolder(FileManagerParams params, String srcPath, String destPath) {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return;
        }

        // 拷贝文件
        FtpUtils.copyFileAndFolder(sftp, srcPath, destPath);

        // 关闭连接
        FtpUtils.closeChannel(sftp);
    }


    @Override
    public String deleteFolder(FileManagerParams params, String srcPath) {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return "Sftp连接失败：主机IP " + params.getHost();
        }

        // 删除文件夹
        try {
            sftp.rmdir(srcPath);
        } catch (SftpException e) {
            log.error("Sftp异常：{} ", e.getMessage());
            return e.getMessage();
        } finally {
            FtpUtils.closeChannel(sftp);
        }
        return "";
    }

    @Override
    public String deleteFile(FileManagerParams params, String srcPath) {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return "Sftp连接失败：主机IP " + params.getHost();
        }

        // 删除文件
        try {
            sftp.rm(srcPath);
        } catch (SftpException e) {
            log.error("Sftp异常：{} ", e.getMessage());
            return e.getMessage();
        } finally {
            FtpUtils.closeChannel(sftp);
        }
        return "";
    }

    /**
     * 删除文件或文件夹 sudo方式
     *
     * @param params
     * @param srcPath
     * @return
     */
    public boolean deleteFileOrFolder(FileManagerParams params, String srcPath) {
        boolean result = false;
        // 获取session连接
        //FtpUtils.getSessionConnect(params.getHost(), params.getPort(), params.getUserName(), params.getPassWord());

        String cmd = "sudo -S rm -rf " + srcPath;
        result = FtpUtils.excuteShell(FtpUtils.session, cmd, sudoPassword);

        return result;
    }

    @Override
    public boolean fileRename(FileManagerParams params) {
        // 重命名
        boolean result = false;
        String cmd = "sudo -S mv " + params.getOldName() + " " + params.getNewName();
        result = FtpUtils.excuteShell(FtpUtils.session, cmd, sudoPassword);

        return result;
    }

    @Override
    public FileInfo getFileInfo(FileManagerParams params, String path) {
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        File tempFile = new File(path);
        if (sftp == null) {
            log.error("名称为'" + tempFile.getAbsolutePath() + "'的文件不存在");
            return null;
        }
        // 获取文件信息
        FileInfo fileInfo = null;
        try {
            SftpATTRS attrs = sftp.stat(path);
            fileInfo = new FileInfo();
            String fileName = tempFile.getName();
            if (attrs.isDir()) {
                fileInfo.setFileType("文件夹");
            } else {
                fileInfo.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));
                fileInfo.setFileSize(attrs.getSize());
            }
            fileInfo.setName(fileName);
            fileInfo.setLocation(path.replace(fileName, ""));
            fileInfo.setModifyTime(new Date(attrs.getMtimeString()));
            fileInfo.setPermissions(attrs.getPermissionsString());
            fileInfo.setGroupId(attrs.getGId());
            fileInfo.setUserId(attrs.getUId());
        } catch (SftpException e) {
            log.error("路径不存在：{} ", e.getMessage());
        } finally {
            FtpUtils.closeChannel(sftp);
        }

        return fileInfo;
    }

    @Override
    public boolean createFile(FileManagerParams params, String path) {
        // 获取session连接
        //FtpUtils.getSessionConnect(params.getHost(), params.getPort(), params.getUserName(), params.getPassWord());

        String cmd = "sudo -S touch " + path;
        boolean result = new FtpUtils().excuteShell(FtpUtils.session, cmd, sudoPassword);

        /*// 创建文件 无sudo
        try {
            OutputStream put = sftp.put(path);
        } catch (SftpException e) {
            log.error("Sftp异常：{} ", e.getMessage());
            return false;
        } finally {
            FtpUtils.closeSftp();
        }*/
        return result;
    }

    /**
     * 创建文件夹
     *
     * @param params 连接远程主机参数
     * @param path   创建文件夹名称
     * @return
     */
    @Override
    public boolean createFolder(FileManagerParams params, String path) {
        // 获取session连接
        //FtpUtils.getSessionConnect(params.getHost(), params.getPort(), params.getUserName(), params.getPassWord());

        String cmd = "sudo -S mkdir " + path;
        boolean result = new FtpUtils().excuteShell(FtpUtils.session, cmd, sudoPassword);
        return result;
    }

    @Override
    public String getFileContent(FileManagerParams params, String path) {
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return "";
        }
        // 获取文件内容
        String content = "";
        try {
            InputStream inputStream = sftp.get(path);
            File tempFile = File.createTempFile("temp", null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            content = outputStream.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("UnsupportedEncodingException异常：{} ", e.getMessage());
        } catch (IOException e) {
            log.error("IOException异常：{} ", e.getMessage());
        } catch (SftpException e) {
            log.error("SftpException异常：{} ", e.getMessage());
        } finally {
            FtpUtils.closeChannel(sftp);
        }

        return content;
    }

    @Override
    public boolean writeFile(FileManagerParams params, String path, String content, boolean b) {
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return false;
        }
        // 写入文件
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
            sftp.put(inputStream, path);
            return true;
        } catch (SftpException e) {
            log.error("SftpException异常：{}", e.getMessage());
        } finally {
            FtpUtils.closeChannel(sftp);
        }
        return false;
    }

    /**
     * 下载远程主机文件 Step 1
     *
     * @param params  连接参数
     * @param srcPath 远程文件路径 路径+文件名
     * @return
     */
    @Override
    @Async("taskExecutor")
    public CommonResult downLoadStepOne(String progressId, FileManagerParams params, String srcPath) throws SftpException {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            return null;
        }

        // 下载文件
        FileOutputStream out = null;
        File file = new File(srcPath); // 获取源文件信息，单纯的信息
        SftpATTRS stat = sftp.stat(srcPath);

        if (!(srcPath == null || srcPath.trim().length() == 0)) {
            try {
                sftp.cd(file.getParent().replace("\\", "/"));
                if (stat.isDir()) {
                    String dirPath = (file.getPath() + ".tar").replace("\\", "/");
                    String cmd = "sudo -S tar -cvf " + dirPath + " " + file.getPath().replace("\\", "/");
                    FtpUtils.excuteShell(FtpUtils.session, cmd, sudoPassword);
                    file = new File(dirPath);
                    srcPath = srcPath + ".tar";
                    // 文件压缩成功，但是压缩后的文件，z格式，打不开，不z格式会压缩文件夹整个目录
                    //System.out.println("压缩文件夹传输，压缩文件成功");
                }
                File saveFile = File.createTempFile("temp", file.getName());
                //File saveFile = new File(srcPath); // 创建本地保存文件
                if (saveFile.exists()) {
                    saveFile.delete();
                }
                out = new FileOutputStream(saveFile);
                sftp.get(srcPath, out); // 下载文件，文件不存在会报错

                // 上载到9.64var/nfs/general下供下载使用
                ProgressInfo progressInfo = new ProgressInfo();
                progressInfo.setProgressId(progressId);
                progressInfo.setSecond(0);
                progressInfo.setTransVelocity("0");
                progressInfo.setTransLength(0);
                progressInfo.setPlan("0");
                progressInfo.setFileSize(saveFile.length());
                progressInfo.setRemainTime("0");
                progressInfo.setConsumTime("0");
                FileUtils.uploadFileFromFile(progressInfo, saveFile, file.getName(), downloadPath);

                if (stat.isDir()) {
                    FtpUtils.excuteShell(FtpUtils.session, "sudo -S rm -rf " + srcPath, sudoPassword); // 上传完，删除压缩文件
                }
            } catch (SftpException e) {
                if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                    log.error("该路径不存在：{}", file.getParent());
                } else if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED) {
                    log.error("没有对该文件的读取权限：异常信息 {} ，目标文件 {}", e.getMessage(), file.getAbsolutePath());
                    return new CommonResult().message("没有对该文件的读取权限：" + e.getMessage()).fail(ResultCode.FAIL_PERMISSION_DENIED);
                } else {
                    log.error("其他SftpException： 异常ID {}，异常信息 {}", e.id, e.getMessage());
                }
            } catch (IOException e) {
                log.error("IO异常：{} ", e.getMessage());
            } finally {
                //FtpUtils.closeChannel(sftp);
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * 下载远程主机文件 Step 2
     *
     * @param fileName
     * @param isFile
     * @return
     */
    @Override
    public CommonResult downLoadStipTwo(String fileName, boolean isFile) {
        File file = null;
        if (!isFile) {
            fileName = fileName + ".tar";
        }
        file = new File(fileName);
        CommonResult result = this.excuteDownload(file);
        return result;
    }

    /**
     * 删除临时文件
     *
     * @param fileName
     */
    @Override
    public void clearTempFile(String fileName) {
        String cmd = "sudo -S rm -rf " + downloadPath + "/" + fileName;
        FtpUtils.excuteShell(FtpUtils.session, cmd, sudoPassword);
    }

    /* *//**
     * 上传文件或文件夹到远程服务器，多个递归上传
     * @param srcPath
     * @param params
     * @param destPath
     * @return
     *//*
    @Override
    @Async("taskExecutor")
    public void upload(String srcPath, FileManagerParams params, String destPath) throws SftpException {
        // 获取远程主机连接
        ChannelSftp sftp = FtpUtils.getSftpConnect();
        if (sftp == null) {
            log.error("SftpException异常：Sftp连接失败异常 ");
        }

        File srcFile = new File(srcPath);
        // 根据上传是文件还是文件夹
        if (srcFile.isFile()) {
            new FtpUtils().excuteUpload(sftp, srcFile, destPath);
        } else {
            // 目标路径是否存在
            destPath = FtpUtils.getDestPath(sftp, destPath, srcFile.getName());
            sftp.mkdir(destPath);
            // 是文件夹 遍历文件上传
            File[] files = srcFile.listFiles();
            for (File file : files) {
                upload(file.getAbsolutePath(), params, destPath);
            }
        }
    }*/

    /**
     * 上传文件到服务，单个文件上传
     *
     * @param inputStream
     * @param params
     * @param destPath
     * @return
     */
    @Override
    @Async("taskExecutor")
    public void upload(String progressId, InputStream inputStream, FileManagerParams params, String destPath, String fileName) {
        String cmd = "sudo -S ls -l -d " + destPath;
        BufferedReader bufferedReader = FtpUtils.excuteShellAndOutput(FtpUtils.session, cmd, sudoPassword);
        try {
            String readLine = bufferedReader.readLine();
            String user = readLine.split(" +")[2];
            if (!params.getUserName().equals("root") && !params.getUserName().equals(user)) {
                log.error("权限拒绝：该用户没有对该路径 {} 的操作权限", destPath);
                return;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        new FtpUtils().excuteUpload(progressId, inputStream, destPath, fileName.replace("(", "（")
                .replace(")", "）").replace(" ", ""));
    }


    /**
     * 执行下载远程文件
     *
     * @param file
     * @return
     */
    public CommonResult excuteDownload(File file) {
        // 生成下载链接
        String url = "http://192.168.2.156:31153/api/v1/airengine/file_transfer/download/";
        int service_id = ConnectServiceManager.serviceId;
        String token = request.getHeader("token");
        Map<String, Object> param = new HashMap<>();
        param.put("src_file_path", FileUtils.getStringPath(downloadPath, file.getName()).replace("\\", "/"));
        param.put("service_id", service_id);
        param.put("download_file_name", null);

        HttpHeaders headers = new HttpHeaders();
        headers.add("token", token);
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(param, headers);
        CommonResult commonResult = restTemplate.exchange(url, HttpMethod.POST, entity, CommonResult.class).getBody();
        return commonResult;
    }

    /**
     * 获取文件上传进度
     *
     * @param progressId
     * @return
     */
    @Override
    public ProgressInfo getProgressById(String progressId) {
        ProgressInfo progressInfo = ProgressResponseSingleTon.getInstance().get(progressId);
        if (progressInfo == null) {
            progressInfo = ProgressInfo.builder().progressId(progressId).consumTime("0").remainTime("0").isNormal(true)
                    .plan("0").transLength(0).transVelocity("0").second(0).build();
        }
        return progressInfo;
    }
}
