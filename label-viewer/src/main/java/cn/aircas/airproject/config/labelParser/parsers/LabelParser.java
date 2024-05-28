package cn.aircas.airproject.config.labelParser.parsers;

import cn.aircas.airproject.entity.emun.LabelCategory;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;

/**
 * @ClassName: LabelFileParser
 * @Description 标注文件解析接口
 * @Author yzhan
 * @Date 2024/5/24 8:22
 * @Version 1.0
 */


public interface LabelParser {
    boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category);

    String parseLabelFile(Object labelItem, String imagePath);
}
