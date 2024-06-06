package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.config.labelParser.parsers.impl.AbstractLabelFileParser;
import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.domain.ImageInfo;
import cn.aircas.airproject.entity.emun.*;
import cn.aircas.airproject.utils.LabelPointTypeConvertor;
import cn.aircas.airproject.utils.ParseImageInfo;
import cn.aircas.airproject.utils.XMLUtils;

import org.springframework.stereotype.Component;

/**
 * @ClassName: LabelParserForAIRCAS
 * @Description 对xml类型AIRCAS格式的标注文件格式进行解析
 * @Author yzhan
 * @Date 2024/5/24 8:41
 * @Version 1.0
 */


@Component
public class LabelFileParserForAIRCAS extends AbstractLabelFileParser {

    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {
        return fileType == LabelFileType.XML &&
                format == LabelFileFormat.AIRCAS &&
                category == LabelCategory.LABEL_FILE;
    }

    @Override
    protected LabelObject parseLabelFile() {

        XMLLabelObjectInfo labelObject = XMLUtils.parseXMLFromFile(XMLLabelObjectInfo.class, this.labelFullPath);
        if(null == labelObject){
            throw new RuntimeException("xml file AIRCAS format error");
        }

        return labelObject;
    }
}
