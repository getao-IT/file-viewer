package cn.aircas.airproject;

import cn.aircas.airproject.FileProcessApplication;
import cn.aircas.airproject.callback.impl.GrayConverCallbackImpl;
import cn.aircas.airproject.entity.domain.*;
import cn.aircas.airproject.entity.dto.ProgressContrDto;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.entity.emun.TaskStatus;
import cn.aircas.airproject.entity.emun.TaskType;
import cn.aircas.airproject.service.FileProcessingService;
import cn.aircas.airproject.service.LabelProjectService;
import cn.aircas.airproject.service.ProgressService;
import cn.aircas.airproject.service.impl.ProgressServiceImpl;
import cn.aircas.airproject.utils.HttpUtils;
import cn.aircas.airproject.utils.ImageUtil;
import cn.aircas.airproject.utils.OpenCV;
import cn.aircas.airproject.utils.SQLiteUtils;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.utils.DateUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FileProcessApplication.class)
public class Test {


    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LabelProjectService labelProjectService;

    @Autowired
    private FileProcessingService server;

    @Autowired
    private SQLiteUtils sqLiteUtils;


    @org.junit.Test
    public void testGetVpnIp() {
        /*try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                // name:net4 (TAP-Windows Adapter V9)
                *//*if (networkInterface.getDisplayName().contains("VPN") || networkInterface.getDisplayName().contains("Open")) {
                    System.out.println("找到了一个VPN接口，名称 - "+networkInterface.getDisplayName()+"，addr - " + networkInterface.getInetAddresses());
                }*//*

                if (networkInterface.getDisplayName().equalsIgnoreCase("TAP-Windows Adapter V9")) {
                    InetAddress inetAddress = networkInterface.getInetAddresses().nextElement();
                    String clientIp = inetAddress.getHostAddress();
                    System.out.println("找到了OPEN VPN接口，名称 - "+networkInterface.getDisplayName()+"，addr - " + networkInterface.getInetAddresses());
                }

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // 跳过回环接口和非激活状态的接口
                }

                *//*if (networkInterface.isVirtual()) { // 检查是否为虚拟接口，即VPN接口
                    System.out.println("Interface: " + networkInterface.getDisplayName());
                    Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress inetAddress = addresses.nextElement();
                        System.out.println("IP Address: " + inetAddress.getHostAddress());
                    }
                }*//*
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        long start = System.currentTimeMillis();
        String clientIpFromNetwork = HttpUtils.getClientIp(request);
        System.out.println("VPN 客户端IP 1：" + clientIpFromNetwork);
        System.out.println("耗时：" + (System.currentTimeMillis() - start));

        long start1 = System.currentTimeMillis();
        String ip = HttpUtils.getClientIp(request);
        System.out.println("VPN 客户端IP 2：" + ip);
        System.out.println("耗时：" + (System.currentTimeMillis() - start1));
    }


    @org.junit.Test
    public void createTable() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection(null, "jdbc:sqlite:dbs/tb_label_tag.db");

        // 创建数据库
        /*String sql = "CREATE TABLE tb_gt_test (\n" +
                "\tID int4 PRIMARY KEY\n" +
                ")";*/
        String sql = "CREATE TABLE tb_label_tag_info (\n" +
                "\tid int4 PRIMARY KEY,\n" +
                "\ttag_name TEXT NOT NULL,\n" +
                "\ttag_childrens TEXT NOT NULL\n" +
                ");";
        SQLiteUtils.executeSql(sql, null);

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void insert() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection(null, "jdbc:sqlite:dbs/tb_label_tag.db");

        // 插入数据
        LabelTagParent parent = new LabelTagParent();
        parent.setId(5);
        parent.setTag_name("飞机");
        parent.setTag_childrens("4,5,6");
        SQLiteUtils.insert(parent, "tb_label_tag_info", null);

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void query() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection(null, "jdbc:sqlite:dbs/default.db");

        LabelTagParent labelTagParent = new LabelTagParent();
        labelTagParent.setTag_name("飞机");
        LabelTagChildren children = new LabelTagChildren();
        children.setParent_id(1);
        children.setProperties_name("巡洋舰");

        // 查询数据
        List<Object> tb_label_tag_info = SQLiteUtils.queryList(LabelTagChildren.class, "tb_label_tag_children_info", children, null);
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
        SQLiteUtils.getSQLiteConnection(null, "jdbc:sqlite:dbs/tb_label_tag.db");

        // 更新数据
        LabelTagParent updateParent = new LabelTagParent();
        updateParent.setId(5);
        updateParent.setTag_name("舰船");
        updateParent.setTag_childrens("7,8,9");
        SQLiteUtils.updateById(updateParent, "tb_label_tag_info", null);

        // 释放连接
        SQLiteUtils.deSQLiteConnection();
    }

    @org.junit.Test
    public void delete() throws SQLException, IllegalAccessException {
        // 获取连接
        SQLiteUtils.getSQLiteConnection(null, "jdbc:sqlite:dbs/tb_label_tag.db");

        // 删除数据
        SQLiteUtils.deleteById("tb_label_tag_parent_info", -1, null);

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

    @org.junit.Test
    public void testWriteFile() throws ParseException, IOException {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setSize("sdfsdfsdfsdf");
        String result = new ObjectMapper().writeValueAsString(imageInfo);
        String filePath = "C:\\Users\\Administrator\\Desktop\\temp\\test.json";

        byte[] bytes = result.getBytes("UTF-8");
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(bytes);
        /*ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.writeTo(fileOutputStream);*/
        System.out.println("输出完成");
    }

    @org.junit.Test
    public void testLabelTag() throws ParseException, IOException {
        String labelJson = " {\n" +
                "            \"id\": 1,\n" +
                "            \"tag_name\": \"舰船\",\n" +
                "            \"tag_childrens\": \"null\",\n" +
                "            \"tagChildrenValues\": [\n" +
                "                {\n" +
                "                    \"id\": 1,\n" +
                "                    \"parent_id\": 1,\n" +
                "                    \"tag_name\": \"巡洋舰150\",\n" +
                "                    \"properties_name\": \"巡洋舰150\",\n" +
                "                    \"properties_color\": \"rgb(255,255,255)\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"id\": 2,\n" +
                "                    \"parent_id\": 1,\n" +
                "                    \"tag_name\": \"驱逐舰\",\n" +
                "                    \"properties_name\": \"驱逐舰\",\n" +
                "                    \"properties_color\": \"rgb(200,255,255)\"\n" +
                "                }\n" +
                "            ]\n" +
                "        }";
        JSONObject jsonObject = JSONObject.parseObject(labelJson);
        LabelTagParent labelTagParent = jsonObject.toJavaObject(LabelTagParent.class);
        System.out.println(labelTagParent.toString());
    }
}

