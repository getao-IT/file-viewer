package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.CoordinateSystemType;
import lombok.Data;

@Data
public class ImageInfo {
    private int bands;
    private int width;
    private int height;
    private double minLon;
    private double maxLon;
    private double minLat;
    private double maxLat;
    private String bit;
    private String size;
    private CoordinateSystemType coordinateSystemType;
    /**
     * 影像投影信息
     */
    private String projection;
    /**
     * 分辨率
     */
    private double resolution;

    /**
     * 最小投影坐标x
     */
    private double minProjectionX;

    /**
     * 最小投影坐标y
     */
    private double minProjectionY;

    /**
     * 最大投影坐标x
     */
    private double maxProjectionX;

    /**
     * 最大投影坐标x
     */
    private double maxProjectionY;
}
