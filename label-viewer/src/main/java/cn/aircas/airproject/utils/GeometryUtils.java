package cn.aircas.airproject.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * * @projectName launcher2
 * * @title Test
 * * @package org.dxl
 * * @description  判断两个多边形是否相交
 * * @author IT_CREAT
 * * @date  2020 2020/11/5/005 22:06
 * * @version
 */
@Slf4j
public class GeometryUtils {
    /*
    第一步：快速排除，以某一个多边形参照物，沿着此多边形画矩形，用矩形将多边形包起来，
    在判断另一个多边形是否在这个矩形框中存在顶点，没有就说明另一个矩形一定在参照矩形框的外围。
    第二步：利用的是向量叉乘求两线段是否相交，这种求法是求的绝对相交（交叉相交），只要存在多边形任意两线段相交，就一定相交，
    同时还有一种情况，就是平行或者在延长线及线段上，这时候需要排除平行和在延长线上的情况
    第三步：用点射法求某一个顶点是否在某个多边形内部，这里需要同时同时判断多变形1上的所有点都在多边形2的内部或者边线段上，
    或者是反向多边形2的所有点在多边形1的内部或边线上，二者满足其一即可；
    */
    public static void main(String[] args) {

        GeometryFactory fa = new GeometryFactory(new PrecisionModel(PrecisionModel.FLOATING), 4326);
        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(0,1),
                new Coordinate(0,10),
                new Coordinate(10,10),
                new Coordinate(10,1),
                new Coordinate(0,1)
        };
        org.locationtech.jts.geom.Polygon polygon1 = fa.createPolygon(coordinates);
        Coordinate[] coordinates2 = new Coordinate[]{
                new Coordinate(5,5),
                new Coordinate(5,20),
                new Coordinate(9,20),
                new Coordinate(10,17),
                new Coordinate(7,15),
                new Coordinate(5,5)
        };
        org.locationtech.jts.geom.Polygon polygon2 = fa.createPolygon(coordinates2);
        Geometry intersection = polygon1.intersection(polygon2);

        Coordinate[] coordinates1 = intersection.getCoordinates();
        List<String> collect = Arrays.stream(coordinates1).map(String::valueOf).collect(Collectors.toList());
        System.out.println("是否相交:" + !intersection.isEmpty());
        for (String s : collect) {
            System.out.println(s);
        }


        /*List<List<Integer>> oneList = new ArrayList<>();
        List<Integer> twoList = new ArrayList<>();
        twoList.add(1);
        twoList.add(2);
        oneList.add(twoList);
        System.out.println("更新前：" + oneList.get(0).toString());
        List<Integer> integers = oneList.get(0);
        integers.add(3);
        System.out.println("更新后：" + oneList.get(0).toString());*/

