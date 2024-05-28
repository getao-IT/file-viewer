package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

/**
 * @ClassName: AbstractLabelFileParser
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 8:26
 * @Version 1.0
 */


@Slf4j
public abstract class AbstractLabelFileParser extends AbstractLabelParser {


    protected String labelFullPath;

    @Override
    protected boolean beforeLabelItemParse(Object labelItem, String imagePath){
        return this.checkLabelFile((String) labelItem) &&
                this.checkImageFile(imagePath);
    }

    protected boolean checkLabelFile(String labelPath){
        this.labelFullPath = FileUtils.getStringPath(this.rootDir, labelPath);
        if(false == new File(labelFullPath).exists()){
            log.info("选择的标注文件{}不存在", this.labelFullPath);
            return false;
        }
        return true;
    }

}
