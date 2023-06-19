package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.domain.FileTransferProgressInfo;
import cn.aircas.airproject.entity.domain.MultipartFileParam;
import cn.aircas.airproject.mapper.FileTransferProgressInfoMapper;
import cn.aircas.airproject.service.FileTransferProgressInfoService;
import cn.aircas.airproject.utils.FileUtils;
import cn.aircas.airproject.utils.FtpUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.StringJoiner;

@Service
@PropertySource(value = "classpath:/application.yml")
public class FileTransferProgressInfoServiceImpl
        extends ServiceImpl<FileTransferProgressInfoMapper, FileTransferProgressInfo>
        implements FileTransferProgressInfoService {

    @Autowired
    private FileTransferProgressInfoMapper progressInfoMapper;

    @Value("${sudo.password}")
    private String sudoPassword;


    /**
     * 更新已完成传输分块数
     */
    @Override
    public void updateTransferedChunk(String fileMd5) {
        FileTransferProgressInfo transferProgressInfo = this.progressInfoMapper
                .selectOne(new QueryWrapper<FileTransferProgressInfo>().eq("md5", fileMd5));
        transferProgressInfo.setTransferredChunk(transferProgressInfo.getTransferredChunk()+1);
        this.updateById(transferProgressInfo);
    }


    /**
     * 校验文件是否传输完成
     * @param fileMd5
     * @return
     */
    @Override
    public boolean checkFileComplete(String fileMd5) {
        FileTransferProgressInfo transferProgressInfo = this.progressInfoMapper
                .selectOne(new QueryWrapper<FileTransferProgressInfo>().eq("md5", fileMd5));
        if (transferProgressInfo.getChunks() == transferProgressInfo.getTransferredChunk()) {
            return true;
        }
        return false;
    }


    /**
     * 合并分块
     * @param param
     */
    @Override
    public void mergeChunk(MultipartFileParam param, String destPath) {
        String filePath = FileUtils.getStringPath(param.getDestPath(), param.getFileName()).replace("\\", "/");
        StringJoiner cmd = new StringJoiner(" ", " ", " ");
        cmd.add("sudo -S cat");
        for (int i = 1; i <= param.getChunkTotal(); i++) {
            cmd.add(FileUtils.getStringPath(
                    destPath, param.getFileMd5() + i + "_" + param.getFileName() +".temp")
                    .replace("\\", "/"));
        }
        cmd.add("> " + filePath);
        FtpUtils.excuteShell(FtpUtils.session, cmd.toString(), sudoPassword);
        String deleteTempFolder = "sudo -S rm -rf " + destPath;
        FtpUtils.excuteShell(FtpUtils.session, deleteTempFolder, sudoPassword);
    }
}
