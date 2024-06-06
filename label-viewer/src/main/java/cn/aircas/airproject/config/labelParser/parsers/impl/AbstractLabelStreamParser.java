package cn.aircas.airproject.config.labelParser.parsers.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

/**
 * @ClassName: AbstractLabelStreamParser
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/27 16:23
 * @Version 1.0
 */

@Slf4j
public abstract class AbstractLabelStreamParser extends AbstractLabelParser{

    protected InputStream inputStream;

    @Override
    protected boolean beforeLabelItemParse(Object labelItem, String imagePath) {
        return checkStream((InputStream) labelItem) &&
                checkImageFile(imagePath);
    }

    protected boolean checkStream(InputStream is) {
        String info = null;
        if(null == is){
            log.info("输入流为空，立即返回");
            return false;
        }
        log.info("输入流正常");
        inputStream = is;
        return true;
    }


}
