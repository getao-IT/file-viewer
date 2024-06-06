package cn.aircas.airproject.config.labelParser.parsers;

/**
 * @ClassName: LabelParserComposite
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 16:43
 * @Version 1.0
 */


public interface LabelParserComposite extends LabelParser {
    LabelParser getTargetParser();
}
