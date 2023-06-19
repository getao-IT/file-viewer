package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.domain.ImageInfo;
import cn.aircas.airproject.entity.emun.CoordinateSystemType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

@Slf4j
public class ParseImageInfo {

    /**
     * 读取影像的地理信息
     * @param imagePath
     * @return
     */
    public static ImageInfo parseInfo(String imagePath){

        log.info("开始解析文件：{} 的地理信息",imagePath);
        gdal.AllRegister();
        ImageInfo imageInfo = new ImageInfo();
        String path = FilenameUtils.normalizeNoEndSeparator( imagePath);
        Dataset dataset = gdal.Open(path,gdalconst.GA_ReadOnly);

        if (dataset==null){
            log.error("没有找到需要parseInfo的影像路径:{}",imagePath);
            return imageInfo;
        }

        String projection = dataset.GetProjection();
        double[] geoTransform = dataset.GetGeoTransform();

        imageInfo.setBands(dataset.getRasterCount());
        imageInfo.setWidth(dataset.getRasterXSize());
        imageInfo.setHeight(dataset.getRasterYSize());
        imageInfo.setProjection(projection);
        if (imageInfo.getBands()>=1){
            imageInfo.setBit(gdal.GetDataTypeName(dataset.GetRasterBand(1).getDataType()));
        }

        double resolution = Double.parseDouble(String.format("%.2f",geoTransform[1] * 111194.872221777));
        if (geoTransform[0] > 180 )
            resolution = geoTransform[1];

        if (geoTransform[0]==0 && geoTransform[1]==1 && geoTransform[2]==0)
            resolution = 0;

        double[] leftUpPoint;
        double[] rightBottom;
        double rightX = geoTransform[0] + imageInfo.getWidth() * geoTransform[1] + imageInfo.getHeight() * geoTransform[2];
        double rightY = geoTransform[3] + imageInfo.getWidth() * geoTransform[4] + imageInfo.getHeight() * geoTransform[5];
        leftUpPoint = project2LonLat(geoTransform[0],geoTransform[3],projection);
        rightBottom = project2LonLat(rightX,rightY,projection);

        if (geoTransform[0] == 0 && geoTransform[2]==0 && geoTransform[3]==0 && geoTransform[4]==0){
            imageInfo.setCoordinateSystemType(CoordinateSystemType.PIXELCS);
            leftUpPoint = new double[]{0,0};
            rightBottom = new double[]{0,0};
        }
        else
            imageInfo.setCoordinateSystemType(CoordinateSystemType.GEOGCS);

        if (geoTransform[0] >180){
            imageInfo.setMinProjectionX(geoTransform[0]);
            imageInfo.setMinProjectionY(rightY);
            imageInfo.setMaxProjectionX(rightX);
            imageInfo.setMaxProjectionY(geoTransform[3]);
            imageInfo.setCoordinateSystemType(CoordinateSystemType.PROJCS);
        }
        imageInfo.setMinLon(leftUpPoint[0]);
        imageInfo.setMinLat(rightBottom[1]);
        imageInfo.setMaxLon(rightBottom[0]);
        imageInfo.setMaxLat(leftUpPoint[1]);
        imageInfo.setResolution(resolution);

        log.info("文件：{} 的地理信息解析完成",imagePath);
        return imageInfo;
    }

    /**
     * 投影坐标转经纬度
     * @param x
     * @param y
     * @param wtk
     * @return
     */
    private static double[] project2LonLat(double x, double y, String wtk){
        double[] lonLat = new double[]{0,0,0};
        SpatialReference srcspatialReference = new SpatialReference(wtk);
        SpatialReference destspatialReference =  srcspatialReference.CloneGeogCS();
        CoordinateTransformation coordinateTransformation = new CoordinateTransformation(srcspatialReference, destspatialReference);
        coordinateTransformation.TransformPoint(lonLat, x, y);
        return lonLat;
    }

    public static void main(String[] args) {
        ParseImageInfo.parseInfo("d:\\GF02_PA1_027435_20190916_MY350_01_024_L1A_01.tif");
    }
}
