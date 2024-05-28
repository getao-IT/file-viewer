package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.VifLabelOjectInfo;
import cn.aircas.airproject.entity.emun.LabelCategory;
import cn.aircas.airproject.entity.emun.LabelFileFormat;
import cn.aircas.airproject.entity.emun.LabelFileType;
import cn.aircas.airproject.utils.XMLUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: LabelSteamParserForVIF
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/28 8:28
 * @Version 1.0
 */

@Component
public class LabelSteamParserForVIF extends AbstractLabelStreamParser{
    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {
        return format == LabelFileFormat.VIF &&
                category == LabelCategory.LABEL_STREAM;
    }

    @Override
    protected LabelObject parseLabelFile() {
        VifLabelOjectInfo vifLabelOjectInfo = XMLUtils.parseXMLFromStream(this.inputStream, VifLabelOjectInfo.class);
        if(null == vifLabelOjectInfo) {
            throw new RuntimeException("vif stream VIF format error");
        }

        return vifLabelOjectInfo;
    }

    @Override
    protected boolean afterLabelFileParse(LabelObject labelObject) {
        this.inputStream = null;
        return true;
    }
}
