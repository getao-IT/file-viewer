package cn.aircas.airproject.config.labelParser.parsers;

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
public class LabelParserForVOC extends AbstractLabelFileParser{
    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format) {
        return (fileType == LabelFileType.XML && format == LabelFileFormat.VOC);
    }

    @Override
    protected LabelObject parseLabelFile() {

        VOCBridgedObjectInfo bridgedObjectInfo = XMLUtils.parseXMLFromFile(VOCBridgedObjectInfo.class, this.labelFullPath);

        return (null == bridgedObjectInfo) ? null : new VOCLabelObjectInfo(bridgedObjectInfo);
    }

    @Override
    protected boolean afterLabelFileParse(LabelObject labelObject) {
        if(null == labelObject) {
            return false;
        }

        ImageInfo imageInfo = ParseImageInfo.parseInfo(this.imageFullPath);
        CoordinateSystemType coordinateSystemType = imageInfo.getCoordinateSystemType();
        LabelPointType labelPointType = null;
        switch (coordinateSystemType){
            case PIXELCS:
                labelPointType = LabelPointType.PIXEL;
                setVOCLabelObjectCoordinateAndDesc(labelObject, "pixel","像素坐标");
                break;
            case GEOGCS:
                labelPointType = LabelPointType.GEODEGREE;
                setVOCLabelObjectCoordinateAndDesc(labelObject, "geodegree", "地理坐标");
                break;
            case PROJCS:
                labelPointType = LabelPointType.PROJECTION;
                setVOCLabelObjectCoordinateAndDesc(labelObject, "projection", "投影坐标");
                break;
            default:
                throw new RuntimeException("解析标注文件时，无法获取对应图像的坐标系统类型");
        }

        String coordinate = labelObject.getCoordinate();
        CoordinateConvertType coordinateConvertType = CoordinateConvertType.NO_ACTION;
        //如果标注点类型与图像坐标系不同
        if (!labelPointType.name().equalsIgnoreCase(coordinate)) {
            if (labelPointType == LabelPointType.GEODEGREE) {
                if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name()))
                    coordinateConvertType = CoordinateConvertType.PROJECTION_TO_LONLAT;
                else
                    coordinateConvertType = CoordinateConvertType.PIXEL_TO_LONLAT;
            }
            if (labelPointType == LabelPointType.PROJECTION) {
                if (coordinate.equalsIgnoreCase(LabelPointType.GEODEGREE.name()))
                    coordinateConvertType = CoordinateConvertType.LONLAT_TO_PROJECTION;
                else
                    coordinateConvertType = CoordinateConvertType.PIXEL_TO_PROJECTION;
            }
            if (labelPointType == LabelPointType.PIXEL) {
                if (coordinate.equalsIgnoreCase(LabelPointType.PROJECTION.name()))
                    coordinateConvertType = CoordinateConvertType.PROJECTION_TO_PIXEL;
                else
                    coordinateConvertType = CoordinateConvertType.LONLAT_TO_PIXEL;
            }
        } else {
            //如果图像坐标为像素，标注坐标为像素，则将像素进行翻转
            if (LabelPointType.PIXEL == labelPointType)
                coordinateConvertType = CoordinateConvertType.PIXEL_REVERSION;
        }
        LabelPointTypeConvertor.convertLabelPointType(this.imageFullPath, labelObject, coordinateConvertType);
        return true;
    }

    private void setVOCLabelObjectCoordinateAndDesc(LabelObject labelObject, String coordinate, String desc){
        VOCLabelObjectInfo vocLabelObjectInfo = (VOCLabelObjectInfo) labelObject;
        for (VOCLabelObjectInfo.VOCLabelObject object : vocLabelObjectInfo.getObjects()) {
            object.setCoordinate(coordinate);
            object.setDescription(desc);
        }
    }
}