        /*Point point = new Point(0, 0.6789795646314548974);
        Polygon polygon = new Polygon().setPoints(Arrays.asList(
                new Point(0, 0),
                new Point(0, 0.5),
                new Point(0.5, 0.5),
                new Point(0.5, 0)
        ));
        // 点是否在内部
        boolean b = pointInPolygon(point, getLineSegment(polygon));
        System.out.println("点是否在内部"+b);

        Polygon polygon1 = new Polygon().setPoints(Arrays.asList(
                new Point(0.51, 0),
                new Point(0.51, 0.5),
                new Point(1, 0.5),
                new Point(1, 0)
        ));
        // 两个多边形相交
        System.out.println("两个多边形是否相交:"+intersectionJudgment(polygon, polygon1));*/
    }

    /**
     * 多边形相交判断(有一个点相交也认为相交)
     *
     * @param polygon1 多边形1
     * @param polygon2 多边形2
     * @return boolean
     */
    public static boolean intersectionJudgment(Polygon polygon1, Polygon polygon2) {
        if (isExistNull(polygon1, polygon2)) {
            return false;
        }
        if (isExistNullOrEmpty(polygon1.getPoints(), polygon2.getPoints())) {
            return false;
        }
        // 1、快速判断，如果不想交，则返回不相交
        if (!fastExclude(polygon1, polygon2)) {
            return false;
        }
        // 获取多边形线段集合
        List<LineSegment> lineSegment1 = getLineSegment(polygon1);
        List<LineSegment> lineSegment2 = getLineSegment(polygon2);
        // 存在线段集合任意一方为空，则不想交
        if (isExistNullOrEmpty(lineSegment1, lineSegment2)) {
            return false;
        }
        // 2、向量叉乘判断多边形是否存在线段相交，存在相交则返回相交
        if (crossJudgment(lineSegment1, lineSegment2)) {
            return true;
        }
        // 3、包含关系判断，分别两次判断，判断polygon1是否在polygon2内部和polygon2是否在polygon1内部，满足其一即可
        boolean isInclude = includeRelation(polygon1, lineSegment2);
        if (isInclude) {
            return true;
        }
        return includeRelation(polygon2, lineSegment1);
    }

    /**
     * 1、快速判断多边形是否相交
     *
     * @param polygon1 多边形1
     * @param polygon2 多边形2
     * @return boolean
     */
    private static boolean fastExclude(Polygon polygon1, Polygon polygon2) {
        // 多边形1
        double polygon1MaxX = polygon1.getPoints().get(0).getX();
        double polygon1MinX = polygon1.getPoints().get(0).getX();
        double polygon1MaxY = polygon1.getPoints().get(0).getY();
        double polygon1MinY = polygon1.getPoints().get(0).getY();
        for (Point point : polygon1.getPoints()) {
            polygon1MaxX = Math.max(polygon1MaxX, point.getX());
            polygon1MinX = Math.min(polygon1MinX, point.getX());
            polygon1MaxY = Math.max(polygon1MaxY, point.getY());
            polygon1MinY = Math.min(polygon1MinY, point.getY());
        }

        // 多边形2
        double polygon2MaxX = polygon2.getPoints().get(0).getX();
        double polygon2MinX = polygon2.getPoints().get(0).getX();
        double polygon2MaxY = polygon2.getPoints().get(0).getY();
        double polygon2MinY = polygon2.getPoints().get(0).getY();
        for (Point point : polygon2.getPoints()) {
            polygon2MaxX = Math.max(polygon2MaxX, point.getX());
            polygon2MinX = Math.min(polygon2MinX, point.getX());
            polygon2MaxY = Math.max(polygon2MaxY, point.getY());
            polygon2MinY = Math.min(polygon2MinY, point.getY());
        }

        // 我这里是人为临界点的点-点，点-线也是属于相交，（如过你认为不是，加上等于条件，也就是最大和最小出现任意相等也是不想交）
        // 1、多边形1的最大x比多边形2最小x还小，说明多边形1在多边形2左边，不可能相交
        // 2、多边形1的最小x比多边形2最大x还大，说明多边形1在多边形2右边，不可能相交
        // 3、多边形1的最大y比多边形2最小y还小，说明多边形1在多边形2下边，不可能相交
        // 4、多边形1的最小y比多边形2最大y还小，说明多边形1在多边形2上边，不可能相交
        return !(polygon1MaxX < polygon2MinX)
                && !(polygon1MinX > polygon2MaxX)
                && !(polygon1MaxY < polygon2MinY)
                && !(polygon1MinY > polygon2MaxY);
    }

    /**
     * 获取线段集合
     *
     * @param polygon 多边形
     * @return 线段集合
     */
    private static List<LineSegment> getLineSegment(Polygon polygon) {
        List<LineSegment> lineSegments = new ArrayList<>();
        List<Point> points = polygon.getPoints();
        // 依次链接，形成线段
        for (int i = 0; i < points.size() - 1; i++) {
            Point previousElement = points.get(i);
            Point lastElement = points.get(i + 1);
            lineSegments.add(new LineSegment(previousElement, lastElement));
        }
        // 最后一组线段（最后一个点和初始点形成最后一条线段，形成闭环）
        if (lineSegments.size() > 0) {
            Point previousElement = points.get(points.size() - 1);
            Point lastElement = points.get(0);
            lineSegments.add(new LineSegment(previousElement, lastElement));
        }
        return lineSegments;
    }

    /**
     * 2、线段集合之间是否存在相交关系
     *
     * @param lineSegments1 线段集合1（其中一个多边形的线段集合）
     * @param lineSegments2 线段集合2（另一个多边形的线段集合）
     * @return boolean
     */
    private static boolean crossJudgment(List<LineSegment> lineSegments1, List<LineSegment> lineSegments2) {
        for (LineSegment lineSegment1 : lineSegments1) {
            for (LineSegment lineSegment2 : lineSegments2) {
                // 任意一组线段相交及多边形相交，返回相交
                if (calculationLineSegmentCrossing(lineSegment1, lineSegment2)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 线段是否相交（向量叉乘判断）
     *
     * @param lineSegment1 线段1
     * @param lineSegment2 线段2
     * @return boolean
     */
    private static boolean calculationLineSegmentCrossing(LineSegment lineSegment1, LineSegment lineSegment2) {
        // 如果存在任意一点在对方线段上，则相交
        if (isPointOnline(lineSegment1, lineSegment2)) {
            return true;
        }
        // 当不存端点在线段上，则进行交叉相交判断
        // A点
        Point aPoint = lineSegment1.getPrePoint();
        // B点
        Point bPoint = lineSegment1.getLastPoint();
        // C点
        Point cPoint = lineSegment2.getPrePoint();
        // D点
        Point dPoint = lineSegment2.getLastPoint();
        // AB向量叉乘AC向量
        double bc = crossProduct(aPoint, bPoint, aPoint, cPoint);
        // AB向量叉乘AD向量
        double bd = crossProduct(aPoint, bPoint, aPoint, dPoint);
        // CD向量叉乘CA向量
        double da = crossProduct(cPoint, dPoint, cPoint, aPoint);
        // CD向量叉乘CB向量
        double db = crossProduct(cPoint, dPoint, cPoint, bPoint);
        return bc * bd < 0 && da * db < 0;
    }

    /**
     * 两线段是否存在点在对方线段上
     *
     * @param lineSegment1 线段1
     * @param lineSegment2 线段2
     * @return boolean
     */
    private static boolean isPointOnline(LineSegment lineSegment1, LineSegment lineSegment2) {
        return isExistTrue(new boolean[]{
                isCollinearIntersection(lineSegment1.getPrePoint(), lineSegment2),
                isCollinearIntersection(lineSegment1.getLastPoint(), lineSegment2),
                isCollinearIntersection(lineSegment2.getPrePoint(), lineSegment1),
                isCollinearIntersection(lineSegment2.getLastPoint(), lineSegment1)});
    }

    /**
     * 点是否在线段上
     *
     * @param point            点
     * @param lineSegmentStart 线段起始点
     * @param lineSegmentEnd   线段尾点
     * @return boolean
     */
    private static boolean isCollinearIntersection(Point point, Point lineSegmentStart, Point lineSegmentEnd) {
        // 排除在延长线上的情况
        if (point.getX() >= Math.min(lineSegmentStart.getX(), lineSegmentEnd.getX())
                && point.getX() <= Math.max(lineSegmentStart.getX(), lineSegmentEnd.getX())
                && point.getY() >= Math.min(lineSegmentStart.getY(), lineSegmentEnd.getY())
                && point.getY() <= Math.max(lineSegmentStart.getY(), lineSegmentEnd.getY())) {
            // 任意两点之间形成的向量叉乘等于0，表示在线段上或延长线上（三点共线）
            return crossProduct(point, lineSegmentStart, point, lineSegmentEnd) == 0;
        }
        return false;
    }

    /**
     * 点是否在线段上
     *
     * @param point       点
     * @param lineSegment 线段
     * @return boolean
     */
    private static boolean isCollinearIntersection(Point point, LineSegment lineSegment) {
        return isCollinearIntersection(point, lineSegment.getPrePoint(), lineSegment.getLastPoint());
    }

    /**
     * 3、多边形polygon的所有点是都在另一个多边形内部（包含关系）
     *
     * @param polygon      被包含（内部）多边形
     * @param lineSegments 包含（外部）多边形所有线段集合
     * @return boolean
     */
    private static boolean includeRelation(Polygon polygon, List<LineSegment> lineSegments) {
        List<Point> points = polygon.getPoints();
        // 所有点在内部或者线段上才算包含，返回包含关系，只要一个不满足，则返回不包含关系
        for (Point point : points) {
            if (!pointInPolygon(point, lineSegments)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断某个点是否在多边形内部
     *
     * @param point        点
     * @param lineSegments 多边形线段集合
     * @return boolean
     */
    private static boolean pointInPolygon(Point point, List<LineSegment> lineSegments) {
        // 点坐标
        double x = point.getX();
        double y = point.getY();
        // 交点个数
        int intersectionNum = 0;
        // 判断射线与多边形的交点个数
        for (LineSegment seg : lineSegments) {
            // 如果点在多边形边线上，则算在多边形内部
            if (isCollinearIntersection(point, seg.getPrePoint(), seg.getLastPoint())) {
                return true;
            }
            double maxY = Math.max(seg.getPrePoint().getY(), seg.getLastPoint().getY());
            double minY = Math.min(seg.getPrePoint().getY(), seg.getLastPoint().getY());
            if (y >= minY && y < maxY) {
                // 计算交点X坐标
                double intersectionPointX = (y - seg.getPrePoint().getY()) * (seg.getLastPoint().getX() - seg.getPrePoint().getX())
                        / (seg.getLastPoint().getY() - seg.getPrePoint().getY()) + seg.getPrePoint().getX();
                if (x > intersectionPointX) {
                    intersectionNum++;
                }
            }
        }
        return intersectionNum % 2 != 0;
    }

    /**
     * 向量叉乘
     *
     * @param point1Start 向量1起点
     * @param point1End   向量1尾点
     * @param point2Start 向量2起点
     * @param point2End   向量2尾点
     * @return 向量叉乘结果
     */
    private static double crossProduct(Point point1Start, Point point1End, Point point2Start, Point point2End) {
        // 向量a
        double aVectorX = point1End.getX() - point1Start.getX();
        double aVectorY = point1End.getY() - point1Start.getY();
        // 向量b
        double bVectorX = point2End.getX() - point2Start.getX();
        double bVectorY = point2End.getY() - point2Start.getY();
        // 向量a叉乘向量b
        return aVectorX * bVectorY - bVectorX * aVectorY;
    }

    /**
     * 是否存在空对象
     *
     * @param objects 对象集合
     * @return boolean
     */
    private static boolean isExistNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在空集合
     *
     * @param collections 集合对象
     * @return boolean
     */
    private static boolean isExistNullOrEmpty(Collection<?>... collections) {
        for (Collection<?> collection : collections) {
            if (collection == null || collection.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否存在true
     *
     * @param booleans 布尔集合
     * @return boolean
     */
    private static boolean isExistTrue(boolean[] booleans) {
        for (boolean bool : booleans) {
            if (bool) {
                return true;
            }
        }
        return false;
    }

    /**
     * 点
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class Point {
        private double x;
        private double y;
    }

    /**
     * 线段
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class LineSegment {
        private Point prePoint;
        private Point lastPoint;
    }

    /**
     * 多边形
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Accessors(chain = true)
    private static class Polygon {
        private List<Point> points;
    }
}