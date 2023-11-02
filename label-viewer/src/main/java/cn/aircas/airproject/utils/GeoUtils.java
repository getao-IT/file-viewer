package cn.aircas.airproject.utils;

import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

/**
 * 常用的地理信息工具类，获取经纬度信息，经纬度，像素转换等
 * @author vanishrain
 */
public class GeoUtils {

    //经纬度
    public static String COORDINATE_LONLAT = "LONLAT";
    //投影
    public static String COORDINATE_PROJECTION = "PROJECTION";


    /**
     * 获取影像的经纬度范围
     * @param imageFilePath 影像路径
     * @param coordinateType 坐标类型 GeoUtils.COORDINATE_LONLAT GeoUtils.COORDINATE_PROJECTION  经纬度坐标或者投影坐标
     * @return
     */
    public static double[] getCoordinateRange(String imageFilePath, String coordinateType){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imageFilePath);
        return getCoordinateRange(dataset,coordinateType);
    }

    /**
     * 获取影像的经纬度范围
     * @param dataset gdal dataset
     * @param coordinateType 坐标类型 GeoUtils.COORDINATE_LONLAT GeoUtils.COORDINATE_PROJECTION  经纬度坐标或者投影坐标
     * @return
     */
    public static double[] getCoordinateRange(Dataset dataset, String coordinateType){
        int imageWidth = dataset.getRasterXSize();
        int imageHeight = dataset.getRasterYSize();
        double[] geoTransform = dataset.GetGeoTransform();

        double rightX = geoTransform[0] + imageWidth * geoTransform[1] + imageHeight * geoTransform[2];
        double rightY = geoTransform[3] + imageWidth * geoTransform[4] + imageHeight * geoTransform[5];
        double[] leftUpPoint = coordinateConvertor(geoTransform[0],geoTransform[3],dataset,coordinateType);
        double[] rightBottomPoint = coordinateConvertor(rightX,rightY,dataset,coordinateType);

        return new double[]{leftUpPoint[0],rightBottomPoint[1],rightBottomPoint[0],leftUpPoint[1]};
    }

    /**
     * 像素点转坐标点
     * @param x
     * @param y
     * @param dataset
     * @param coordinateType
     * @return
     */
    public static double[] pixel2Coordinate(double x, double y, Dataset dataset, String coordinateType){
        double[] geoTransform = dataset.GetGeoTransform();

        double rightX = geoTransform[0] + x * geoTransform[1] + y * geoTransform[2];
        double rightY = geoTransform[3] + x * geoTransform[4] + y * geoTransform[5];
        return coordinateConvertor(rightX,rightY,dataset,coordinateType);
    }

    /**
     * 像素点转坐标点
     * @param x
     * @param y
     * @param imagePath
     * @param coordinateType
     * @return
     */
    public static double[] pixel2Coordinate(double x, double y, String imagePath, String coordinateType){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imagePath);
        return pixel2Coordinate(x,y,dataset,coordinateType);
    }


    /**
     * 经纬度范围转像素范围
     * @param coordinateRange 经纬度范围
     * @param srcPath 影像路径
     * @return 像素范围
     */
    public static double[] lonLatRange2PixelRange(double[] coordinateRange, String srcPath){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(srcPath);
        return lonLatRange2PixelRange(coordinateRange,dataset);
    }

    /**
     * 经纬度范围转像素范围
     * @param coordinateRange 经纬度范围
     * @param dataset gdal dataset
     * @return 像素范围
     */
    public static double[] lonLatRange2PixelRange(double[] coordinateRange, Dataset dataset){
        double[] range;

        if (dataset.GetSpatialRef().IsProjected()==1){
            double[] upLeftPoint = coordinateConvertor(coordinateRange[0],coordinateRange[3],dataset, GeoUtils.COORDINATE_PROJECTION);
            double[] bottomRightPoint = coordinateConvertor(coordinateRange[2],coordinateRange[1],dataset, GeoUtils.COORDINATE_PROJECTION);
            double[] projectionRange = new double[]{upLeftPoint[0],bottomRightPoint[1],bottomRightPoint[0],upLeftPoint[1]};
            range = convertCoordinateRangeToPixelRange(projectionRange,dataset, GeoUtils.COORDINATE_PROJECTION);
        }else {
            range = convertCoordinateRangeToPixelRange(coordinateRange, dataset, GeoUtils.COORDINATE_LONLAT);
        }
        return range;
    }

    /**
     * 坐标转换 经纬度、投影坐标相互转换
     * @param x
     * @param y
     * @param imagePath 影像路径
     * @param coordinateType 坐标类型
     * @return 转换后的坐标点
     */
    public static double[] coordinateConvertor(double x, double y, String imagePath, String coordinateType){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imagePath);
        return coordinateConvertor(x,y,dataset,coordinateType);
    }


    /**
     * 坐标转换 经纬度、投影坐标相互转换
     * @param x
     * @param y
     * @param dataset gdal dataset
     * @param coordinateType 坐标类型
     * @return 转换后的坐标点
     */
    public static double[] coordinateConvertor(double x, double y, Dataset dataset, String coordinateType){
        double[] coordinate = new double[]{0,0,0};
        String projection = dataset.GetProjection();
        CoordinateTransformation coordinateTransformation;
        SpatialReference srcSpatialReference = new SpatialReference(projection);
        SpatialReference destSpatialReference =  srcSpatialReference.CloneGeogCS();

        if (COORDINATE_LONLAT.equals(coordinateType))
            coordinateTransformation = new CoordinateTransformation(srcSpatialReference, destSpatialReference);
        else
            coordinateTransformation = new CoordinateTransformation(destSpatialReference, srcSpatialReference);
        coordinateTransformation.TransformPoint(coordinate, x, y);

        return coordinate;
    }


    /**
     * 将经纬度坐标转换成像素坐标
     * @param lon
     * @param lat
     * @param dataset
     * @param coordinateType
     * @return
     */
    private static double[] convertCoordinateToPixel(double lon, double lat, Dataset dataset, String coordinateType){
        double[] srcInfo;
        int srcWidth = dataset.getRasterXSize();
        int srcHeight = dataset.getRasterYSize();

        if (coordinateType.equals(GeoUtils.COORDINATE_LONLAT))
            srcInfo = getCoordinateRange(dataset, GeoUtils.COORDINATE_LONLAT);
        else{
            double[] coordinate = coordinateConvertor(lon,lat,dataset, GeoUtils.COORDINATE_PROJECTION);
            lon = coordinate[0];
            lat = coordinate[1];
            srcInfo = getCoordinateRange(dataset, GeoUtils.COORDINATE_PROJECTION);
        }

        double srcLatRange = srcInfo[3] - srcInfo[1];
        double srcLonRange = srcInfo[2] - srcInfo[0];

        int xPixel = (int) (srcWidth * (lon - srcInfo[0]) / srcLonRange);
        int yPixel = (int) (srcHeight * (srcInfo[3] - lat) / srcLatRange);

        return new double[]{xPixel,yPixel};
    }


    /**
     * 将经纬度坐标转换成像素坐标
     * @param lon
     * @param lat
     * @param imagePath
     * @param coordinateType
     * @return
     */
    public static double[] convertCoordinateToPixel(double lon, double lat, String imagePath, String coordinateType){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imagePath);
        return convertCoordinateToPixel(lon,lat,dataset,coordinateType);
    }

    /**
     * 将经纬度范围转换为像素范围
     * @param range 经纬度坐标点
     * @param dataset 原始影像
     * @return 切片的像素范围
     */
    private static double[] convertCoordinateRangeToPixelRange(double[] range, Dataset dataset, String coordinateType){
        double[] upLeftPoint = convertCoordinateToPixel(range[0],range[3],dataset,coordinateType);
        double[] bottomRight = convertCoordinateToPixel(range[2],range[1],dataset,coordinateType);

        return new double[]{upLeftPoint[0],upLeftPoint[1],bottomRight[0],bottomRight[1]};
    }


    /**
     * 将经纬度范围转换为像素范围
     * @param range 经纬度坐标点
     * @param imagePath 原始影像路径
     * @return 切片的像素范围
     */
    private static double[] convertCoordinateRangeToPixelRange(double[] range, String imagePath, String coordinateType){
        gdal.AllRegister();
        Dataset dataset = gdal.Open(imagePath);
        return convertCoordinateRangeToPixelRange(range,dataset,coordinateType);
    }

    public static void main(String[] args) {
        gdal.AllRegister();
        Dataset dataset = gdal.Open("D:\\数据\\流程示例图像\\MBSB_KJGSB_HKQSB_11.tif");
        System.out.println("sdf");
    }
}
