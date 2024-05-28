package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.config.labelParser.parsers.LabelParser;
import cn.aircas.airproject.config.labelParser.parsers.LabelParserComposite;
import cn.aircas.airproject.entity.emun.LabelCategory;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;

import java.util.List;

/**
 * @ClassName: LabelParserCompositeImpl
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/28 8:48
 * @Version 1.0
 */


public class LabelParserCompositeImpl implements LabelParserComposite {

    private List<LabelParser> parserList;
    private LabelParser targetParser;

    public LabelParserCompositeImpl(List<LabelParser> parsers) {
        if(null != parsers) {
            this.parserList = parsers;
        }
    }


    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {

        for (LabelParser parser : parserList) {
            if(true == parser.support(fileType, format, category)){
                targetParser = parser;
                return true;
            }
        }

        targetParser = null;
        return false;
    }

    @Override
    public String parseLabelFile(Object labelItem, String imagePath) {
        return targetParser.parseLabelFile(labelItem, imagePath);
    }

    @Override
    public LabelParser getTargetParser() {
        return targetParser;
    }
}
