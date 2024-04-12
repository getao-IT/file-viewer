package cn.aircas.airproject;

import cn.aircas.airproject.FileProcessApplication;
import cn.aircas.airproject.callback.impl.GrayConverCallbackImpl;
import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import cn.aircas.airproject.entity.domain.ProgressContr;
import cn.aircas.airproject.entity.domain.SaveLabelRequest;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.service.impl.ProgressServiceImpl;
import cn.aircas.airproject.utils.ImageUtil;
import cn.aircas.airproject.utils.OpenCV;
import cn.aircas.airproject.utils.SQLiteUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.DateUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileProcessApplication.class)
public class Test {

    @Autowired
    private LabelProjectService labelProjectService;

    @Autowired
    private FileProcessingService server;

    @Autowired
    private SQLiteUtils sqLiteUtils;

    @org.junit.Test
    public void createTable() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection("jdbc:sqlite:dbs/tb_label_tag.db");

        // 创建数据库
        /*String sql = "CREATE TABLE tb_gt_test (\n" +
                "\tID int4 PRIMARY KEY\n" +
                ")";*/
        String sql = "CREATE TABLE tb_label_tag_info (\n" +
                "\tid int4 PRIMARY KEY,\n" +
                "\ttag_name TEXT NOT NULL,\n" +
                "\ttag_childrens TEXT NOT NULL\n" +
                ");";
        SQLiteUtils.executeSql(sql);

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void insert() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection("jdbc:sqlite:dbs/tb_label_tag.db");

        // 插入数据
        LabelTagParent parent = new LabelTagParent();
        parent.setId(5);
        parent.setTag_name("飞机");
        parent.setTag_childrens("4,5,6");
        SQLiteUtils.insert(parent, "tb_label_tag_info");

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void query() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection("jdbc:sqlite:dbs/default.db");

        LabelTagParent labelTagParent = new LabelTagParent();
        labelTagParent.setTag_name("飞机");
        LabelTagChildren children = new LabelTagChildren();
        children.setParent_id(1);
        children.setProperties_name("巡洋舰");

        // 查询数据
        List<Object> tb_label_tag_info = SQLiteUtils.queryList(LabelTagChildren.class, "tb_label_tag_children_info", children);
        for (Object o : tb_label_tag_info) {
            System.out.println("=======================第"+tb_label_tag_info.indexOf(o)+"个数据");
            Class<?> aClass = o.getClass();
            Field[] fields = aClass.getDeclaredFields();
            for(int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                System.out.println(fields[i].getName() + ": " + fields[i].get(o));
            }
        }

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void update() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection("jdbc:sqlite:dbs/tb_label_tag.db");

        // 更新数据
        LabelTagParent updateParent = new LabelTagParent();
        updateParent.setId(5);
        updateParent.setTag_name("舰船");
        updateParent.setTag_childrens("7,8,9");
        SQLiteUtils.updateById(updateParent, "tb_label_tag_info");

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void delete() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection("jdbc:sqlite:dbs/tb_label_tag.db");

        // 删除数据
        SQLiteUtils.deleteById("tb_label_tag_parent_info", -1);

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void test() throws IOException {
        SaveLabelRequest saveLabelRequest = new SaveLabelRequest();
        String sdf = "{\"object\":[{\"id\":35,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"1045639.49981334 , 48512.49999920051\",\"1045639.4998133401 , 48534.999999200154\",\"1045602.9998134142 , 48534.99999920051\",\"1045602.9998134141 , 48512.499999200874\"]}},{\"id\":38,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"1045547.9998135255 , 48480.499999201944\",\"1045547.999813525 , 48502.99999920157\",\"1045511.4998136 , 48502.99999920192\",\"1045511.4998135989 , 48480.4999992023\"]}},{\"id\":40,\"type\":\"Rectangle\",\"coordinate\":\"geodegree\",\"description\":\"经纬度坐标\",\"possibleresult\":[{\"name\":\"未知\",\"probability\":\"1\"}],\"points\":{\"point\":[\"531887.0000000001 , 2777048.5\",\"531886.9999999993 , 2777616.5000000005\",\"533102.9999999998 , 2777616.499999999\",\"533103.0000000002 , 2777048.5\"]}}]}";
        saveLabelRequest.setLabel(sdf);
        saveLabelRequest.setSavePath("1.xml");
        saveLabelRequest.setImagePath("卡塔尔-乌代德空军基地_2017101418063800.tiff");
        saveLabelRequest.setLabelPointType(LabelPointType.GEODEGREE);
        labelProjectService.saveLabel(saveLabelRequest);

    }

