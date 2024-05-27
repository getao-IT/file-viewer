package cn.aircas.airproject.config.labelParser.parsers;

import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;

import java.util.List;

/**
 * @ClassName: LabelParserComposite
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 16:23
 * @Version 1.0
 */


public class LabelParserComposite implements LabelFileParserComposite{

    private List<LabelFileParser> parserList;
    private LabelFileParser targetParser;

    public LabelParserComposite(List<LabelFileParser> parsers){
        if(null != parsers)
            this.parserList = parsers;
    }

    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format) {
        for (LabelFileParser parser : parserList) {
            if(parser.support(fileType, format))
            {
                targetParser = parser;
                return true;
            }
        }
        return false;
    }

    @Override
    public String parseLabelFile(String labelPath, String imagePath) {
        return targetParser.parseLabelFile(labelPath, imagePath);
    }

    @Override
    public void setRootDir(String rootDir) {
        if(null != parserList){
            for (LabelFileParser parser : parserList) {
                parser.setRootDir(rootDir);
            }
        }
    }

    @Override
    public LabelFileParser getSupportedParser() {
        return this.targetParser;
    }
}
