package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.emun.CoordinateConvertType;
import cn.aircas.utils.image.geo.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;

import java.util.Map;

@Slf4j
public class LabelPointTypeConvertor {

    static {
        gdal.AllRegister();
        gdal.SetConfigOption("GDAL_PAM_ENABLED","FALSE");
    }

    public static LabelObject convertLabelPointType(String imagePath, LabelObject labelObject, CoordinateConvertType coordinateConvertType){
        String coordindate = labelObject.getCoordinate();
        Map<Integer,double[][]> labelPointMap = labelObject.getPointMap();
        switch (coordinateConvertType){
            case PIXEL_REVERSION: revisePixel(imagePath,labelPointMap); coordindate = "pixel"; break;
            case LONLAT_TO_PIXEL: lonlatToPixel(imagePath,labelPointMap); coordindate = "pixel"; break;
            case PIXEL_TO_LONLAT: pixelToLONLAT(imagePath,labelPointMap); coordindate = "geodegree"; break;
            case PROJECTION_TO_LONLAT: projectionToLONLAT(imagePath,labelPointMap); coordindate = "geodegree"; break;
            case PROJECTION_TO_PIXEL: projectionToPixel(imagePath,labelPointMap); coordindate = "pixel"; break;
            case PIXEL_TO_PROJECTION: pixelToProjection(imagePath,labelPointMap); coordindate = "geodegree"; break;
            case LONLAT_TO_PROJECTION:  lonlatToProjection(imagePath,labelPointMap); coordindate = "geodegree";  break;
            default: NO_ACTION: break;
        }
        labelObject.updatePointList(labelPointMap,coordindate);
        return labelObject;
    }

    /**
     * 经纬度转像素
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> lonlatToPixel(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 经纬度坐标---->像素转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                double[] coordination = new double[]{point[0],point[1]};
//                if (GeoUtils.isProjection(imagePath)){
//                    coordination = GeoUtils.coordinateConvertor(coordination[0],coordination[1],dataset,GeoUtils.COORDINATE_PROJECTION);
//                }
                coordination = GeoUtils.convertCoordinateToPixel(coordination[0],coordination[1],dataset,GeoUtils.COORDINATE_LONLAT);
                point[0] = coordination[0];
                point[1] = coordination[1];
            }
        }
        dataset.delete();
        log.info("影像{} 经纬度坐标---->像素转换完成",imagePath);
        return labelPointMap;
    }


    /**
     * 投影转像素
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> projectionToPixel(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 投影坐标---->像素转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                double[] coordination = new double[]{point[0],point[1]};
                coordination = GeoUtils.convertCoordinateToPixel(coordination[0],coordination[1],dataset,GeoUtils.COORDINATE_PROJECTION);
                point[0] = coordination[0];
                point[1] = coordination[1];
            }
        }
        dataset.delete();
        log.info("影像{} 投影坐标---->像素转换完成",imagePath);
        return labelPointMap;
    }

    /**
     * 像素转经纬度
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> pixelToLONLAT(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 像素---->经纬度坐标转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
//                point[1] = dataset.getRasterYSize()-point[1];
                double[] coordination = GeoUtils.pixel2Coordinate(point[0],point[1],dataset,GeoUtils.COORDINATE_LONLAT);
                point[0] = coordination[0];
                point[1] = coordination[1];
            }
        }
        dataset.delete();
        log.info("影像{} 像素---->经纬度坐标转换完成",imagePath);
        return labelPointMap;
    }

    /**
     * 像素转投影坐标
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> pixelToProjection(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 像素---->投影坐标转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                double[] coordination = GeoUtils.pixel2Coordinate(point[0],point[1],dataset,GeoUtils.COORDINATE_PROJECTION);
                point[0] = coordination[0];
                point[1] = coordination[1];
//                point[1] = dataset.getRasterYSize()-point[1];

            }
        }
        dataset.delete();
        log.info("影像{} 像素---->投影坐标转换完成",imagePath);
        return labelPointMap;
    }

    /**
     * 将经纬度转换为投影坐标
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> lonlatToProjection(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 经纬度---->投影坐标转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                double[] coordination = GeoUtils.coordinateConvertor(point[0],point[1],dataset,GeoUtils.COORDINATE_PROJECTION);
                point[0] = coordination[0];
                point[1] = coordination[1];
            }
        }
        dataset.delete();
        log.info("影像{} 经纬度---->投影坐标转换完成",imagePath);
        return labelPointMap;
    }

    /**
     * 将经纬度转换为投影坐标
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> projectionToLONLAT(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 经纬度---->投影坐标转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                double[] coordination = GeoUtils.coordinateConvertor(point[0],point[1],dataset,GeoUtils.COORDINATE_LONLAT);
                point[0] = coordination[0];
                point[1] = coordination[1];
            }
        }
        dataset.delete();
        log.info("影像{} 经纬度---->投影坐标转换完成",imagePath);
        return labelPointMap;
    }

    /**
     * 将像素坐标原点进行反转
     * @param imagePath
     * @param labelPointMap
     * @return
     */
    public static Map<Integer,double[][]> revisePixel(String imagePath, Map<Integer,double[][]> labelPointMap){
        log.info("开始进行影像{} 像素坐标原点转换",imagePath);
        Dataset dataset = gdal.Open(imagePath);
        int ySize = dataset.getRasterYSize();
        for (double[][] pointValue : labelPointMap.values()) {
            for (double[] point : pointValue) {
                point[1] = ySize - point[1];
            }
        }
        dataset.delete();
        log.info("影像{} 像素坐标原点转换完成",imagePath);
        return labelPointMap;
    }
}