    @org.junit.Test
    public void testFileProcess() {
        String filePath = "C:\\Users\\Administrator\\Desktop\\temp\\无标题.jpg";
        /*server.formatConverter(filePath, "PNG");
        System.out.println("格式转换成功！");*/
        server.grayConverter("111", filePath, "");
        System.out.println("灰度转换成功！");
    }


    @org.junit.Test
    public void testGdal() {
        String inputPath = "C:\\Users\\Administrator\\Desktop\\temp\\RD0100_DataCloud_Guangzhou_jiangmen_heshan_2023H1_4IM.tiled.deflate.tif";
        //String inputPath = "C:\\Users\\Administrator\\Desktop\\temp\\无标题.png";
        String outputPath = "C:\\Users\\Administrator\\Desktop\\temp";
        String format = "PNG";
        String s = ImageUtil.formatConvertor(inputPath, outputPath, format, null);
        System.out.println(s);
    }

    @org.junit.Test
    public void testGetProgress() {
        ProgressService service = new ProgressServiceImpl();
        ProgressContrDto progressContrDto = new ProgressContrDto();
        progressContrDto.setTaskId("111");
        List<ProgressContr> allTaskById = service.getAllTaskById(progressContrDto);
        System.out.println("获取进度成功："+allTaskById);
    }

    @org.junit.Test
    public void testGdalGray() {
        String src = "C:\\Users\\Administrator\\Desktop\\temp\\456_copy.png";
        String dst = "C:\\Users\\Administrator\\Desktop\\temp\\456_copy.png";
        OpenCV.normalize(src, dst, OpenCV.NormalizeType.MINMAX);
    }

    @org.junit.Test
    public void testImageIoGray() {
        ProgressService service = new ProgressServiceImpl();
        String src = "C:\\Users\\Administrator\\Desktop\\temp\\P_GZ_test4_2010_1107_Level_18.tif";
        File file = new File(src);
        String dst = "C:\\Users\\Administrator\\Desktop\\temp\\P_GZ_1.tif";
        File dest = new File(dst);
        ProgressContr progress = ProgressContr.builder().taskId("111222").filePath(src).consumTime(0)
                .fileName(file.getName()).taskType(TaskType.GRAY).status(TaskStatus.WORKING)
                .startTime(new Date()).progress("0%").build();
        service.createTaskById(progress);
        System.out.print("创建传输任务成功：" + progress);
        ImageUtil.grayConver(file, new GrayConverCallbackImpl(progress));
    }

    @org.junit.Test
    public void testImageIoGray1() {
        ProgressService service = new ProgressServiceImpl();
        String src = "C:\\Users\\Administrator\\Desktop\\temp\\P_GZ_test4_2010_1107_Level_18.tif";
        File file = new File(src);
        String dst = "C:\\Users\\Administrator\\Desktop\\temp\\P_GZ_1.tif";
        File dest = new File(dst);
        ProgressContr progress = ProgressContr.builder().taskId("111222").filePath(src).consumTime(0)
                .fileName(file.getName()).taskType(TaskType.GRAY).status(TaskStatus.WORKING)
                .startTime(new Date()).progress("0%").build();
        service.createTaskById(progress);
        System.out.print("创建传输任务成功：" + progress);
        ImageUtil.grayConver(file, new GrayConverCallbackImpl(progress));
    }
    @org.junit.Test
    public void testDate() throws ParseException {
//        String filePath = "C:\\Users\\Administrator\\Desktop\\temp\\456.jpg";
//        ImageUtil.grayConver(filePath);
        SaveLabelRequest saveLabelRequest = new SaveLabelRequest();
        System.out.println(JSONObject.toJSON(saveLabelRequest).toString());
    }
}

