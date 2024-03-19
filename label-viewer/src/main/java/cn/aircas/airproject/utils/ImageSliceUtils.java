package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.utils.image.geo.GeoUtils;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



/**
 * 影像标注切片工具类
 */
@Slf4j
public class ImageSliceUtils {


    /**
     * 判断单张切片是否是空白切片，仅适用四个点以上的情况
     * @return
     */
    public static boolean isBlankSleceMorethan4point(XMLLabelObjectInfo xmlLabelObjectInfo, double minX, double maxX, double minY, double maxY) {
        List<XMLLabelObjectInfo.XMLLabelObject> xmlLabelObjectList = xmlLabelObjectInfo.getXMLLabelObjectList();
        for (int i = 0; i < xmlLabelObjectList.size(); i++) {
            XMLLabelObjectInfo.XMLLabelObject xmlLabelObject = xmlLabelObjectList.get(i);
            List<String> point = xmlLabelObject.getPoints().getPoint();
            String[] coord1 = point.get(3).replace("\"", "").replace(" ", "")
                    .split(",");
            String[] coord2 = point.get(1).replace("\"", "").replace(" ", "")
                    .split(",");
            double maxLon = Double.parseDouble(coord1[0]);
            double maxLat = Double.parseDouble(coord1[1]);
            double minLon = Double.parseDouble(coord2[0]);
            double minLat = Double.parseDouble(coord2[1]);

            if (!(maxLon <= minX) && !(minLon >= maxX) && !(minLat > maxY) && !(maxLat < minY))
                return false;

        }
        return true;
    }


