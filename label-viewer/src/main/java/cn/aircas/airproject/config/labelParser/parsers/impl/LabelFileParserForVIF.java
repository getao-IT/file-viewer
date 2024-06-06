package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.config.labelParser.parsers.impl.AbstractLabelFileParser;
import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.VifLabelOjectInfo;
import cn.aircas.airproject.entity.emun.LabelCategory;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.utils.XMLUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: LabelParserForVIF
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 10:09
 * @Version 1.0
 */


@Component
public class LabelFileParserForVIF extends AbstractLabelFileParser {

    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {
        return  format == LabelFileFormat.VIF &&
                category == LabelCategory.LABEL_FILE;
    }


    @Override
    protected LabelObject parseLabelFile() {
        VifLabelOjectInfo vifLabelOjectInfo = XMLUtils.parseXMLFromFile(VifLabelOjectInfo.class, this.labelFullPath);
        if(null == vifLabelOjectInfo) {
            throw new RuntimeException("vif file VIF format error");
        }
        return vifLabelOjectInfo;
    }

    @Override
    protected boolean afterLabelFileParse(LabelObject labelObject) {
        return true;
    }
}
