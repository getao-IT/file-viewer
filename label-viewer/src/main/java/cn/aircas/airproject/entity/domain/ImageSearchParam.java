package cn.aircas.airproject.entity.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
public class ImageSearchParam {
    /**
     * 查询用户id
     */
    private int userId;

    /**
     * 最小经度
     */
    private double minLon;

    /**
     * 最小纬度
     */
    private double minLat;

    /**
     * 最大经度
     */
    private double maxLon;

    /**
     * 最大纬度
     */
    private double maxLat;

    /**
     * 來源
     */
    private String source;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 查询结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    /**
     * 查询页码
     */
    private long pageNo =1;

    /**
     * 关键字
     */
    private String keywords;

    /**
     * 查询开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    /**
     * 查询批次号
     */
    private int batchNumber;


    /**
     * 传感器类型
     */
    private String[] sensorType;

    /**
     * 卫星名称
     */
    private String[] satelliteName;

    /**
     * 传感器名称
     */
    private String sensorName;

    /**
     * 查询页数量
     */
    private int pageSize = 10;


    /**
     * 影像文件名称
     */
    private String imageName;

    /**
     * 是否公开
     */
    private boolean isPublic;

    /**
     * 是否公开查询用
     */
    private String ispub = "";

    /**
     * 影像id列表
     */
    private List<Integer> imageIdList;

    /**
     * 影像最小宽度
     */
    private int minWidth = -1;

    /**
     * 影像最大宽度
     */
    private int maxWidth = -1;

    /**
     * 影像最小高度
     */
    private int minHeight = -1;

    /**
     * 影像最大高度
     */
    private int maxHeight = -1;


    /**
     * 模糊查询输入字段，可通过影像名称，来源，关键字，用户名进行模糊查询
     */
    private List<String> searchParamList;

    public void setIspub(String ispub) {
        this.ispub = ispub;
        if (ispub.toLowerCase().equals("t")) {
            this.isPublic = true;
        }
        if (ispub.toLowerCase().equals("f"))  {
            this.isPublic = false;
        }
    }

}
