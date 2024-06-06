package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.emun.LabelCategory;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.utils.XMLUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: LabelParserStreamForAIRCAS
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/27 16:44
 * @Version 1.0
 */

@Component
public class LabelStreamParserForAIRCAS extends AbstractLabelStreamParser {

    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {
        return format == LabelFileFormat.AIRCAS &&
                category == LabelCategory.LABEL_STREAM;
    }

    @Override
    protected LabelObject parseLabelFile() {
        XMLLabelObjectInfo xmlLabelObjectInfo = XMLUtils.parseXMLFromStream(this.inputStream, XMLLabelObjectInfo.class);
        if(null == xmlLabelObjectInfo) {
            throw new RuntimeException("xml stream AIRCAS format error");
        }

        return xmlLabelObjectInfo;
    }

    @Override
    protected boolean afterLabelFileParse(LabelObject labelObject) {
        boolean flag = super.afterLabelFileParse(labelObject);
        this.inputStream = null;
        return flag;
    }
}
