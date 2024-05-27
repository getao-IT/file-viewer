package cn.aircas.airproject.config.labelParser.parsers;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @ClassName: AbstractLabelFileParser
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 8:26
 * @Version 1.0
 */


@Slf4j
public abstract class AbstractLabelFileParser implements LabelFileParser, InitializingBean {

    @Value(value = "${sys.rootDir}")
    protected String rootDir;
    protected String labelFullPath;
    protected String imageFullPath;

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }


    @Override
    public abstract boolean support(LabelFileType fileType, LabelFileFormat format);

    @Override
    public String parseLabelFile(String labelPath, String imagePath) {
        //文件地址以及图像地址的前期处理；
        if(false == beforeLabelFileParse(labelPath, imagePath, this.rootDir)){
            return null;
        }

        //解析文件对象——这里的异常如何处理，抛出？还是自己内部处理掉？感觉抛出更好，否则返回null会有多个情况
        LabelObject labelObject = parseLabelFile();

        //解析对象的后处理
        if( false == afterLabelFileParse(labelObject)) {
            return null;
        }

        return labelObject.toJSONObject().toJSONString();
    }

    protected boolean beforeLabelFileParse(String labelPath, String imagePath, String rootDir){
        this.labelFullPath = FileUtils.getStringPath(this.rootDir, labelPath);
        if(false == new File(labelFullPath).exists()){
            log.info("选择的标注文件{}不存在", this.labelFullPath);
            return false;
        }

        this.imageFullPath = FileUtils.getStringPath(this.rootDir, imagePath);
        if(false == new File(this.imageFullPath).exists()){
            log.info("与标注文件匹配的图像文件{}不存在", this.imageFullPath);
            return false;
        }

        return true;
    }

    protected abstract LabelObject parseLabelFile();

    protected abstract boolean afterLabelFileParse(LabelObject labelObject);

    @Override
    public void afterPropertiesSet() throws Exception {
        if(null == this.rootDir){
            //不能为null，可以为空字符串或者非空字符串
            throw new  RuntimeException("rootDir不能为null，无法完成初始化");
        }
    }
}
