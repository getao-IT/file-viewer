package cn.aircas.airproject.config.labelParser.parsers.impl;

import cn.aircas.airproject.config.labelParser.parsers.LabelParser;
import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.domain.ImageInfo;
import cn.aircas.airproject.entity.emun.*;
import cn.aircas.airproject.utils.FileUtils;
import cn.aircas.airproject.utils.LabelPointTypeConvertor;
import cn.aircas.airproject.utils.ParseImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * @ClassName: AbstractLabelParser
 * @Description TODO
 * @Author yzhan
 * @Date 2024/5/27 16:12
 * @Version 1.0
 */


@Slf4j
public abstract class AbstractLabelParser implements LabelParser, InitializingBean {
    @Value(value = "${sys.rootDir}")
    protected String rootDir;
    protected String imageFullPath;

    @Override
    public String parseLabelFile(Object labelItem, String imagePath) {

        if(false == beforeLabelItemParse(labelItem, imagePath)){
            return null;
        }

        LabelObject labelObject = parseLabelFile();


        if( false == afterLabelFileParse(labelObject)) {
            return null;
        }

        return labelObject.toJSONObject().toJSONString();
    }

    protected abstract boolean beforeLabelItemParse(Object labelItem, String imagePath);

    protected abstract LabelObject parseLabelFile();

    protected boolean checkImageFile( String imagePath){
        this.imageFullPath = FileUtils.getStringPath(this.rootDir, imagePath);
        if(false == new File(this.imageFullPath).exists()){
            log.info("与标注文件匹配的图像文件{}不存在", this.imageFullPath);
            return false;
        }

        return true;
    }


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

    @Override
    public void afterPropertiesSet() throws Exception {
        if(null == this.rootDir){
            //不能为null，可以为空字符串或者非空字符串
            throw new  RuntimeException("rootDir不能为null，无法完成初始化");
        }
    }
}
