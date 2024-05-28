package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.config.labelParser.parsers.impl.AbstractLabelFileParser;
import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.VOCBridgedObjectInfo;
import cn.aircas.airproject.entity.LabelFile.VOCLabelObjectInfo;
import cn.aircas.airproject.entity.domain.ImageInfo;
import cn.aircas.airproject.entity.emun.*;
import cn.aircas.airproject.utils.LabelPointTypeConvertor;
import cn.aircas.airproject.utils.ParseImageInfo;
import cn.aircas.airproject.utils.XMLUtils;
import org.springframework.stereotype.Component;

/**
 * @ClassName: LabelParserForVOC
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/24 10:25
 * @Version 1.0
 */

@Component
public class LabelFileParserForVOC extends AbstractLabelFileParser {

    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format, LabelCategory category) {
        return fileType == LabelFileType.XML &&
                format == LabelFileFormat.VOC &&
                category == LabelCategory.LABEL_FILE;
    }

    @Override
    protected LabelObject parseLabelFile() {

        VOCBridgedObjectInfo bridgedObjectInfo = XMLUtils.parseXMLFromFile(VOCBridgedObjectInfo.class, this.labelFullPath);
        if(null == bridgedObjectInfo) {
            throw new RuntimeException("xml file VOC format error");
        }

        return new VOCLabelObjectInfo(bridgedObjectInfo);
    }

    @Override
    protected boolean afterLabelFileParse(LabelObject labelObject) {

        ImageInfo imageInfo = ParseImageInfo.parseInfo(this.imageFullPath);
        CoordinateSystemType coordinateSystemType = imageInfo.getCoordinateSystemType();
        switch (coordinateSystemType){
            case PIXELCS:
                setVOCLabelObjectCoordinateAndDesc(labelObject, "pixel","像素坐标");
                break;
            case GEOGCS:
                setVOCLabelObjectCoordinateAndDesc(labelObject, "geodegree", "地理坐标");
                break;
            case PROJCS:
                setVOCLabelObjectCoordinateAndDesc(labelObject, "projection", "投影坐标");
                break;
            default:
                break;
        }
        return super.afterLabelFileParse(labelObject);
    }

    private void setVOCLabelObjectCoordinateAndDesc(LabelObject labelObject, String coordinate, String desc){
        VOCLabelObjectInfo vocLabelObjectInfo = (VOCLabelObjectInfo) labelObject;
        for (VOCLabelObjectInfo.VOCLabelObject object : vocLabelObjectInfo.getObjects()) {
            object.setCoordinate(coordinate);
            object.setDescription(desc);
        }
    }
}
