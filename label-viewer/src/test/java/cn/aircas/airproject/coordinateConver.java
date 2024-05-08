package cn.aircas.airproject;


import cn.aircas.utils.image.geo.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;
import org.junit.Test;

import java.util.Map;


@Slf4j
public class coordinateConver {


    /**
     * java+gdal实现影像重投影
     * https://blog.csdn.net/m0_37821031/article/details/79111051
     *
     * @param args
     */
    public static void main(String[] args) {
        // String fileName_tif = "D:\\360downloads\\LC812340_B4.TIF";
        String fileName_tif = "C:\\Users\\Administrator\\Desktop\\temp\\image\\float32pro.tif";
        // 首先是所有gdal程序都需要的注册语句：   之前的版本还需要加这句话来支持中文路径：    gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8","YES");
        gdal.AllRegister();
        // 读取影像
        Dataset hDataset = gdal.Open(fileName_tif, gdalconstConstants.GA_ReadOnly);
        // 影像投影信息
        String projection = hDataset.GetProjection();
        // System.out.println("影像投影信息" + projection);

        // 打开的影像像素、波段等信息
        int numBands = hDataset.GetRasterCount(); // 读取影像波段数
        int xSize = hDataset.GetRasterXSize(); // 栅格尺寸
        int ySize = hDataset.GetRasterYSize(); //

        //获取源图像crs     获取源影像的坐标参考
        String src_wkt = hDataset.GetProjectionRef();
        //构造投影坐标系统的空间参考(wkt)
        SpatialReference src_Crs = new SpatialReference(src_wkt);

        //设置输出图像的坐标
        SpatialReference oLatLong;
        //获取该投影坐标系统中的地理坐标系
        oLatLong = src_Crs.CloneGeogCS();
        //构造一个从投影坐标系到地理坐标系的转换关系
        CoordinateTransformation ct = new CoordinateTransformation(src_Crs, oLatLong);

        double[] geoTransform = hDataset.GetGeoTransform();
        // 图像范围
        // 东西方向空间分辨率  --->像素宽度
        double w_src = geoTransform[1];
        // 南北方向空间分辨率 ---> 像素高度
        double h_src = geoTransform[5];
        // x方向旋转角
        double xRotate = geoTransform[2];
        // y方向旋转角
        double yRotate = geoTransform[4];

        // 左上角x坐标,y坐标 ---> 影像 左上角 投影坐标
        double xmin = geoTransform[0];
        double ymax = geoTransform[3];

        // 右下角 x坐标,y坐标
        double xmax = geoTransform[0] + xSize * w_src + ySize * xRotate;
        double ymin = geoTransform[3] + xSize * yRotate + ySize * h_src;

        // 左下角 x坐标,y坐标
        double xmax2 = geoTransform[0];
        double ymin2 = geoTransform[3] + xSize * yRotate + ySize * h_src;

        // 右上角 x坐标,y坐标
        double xmax3 = geoTransform[0] + xSize * w_src + ySize * xRotate;
        double ymin3 = geoTransform[3];

        //计算目标影像的左上和右下坐标,即目标影像的仿射变换参数,投影转换为经纬度
        // xmin: 4304231.100002289, ymin: 618073.5588534473
        // xmax: 4304247.600002289, ymax: 618088.0620982191
//        double a[] = ct.TransformPoint(xmin, ymax);
//        double b[] = ct.TransformPoint(xmax, ymin);
//        double c[] = ct.TransformPoint(xmax2, ymin2);
//        double d[] = ct.TransformPoint(xmax3, ymin3);

        xmin = 4304231.100002289;
        xmax = 4304247.600002289;
        ymin = 618073.5588534473;
        ymax = 618088.0620982191;
        double a[] = ct.TransformPoint(xmin, ymax);
        double b[] = ct.TransformPoint(xmax, ymin);
        double c[] = ct.TransformPoint(xmax2, ymin2);
        double d[] = ct.TransformPoint(xmax3, ymin3);
        // double dbX[]={a[0],b[0],c[0],d[0]};
        // double dbY[]={a[1],b[1],c[1],d[1]};
        System.out.println("---------------------------------投影坐标--------------------------------");
        System.out.println("左上角投影点：" + xmin + "," + ymax);
        System.out.println("右下角投影点：" + ymax + "," + ymin);
        System.out.println("左下角投影点：" + xmax2 + "," + ymin2);
        System.out.println("右上角投影点：" + xmax3 + "," + ymin3);

        System.out.println("---------------------------------地理坐标--------------------------------");
        System.out.println("左上角经纬度：" + a[0] + "," + a[1]);
        System.out.println("右下角经纬度：" + b[0] + "," + b[1]);
        System.out.println("左下角经纬度："  + c[0] + "," + c[1]);
        System.out.println("右上角经纬度：" + d[0] + "," + d[1]);




        // System.out.println(dbX[0]+"\n");
        // System.out.println(dbX[1]+"\n");
        // System.out.println(dbX[2]+"\n");
        // System.out.println(dbX[3]+"\n");
        //
        // System.out.println(dbY[0]+"\n");
        // System.out.println(dbY[1]+"\n");
        // System.out.println(dbY[2]+"\n");
        // System.out.println(dbY[3]+"\n");

    }
}

