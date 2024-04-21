package cn.aircas.airproject.service.impl;

import cn.aircas.airproject.entity.LabelFile.LabelObject;
import cn.aircas.airproject.entity.LabelFile.XMLLabelObjectInfo;
import cn.aircas.airproject.entity.domain.LabelTagChildren;
import cn.aircas.airproject.entity.domain.LabelTagParent;
import cn.aircas.airproject.entity.domain.SaveLabelRequest;
import cn.aircas.airproject.entity.dto.LabelTagDto;
import cn.aircas.airproject.entity.emun.CoordinateConvertType;
import cn.aircas.airproject.entity.emun.LabelPointType;
import cn.aircas.airproject.service.LabelTagService;
import cn.aircas.airproject.utils.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.object.SqlUpdate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service("LabelTagParent-SERVICE")
public class LabelTagParentServiceImpl implements LabelTagService<LabelTagParent> {

    private String clientIp;

    private String driverUrl;

    @Value(value = "${sys.rootPath}")
    private String rootPath;

    @Value(value = "${database.driverPath}")
    private String driverPath;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private LabelTagChildrenServiceImpl childrenService;


    @Override
    public boolean executeSql(String createSql) {
        try {
            SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
            SQLiteUtils.executeSql(createSql, request);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public int insert(LabelTagParent tagParent) {
        try {
            SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
            return SQLiteUtils.insert(tagParent, SQLiteUtils.parentTabelName, request);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }


    @Override
    public List<Object> queryList(Class clazz, Object params) {
        try {
            List<Object> queryList = SQLiteUtils.queryList(clazz, SQLiteUtils.parentTabelName, params, request);
            if (queryList != null && queryList.size() != 0) {
                return queryList;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean updateById(LabelTagParent tagParent) {
        try {
            SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
            SQLiteUtils.updateById(tagParent, SQLiteUtils.parentTabelName, request);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean deleteById(int deleteId) {
        try {
            SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
            SQLiteUtils.deleteById(SQLiteUtils.parentTabelName, deleteId, request);
            LabelTagChildren children = new LabelTagChildren();
            children.setParent_id(deleteId);
            List<Object> objects = childrenService.queryList(LabelTagChildren.class, children);
            if (objects == null)
                return true;
            for (Object object : objects) {
                LabelTagChildren e = (LabelTagChildren) object;
                childrenService.deleteById(e.getId());
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    public List<LabelTagDto> listLabelTag() {
        List<LabelTagDto> result = new ArrayList<>();
        try {
            String dbsPath = FileUtils.getStringPath(System.getProperty("user.dir"), "dbs");
            String clientDbPath = FileUtils.getStringPath(dbsPath, clientIp) + ".db";
            File clientDb = new File(clientDbPath);
            if (!clientDb.exists()) {
                String sqlPath = FileUtils.getStringPath(dbsPath, "create_table.sql");
                SQLiteUtils.executeSqlFile(clientIp, driverUrl, sqlPath);
            } else {
                SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
            }
            List<Object> tagParents = SQLiteUtils.queryList(LabelTagParent.class, SQLiteUtils.parentTabelName, null, request);
            for (Object tagParent : tagParents) {
                LabelTagParent tagp = (LabelTagParent) tagParent;
                String[] cols = new String[]{"parent_id"};
                Object[] values = new Object[]{tagp.getId()};
                List<Object> childrens = SQLiteUtils.queryListByCol(LabelTagChildren.class, SQLiteUtils.childrenTabelName, cols, values, request);
                LabelTagDto labelTagDto = new LabelTagDto();
                BeanUtils.copyProperties(tagp, labelTagDto);
                labelTagDto.setTagChildrenValues(childrens);
                result.add(labelTagDto);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }


    /**
     * 导入标签库
     *
     * @param file
     * @return
     */
    public boolean importLabelTag(MultipartFile file) {
        try {
            String dbsPath = FileUtils.getStringPath(System.getProperty("user.dir"), "dbs");
            String clientIp = HttpUtils.getClientIp(request);
            String clientDbPath = FileUtils.getStringPath(dbsPath, clientIp) + ".db";
            File clientDb = new File(clientDbPath);
            String clientUrl = SQLiteUtils.driverPath + "/" + clientIp + ".db";
            if (!clientDb.exists()) {
                String sqlPath = FileUtils.getStringPath(dbsPath, "create_table.sql");
                SQLiteUtils.executeSqlFile(clientIp, clientUrl, sqlPath);
            } else {
                SQLiteUtils.getSQLiteConnection(clientIp, clientUrl);
            }

            byte[] bytes = file.getBytes();
            String content = new String(bytes, "UTF-8");
            if (content == null || content == "" || content.length() == 0) {
                log.error("导入的文件数据为空字节：{}", file.getName());
                return false;
            }
            JSONObject labelTagJson = JSONObject.parseObject(content.substring(1, content.length() - 1));

            tagJsonToSqllite(labelTagJson);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }


    /**
     * 将tagJson内容入库，此操作会清空标签库现有数据，谨慎操作
     *
     * @param labelTagJson
     */
    private void tagJsonToSqllite(JSONObject labelTagJson) {
        JSONArray tagData = labelTagJson.getJSONArray("data");
        if (tagData == null || tagData.size() == 0)
            return;

        String deleteParent = "DELETE FROM " + SQLiteUtils.parentTabelName;
        String deleteChildren = "DELETE FROM " + SQLiteUtils.childrenTabelName;
        try {
            SQLiteUtils.executeSql(deleteParent, request);
            SQLiteUtils.executeSql(deleteChildren, request);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        for (Object tagDatum : tagData) {
            JSONObject labelTag = JSONObject.parseObject(JSON.toJSONString(tagDatum));
            LabelTagParent tagParent = labelTag.toJavaObject(LabelTagParent.class);
            int insertId = this.insert(tagParent);
            JSONArray childrenValues = labelTag.getJSONArray("tagChildrenValues");
            if (childrenValues == null || childrenValues.size() == 0)
                return;
            for (Object childrenValue : childrenValues) {
                JSONObject childrenJson = JSONObject.parseObject(JSON.toJSONString(childrenValue));
                LabelTagChildren children = childrenJson.toJavaObject(LabelTagChildren.class);
                children.setParent_id(insertId);
                childrenService.insert(children);
            }
        }
    }


    /**
     * 将tagJson内容入库，此操作会清空标签库现有数据，谨慎操作
     */
    public String exportLabelTag() {
        SQLiteUtils.getSQLiteConnection(clientIp, driverUrl);
        JSONObject result = new JSONObject();
        JSONArray data = new JSONArray();
        List<Object> objects = this.queryList(LabelTagParent.class, null);
        if (objects == null || objects.size() == 0)
            result.put("data", "[]");
        for (Object object : objects) {
            JSONObject parent = JSONObject.parseObject(JSON.toJSONString(object));
            Integer parentId = parent.getInteger("id");
            LabelTagChildren params = new LabelTagChildren();
            params.setParent_id(parentId);
            List<Object> childrens = this.childrenService.queryList(LabelTagChildren.class, params);
            if (childrens == null || childrens.size() == 0) {
                parent.put("tagChildrenValues", "[]");
                data.add(parent);
                continue;
            }
            JSONArray childrenValues = new JSONArray();
            for (Object o : childrens) {
                JSONObject children = JSONObject.parseObject(JSON.toJSONString(o));
                childrenValues.add(children);
            }
            parent.put("tagChildrenValues", childrenValues);
            data.add(parent);
        }
        result.put("data", data);
        // octet-stream
        String dbName = clientIp + "_labeltag.json";
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.setHeader("content-type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + dbName);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            String labelTag = "\"" + result.toString() + "\"";
            os.write(labelTag.getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result.toString();
    }


    @Override
    public void setIpAndDriver() {
        this.clientIp = HttpUtils.getClientIp(request);
        this.driverUrl = driverPath + "/" + clientIp + ".db";
    }

}
