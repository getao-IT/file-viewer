package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.SourceFileType;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Map;


@Data
public class Slice {

    /**
     * 传输任务ID
     */
    private String progressId;

    /**
     * 样本路径
     */
    private String imagePath;

    /**
     * 存储地址
     */
    private String sliceInsertPath;

    /**
     * 宽
     */
    private int width;

    /**
     * 高
     */
    private int height;

    /**
     * 步长
     */
    private int step;

    /**
     * 样本类型
     */
    private SourceFileType fileType;

    /**
     * 自定义坐标 宽 高
     */
    private Map<Integer , Integer> params;

    /**
     * 开始经度
     */
    private double minLon;

    /**
     * 开始纬度
     */
    private double minLat;

    /**
     * 结束经度
     */
    private double maxLon;

    /**
     * 结束纬度
     */
    private double maxLat;

    /**
     * 是否存储切片到样本库
     */
    private Boolean storage;

    /**
     * 重复的目标
     */
    private JSONObject sameName;

    /**
     * 重复的目标
     */
    private Map<String, Boolean> sameNames;

    /**
     * 是否生成XML
     */
    private Boolean takeLabelXml;

    /**
     * 是否保留空白切片
     */
    private Boolean retainBlankSlice;

    /**
     * xml坐标类型
     */
    private String coordinateType;
}


