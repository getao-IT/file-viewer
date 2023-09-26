package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.domain.ImageInfo;
import cn.aircas.airproject.entity.emun.CoordinateSystemType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconst;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

import java.io.File;

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
        String fileSize = cn.aircas.utils.file.FileUtils.fileSizeToString(new File(imagePath).length());

        imageInfo.setSize(fileSize);
        imageInfo.setBands(dataset.getRasterCount());
        imageInfo.setWidth(dataset.getRasterXSize());
        imageInfo.setHeight(dataset.getRasterYSize());
        imageInfo.setProjection(projection);
        imageInfo.setBit(gdal.GetDataTypeName(dataset.GetRasterBand(1).getDataType()));

        double resolution = Double.parseDouble(String.format("%.2f",geoTransform[1] * 111194.872221777));
        if (geoTransform[0] > 180 )
            resolution = geoTransform[1];

        if (geoTransform[0]==0 && geoTransform[1]==1 && geoTransform[2]==0)
            resolution = 0;

        CoordinateSystemType coordinateSystem;
        double[] latLonRange = new double[]{0,0,0,0};
        double[] projectionRange  = new double[]{0,0,0,0};
        double[] rightBottom = new double[2];
        double[] leftUpPoint = new double[]{geoTransform[0],geoTransform[3]};

        //为了应对部分图像中纵轴分辨率为正的情况，使用绝对值计算经纬度范围
        rightBottom[0] = geoTransform[0] + Math.abs(imageInfo.getWidth() * geoTransform[1]) +  imageInfo.getHeight() * geoTransform[2];
        rightBottom[1] = geoTransform[3] + imageInfo.getWidth() * geoTransform[4] - Math.abs(imageInfo.getHeight() * geoTransform[5]);
        //double rightX = geoTransform[0] + imageInfo.getWidth() * geoTransform[1] + imageInfo.getHeight() * geoTransform[2];
        //double rightY = geoTransform[3] + imageInfo.getWidth() * geoTransform[4] + imageInfo.getHeight() * geoTransform[5];

        //如果为没有经纬度信息，则为像素类型
        if ((geoTransform[0] == 0 && geoTransform[2]==0 && geoTransform[3]==0 && geoTransform[4]==0) || StringUtils.isBlank(dataset.GetProjection())){
            coordinateSystem = CoordinateSystemType.PIXELCS;
        }else if (geoTransform[0] >180 && StringUtils.isNotBlank(dataset.GetProjection())){
            //如果为投影坐标,则计算经纬度范围
            projectionRange = new double[]{leftUpPoint[0],rightBottom[1],rightBottom[0],leftUpPoint[1]};
            leftUpPoint = project2LonLat(leftUpPoint[0],leftUpPoint[1],projection);
            rightBottom = project2LonLat(rightBottom[0],rightBottom[1],projection);
            latLonRange = new double[]{leftUpPoint[0],rightBottom[1],rightBottom[0],leftUpPoint[1]};
            coordinateSystem = CoordinateSystemType.PROJCS;
        }else{
            //如果为经纬度坐标，则投影坐标置空
            latLonRange = new double[]{leftUpPoint[0],rightBottom[1],rightBottom[0],leftUpPoint[1]};
            coordinateSystem = CoordinateSystemType.GEOGCS;
        }






        imageInfo.setMinLon(latLonRange[0]);
        imageInfo.setMinLat(latLonRange[1]);
        imageInfo.setMaxLon(latLonRange[2]);
        imageInfo.setMaxLat(latLonRange[3]);
        imageInfo.setResolution(resolution);
        imageInfo.setMinProjectionX(projectionRange[0]);
        imageInfo.setMinProjectionY(projectionRange[1]);
        imageInfo.setMaxProjectionX(projectionRange[2]);
        imageInfo.setMaxProjectionY(projectionRange[3]);
        imageInfo.setCoordinateSystemType(coordinateSystem);

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
        ParseImageInfo.parseInfo("D:\\temp\\Haishenwei5987__1__5000___0.tif");
    }
}
