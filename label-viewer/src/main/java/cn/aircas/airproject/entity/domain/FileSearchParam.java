package cn.aircas.airproject.entity.domain;

import cn.aircas.airproject.entity.emun.SourceFileType;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



@Data
public class FileSearchParam {
    /**
     * 查询用户id
     */
    private int userId;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 最小经度
     */
    private double minLon = 181;

    /**
     * 最小纬度
     */
    private double minLat = 181;

    /**
     * 最大经度
     */
    private double maxLon = 181;

    /**
     * 最大纬度
     */
    private double maxLat = 181;


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
     * 查询开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;


    /**
     * 查询批次号
     */
    private int batchNumber;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 是否获取文件内容
     */
    private boolean content;


    /**
     * 查询页数量
     */
    private int pageSize = 10;

    /**
     * 文件类型
     */
    private SourceFileType fileType;

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
    private boolean isPublic = true;
    /**
     * 是否公开
     */


    /**
     * 是否公开查询用
     */
    private String ispub = "";

    /**
     * 模糊查询输入字段，可通过影像名称，来源，关键字，用户名进行模糊查询
     */
    private String searchParam;

    private List<String> searchParamList;

    private List<Integer> fileIdList = new ArrayList<>();

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
     * 是否测试
     */
    private boolean istest;

    /**
     * 是否重数据集查看
     * @param fileIdListStr
     */
    private boolean isFromDataset;

    public void setFileIdList(String fileIdListStr){
        if(fileIdListStr.equals(""))
            return;
        List<String> fileList = Arrays.asList(fileIdListStr.split(","));
        fileList.forEach(str->fileIdList.add(Integer.valueOf(str.trim())));
    }

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
