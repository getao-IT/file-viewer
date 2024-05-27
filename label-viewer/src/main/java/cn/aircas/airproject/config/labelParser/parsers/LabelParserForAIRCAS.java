package cn.aircas.airproject.config.labelParser.parsers;

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
public class LabelParserForAIRCAS extends AbstractLabelFileParser{


    @Override
    public boolean support(LabelFileType fileType, LabelFileFormat format) {
        return (fileType == LabelFileType.XML && format == LabelFileFormat.AIRCAS);
    }


    @Override
    protected LabelObject parseLabelFile() {

        XMLLabelObjectInfo labelObject = XMLUtils.parseXMLFromFile(XMLLabelObjectInfo.class, this.labelFullPath);
        if(null == labelObject){
            throw new RuntimeException("xml label AIRCAS format error");
        }

        return labelObject;
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
                break;
            case GEOGCS:
                labelPointType = LabelPointType.GEODEGREE;
                break;
            case PROJCS:
                labelPointType = LabelPointType.PROJECTION;
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


}
