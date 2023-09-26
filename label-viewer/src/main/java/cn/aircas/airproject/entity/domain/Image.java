package cn.aircas.airproject.entity.domain;

import cn.aircas.utils.image.emun.CoordinateSystemType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("file_image_info")
public class Image {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * 波段
     */
    private int bands;

    /**
     * 影像宽度
     */
    private int width;

    /**
     * 位数
     */
    private String bit;

    /**
     * 影像高度
     */
    private int height;

    /**
     * 用户id
     */
    private int userId;


    /**
     * 保存路径
     */
    private String path;

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
     * 影像缩略图
     */
    private String thumb;

    /**
     * 音箱来源
     */
    private String source;

    /**
     * 是否删除
     */
    private boolean delete;


    /**
     * 标签
     */
    private String keywords;


    /**
     * 影像创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 影像上传批次
     */
    private int batchNumber;

    /**
     * 分辨率
     */
    private double resolution;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 影像名称
     */
    private String imageName;


    /**
     * 是否公开
     */
    private boolean isPublic;


    /**
     * 影像投影信息
     */
    private String projection;

    /**
     * 影像大小
     */
    private String size;


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

    /**
     * 文件长度
     */
    private long fileLength;

    /**
     * 传感器类型
     */
    private String sensorType;

    /**
     * 卫星名称
     */
    private String satelliteName;


    /**
     * 坐标系统
     */
    private CoordinateSystemType coordinateSystemType;

    /**
     * 是否为其他平台同步过来的数据
     */
    private boolean syncFileData;


    @Override
    public String toString() {
        String thumb = this.thumb == null ? "no data" : "base64Thumb";
        return "Image{" +
                "id=" + id +
                ", bands=" + bands +
                ", width=" + width +
                ", bit='" + bit + '\'' +
                ", height=" + height +
                ", userId=" + userId +
                ", path='" + path + '\'' +
                ", minLon=" + minLon +
                ", minLat=" + minLat +
                ", maxLon=" + maxLon +
                ", maxLat=" + maxLat +
                ", thumb='" + thumb + '\'' +
                ", source='" + source + '\'' +
                ", createTime=" + createTime +
                ", batchNumber=" + batchNumber +
                ", userName='" + userName + '\'' +
                ", imageName='" + imageName + '\'' +
                ", isPublic=" + isPublic +
                ", projection='" + projection + '\'' +
                ", source='" + size + '\'' +
                ", coordinateSystemType='" + coordinateSystemType + '\'' +
                ", sensor='" + sensorType + '\'' +
                ", satellite='" + satelliteName + '\'' +
                ", syncFileData='" + syncFileData + '\'' +
                '}';
    }
}