    /**
     * 判断单张切片是否是空白切片，仅适用四个点以上的情况
     * @return
     */
    public static boolean isBlankSlece(XMLLabelObjectInfo xmlLabelObjectInfo, double minX, double maxX, double minY, double maxY) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        Polygon polygon = geometryFactory.createPolygon(new Coordinate[]{
                new Coordinate(minX, minY), new Coordinate(minX, maxY), new Coordinate(maxX, maxY), new Coordinate(maxX, minY), new Coordinate(minX, minY)
        });
        List<XMLLabelObjectInfo.XMLLabelObject> xmlLabelObjectList = xmlLabelObjectInfo.getXMLLabelObjectList();
        for (int i = 0; i < xmlLabelObjectList.size(); i++) {
            XMLLabelObjectInfo.XMLLabelObject xmlLabelObject = xmlLabelObjectList.get(i);
            List<String> point = xmlLabelObject.getPoints().getPoint();
            List<double[]> doubles = stringPointsToDouble(point);
            if (isInSlice(doubles, polygon, geometryFactory))
                return false;
        }
        return true;
    }


    /**
     * 判断标注框是否包含在切片中
     * @return
     */
    public static boolean isInSlice(List<double[]> coords, Polygon polygon, GeometryFactory factory) {
        for (double[] coord : coords) {
            Point point = factory.createPoint(new Coordinate(coord[0], coord[1]));
            if (polygon.contains(point)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 如果两多边形相交，则返回相交坐标点
     * @param polygon1
     * @param polygon2
     * @return
     */
    public static Coordinate[] getIntersections(Polygon polygon1, Polygon polygon2) {
        Geometry intersection = polygon1.intersection(polygon2);
        if (intersection.isEmpty()) {
            return null;
        }
        return intersection.getCoordinates();
    }


    /**
     * 将 Coordinate 坐标转换为样本标注平台可以识别的坐标形式
     * @param coordinate
     * @return
     */
    public static String coordinateToPoint(Coordinate coordinate) {
        StringBuilder builder = new StringBuilder();
        builder.append(coordinate.x);
        builder.append(" , ");
        builder.append(coordinate.y);
        return builder.toString();
    }

    /**
     * 将 Coordinate 如果存储为像素坐标，则将标注XML坐标相对于切片坐标进行偏移
     * @param coordinate
     * @return
     */
    public static String excursionCoordIfPixel(Coordinate coordinate, double[] range) {
        StringBuilder builder = new StringBuilder();
        builder.append(coordinate.x - range[0]);
        builder.append(" , ");
        builder.append(coordinate.y - range[1]);
        return builder.toString();
    }


    /**
     * 从一组坐标获取标注框边界值
     * @param point
     */
    public static double[] getBoundaryFromPoints(List<String> point) {
        String[] coor = point.get(0).replace("\"", "").replace(" ", "")
                .split(",");
        double[] boundary = new double[]{Double.parseDouble(coor[0]),Double.parseDouble(coor[1]),Double.parseDouble(coor[0]),Double.parseDouble(coor[1])};

        for (int i = 1; i < point.size(); i++) {
            String[] coor1 = point.get(i).replace("\"", "").replace(" ", "")
                    .split(",");
            if (Double.parseDouble(coor1[0]) < boundary[0]) {
                boundary[0] = Double.parseDouble(coor1[0]);
            }
            if (Double.parseDouble(coor1[0]) > boundary[2]) {
                boundary[2] = Double.parseDouble(coor1[0]);
            }
            if (Double.parseDouble(coor1[1]) < boundary[1]) {
                boundary[1] = Double.parseDouble(coor1[1]);
            }
            if (Double.parseDouble(coor1[1]) > boundary[3]) {
                boundary[3] = Double.parseDouble(coor1[1]);
            }
        }
        return boundary;
    }

    /**
     * 从一组坐标获取标注框边界值
     * @param boundary
     */
    public static double[] getBoundaryFromArr(double[] boundary) {
        double temp = 0;
        if (boundary[0] > boundary[2]) {
            temp = boundary[0];
            boundary[0] = boundary[2];
            boundary[2]  = temp;
        }
        if (boundary[1] > boundary[3]) {
            temp = boundary[1];
            boundary[1] = boundary[3];
            boundary[3]  = temp;
        }
        return boundary;
    }


    /**
     * 将样本库可以识别的经纬度坐标集合，转换为像素坐标集合
     * @param point
     * @param filePath
     * @param coordType
     * @return
     */
    public static List<String> convertPixelToLonlatFromPoints(String filePath, List<String> point, String coordType) {
        List<String> newPoints = new ArrayList<>();
        for (String p : point) {
            List<Double> coord = Arrays.stream(p.replace(" ", "").split(",")).map(Double::parseDouble).collect(Collectors.toList());
            double[] coordinate = GeoUtils.pixel2Coordinate(coord.get(0), coord.get(1), filePath, coordType);
            newPoints.add(coordinate[0] + " , " + coordinate[1]);
        }
        return newPoints;
    }


    /**
     * 将样本库可以识别的经纬度坐标集合，转换为像素坐标集合
     * @param point
     * @param filePath
     * @param coordType
     * @return
     */
    public static double[] convertPixelToLonlatFromPoint(String filePath, double[] point, String coordType) {
        return GeoUtils.pixel2Coordinate(point[0], point[1], filePath, coordType);
    }


    /**
     * 将样本库可以识别的经纬度坐标集合，转换为像素坐标集合
     * @param points
     * @return
     */
    public static List<double[]> stringPointsToDouble(List<String> points) {
        List<double[]> coords = new ArrayList<>();
        for (String point : points) {
            String[] split = point.replace(" ", "").split(",");
            coords.add(new double[]{Double.parseDouble(split[0]), Double.parseDouble(split[1])});
        }
        return coords;
    }


    public static void main(String[] args) {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        Polygon polygon = factory.createPolygon(new Coordinate[]{
                new Coordinate(5,5), new Coordinate(5, 10), new Coordinate(10, 10), new Coordinate(10, 5), new Coordinate(5,5)
        });
        Point point1 = factory.createPoint(new Coordinate(6, 8));
        Point point2 = factory.createPoint(new Coordinate(5, 5));
        Point point3 = factory.createPoint(new Coordinate(8, 5));
        System.out.println("point1在矩形中吗？ " + polygon.contains(point1));
        System.out.println("point2在矩形中吗？ " + polygon.contains(point2));
        System.out.println("point3在矩形中吗？ " + polygon.contains(point3));
        System.out.println("--------------------------");
        System.out.println("point1在矩形中吗？ " + polygon.within(point1));
        System.out.println("point2在矩形中吗？ " + polygon.within(point2));
        System.out.println("point3在矩形中吗？ " + polygon.within(point3));
    }
}
