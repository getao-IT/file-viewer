package cn.aircas.airproject.utils;

import cn.aircas.airproject.entity.domain.LabelTagDatabaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * SQLite操作类
 */
@Slf4j
@Component
public class SQLiteUtils {

    public static String parentTabelName;

    public static String childrenTabelName;

    public static String driverPath;

    public static Connection conn = null;

    public static Statement ptmt = null;

    public static final Map<String,Connection> connPools = new ConcurrentHashMap<>();

    public String getParentTabelName() {
        return parentTabelName;
    }

    @Value(value = "${database.tagParentTabelName}")
    public void setParentTabelName(String parentTabelName) {
        SQLiteUtils.parentTabelName = parentTabelName;
    }

    @Value(value = "${database.tagChildrenTabelName}")
    public void setChildrenTabelName(String childrenTabelName) {
        SQLiteUtils.childrenTabelName = childrenTabelName;
    }

    @Value(value = "${database.driverPath}")
    public void setDriverPath(String driverPath) {
        SQLiteUtils.driverPath = driverPath;
    }


    /*static {
        driverPath = "jdbc:sqlite:dbs/default.db";
        getSQLiteConnection(driverPath);
    }*/


    /**
     * 获取连接
     *
     * @param driverPath
     */
    public static void getSQLiteConnection(String driverPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(driverPath);
            ptmt = conn.createStatement();
            log.info("Getting the SQLite connection succeeded: {}", driverPath);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 释放连接
     */
    public static void deSQLiteConnection() {
        try {
            if (ptmt != null) {
                ptmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 执行SQL
     *
     * @param createSql
     * @throws SQLException
     */
    public static void executeSql(String createSql, HttpServletRequest request) throws SQLException {
        try {
            getClientConn(request);
            conn.setAutoCommit(false);
            ptmt.execute(createSql);
            conn.commit();
            log.info("execute sql successfully: {}", createSql);
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            deSQLiteConnection();
        }
    }


    /**
     * 执行*.sql文件
     *
     * @param sqlPath
     * @throws SQLException
     */
    public static void executeSqlFile(String clientUrl, String sqlPath) throws SQLException {
        try {
            SQLiteUtils.getSQLiteConnection(clientUrl);
            ScriptRunner run = new ScriptRunner(conn);
            run.setEscapeProcessing(false);
            run.setSendFullScript(false);
            run.runScript(new InputStreamReader(new FileInputStream(sqlPath), "UTF-8"));
            log.info("execute sqlfile successfully: {}", sqlPath);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    public static List<LabelTagDatabaseInfo> listLabelTagDatabase(HttpServletRequest request) {
        List<LabelTagDatabaseInfo> dbs = null;
        try {
            String dbsPath = FileUtils.getStringPath(System.getProperty("user.dir"), "dbs");
            dbs = new ArrayList<>();
            /*String defaultDbPath = FileUtils.getStringPath(dbsPath, "default") + ".db";
            File dfDb = new File(defaultDbPath);
            if (dfDb.exists()) {
                long createTime = Files.readAttributes(Paths.get(dfDb.getPath()), BasicFileAttributes.class).creationTime().toMillis();
                LabelTagDatabaseInfo dbInfo = LabelTagDatabaseInfo.builder().ip("default").name(dfDb.getName())
                        .path(defaultDbPath).createTime(new Date(createTime)).modifyTime(new Date(dfDb.lastModified())).build();
                dbs.add(dbInfo);
            } else {
                throw new RuntimeException("默认标签数据库启动失败");
            }*/

            String clientIp = HttpUtils.getClientIp(request);
            String clientDbPath = FileUtils.getStringPath(dbsPath, clientIp) + ".db";
            File clientDb = new File(clientDbPath);
            String clientUrl = driverPath + "/" + clientIp + ".db";
            if (!clientDb.exists()) {
                String sqlPath = FileUtils.getStringPath(dbsPath, "create_table.sql");
                executeSqlFile(clientUrl, sqlPath);
            } else {
                getSQLiteConnection(clientUrl);
            }
            long createTime = Files.readAttributes(Paths.get(clientDb.getPath()), BasicFileAttributes.class).creationTime().toMillis();
            LabelTagDatabaseInfo dbInfo = LabelTagDatabaseInfo.builder().ip(clientIp).name(clientDb.getName())
                    .path(clientDbPath).createTime(new Date(createTime)).modifyTime(new Date(clientDb.lastModified())).build();
            dbs.add(dbInfo);
        } catch (IOException | SQLException e) {
            log.error("获取标签库列表并连接失败");
            throw new RuntimeException(e);
        } finally {
            deSQLiteConnection();
        }
        return dbs;
    }


    /**
     * 插入数据
     *
     * @param object
     * @param tableName
     * @throws SQLException
     */
    public static int insert(Object object, String tableName, HttpServletRequest request) throws SQLException {
        int insertId = -1;
        try {
            getClientConn(request);
            conn.setAutoCommit(false);
            StringBuilder insertSql = new StringBuilder("INSERT INTO " + tableName + "(");
            StringJoiner cols = new StringJoiner(",");
            StringJoiner values = new StringJoiner(",");
            Class<? extends Object> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                String filedName = fields[i].getName();
                if (filedName.equalsIgnoreCase("ID"))
                    continue;
                Object filedValue = fields[i].get(object);
                if (fields[i].getType() == String.class || fields[i].getType().getName().equals("java.lang.String")) {
                    filedValue = "'" + filedValue + "'";
                }
                cols.add(filedName);
                values.add(filedValue.toString());
            }
            insertSql.append(cols + ") ").append("VALUES(" + values + ");");
            ptmt.execute(insertSql.toString());
            if (ptmt.getGeneratedKeys().next())
                insertId = ptmt.getGeneratedKeys().getInt(1);
            conn.commit();
            log.info("insert successfully: {}", insertSql);
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            deSQLiteConnection();
        }
        return insertId;
    }


    /**
     * 查询数据返回集合
     *
     * @param clazz
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static List<Object> queryList(Class clazz, String tableName, Object parmas, HttpServletRequest request) throws SQLException {
        List<Object> result = new ArrayList();
        ResultSet rs = null;
        try {
            getClientConn(request);
            StringBuilder querySql = new StringBuilder("SELECT * FROM " + tableName);
            Field[] fields = clazz.getDeclaredFields();

            if (parmas != null) {
                querySql.append(" WHERE ");
                StringJoiner parmasSql = new StringJoiner(" and ");
                for (Field field : fields) {
                    field.setAccessible(true);
                    String filedName = field.getName();
                    Object value = field.get(parmas);
                    if (value != null) {
                        if (field.getType() == String.class || field.getType().getName().equals("java.lang.String")) {
                            parmasSql.add(filedName + " = '" + value + "'");
                        }
                        if(field.getType().getName().equalsIgnoreCase("int")
                                && !String.valueOf(value).equalsIgnoreCase("-1")) {
                            parmasSql.add(filedName + " = " + value);
                        }

                    }
                }
                querySql.append(parmasSql.toString());
            }

            rs = ptmt.executeQuery(querySql.toString());
            Object o = null;
            while (rs.next()) {
                o = clazz.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    String filedName = fields[i].getName();
                    Object filedValue = rs.getObject(filedName);
                    fields[i].set(o, filedValue);
                }
                result.add(o);
            }
            log.info("query successfully: {}", querySql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
            deSQLiteConnection();
        }
        return result;
    }


    /**
     * 查询数据返回集合
     *
     * @param clazz
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static List<Object> queryListByCol(Class clazz, String tableName, String[] cols, Object[] values, HttpServletRequest request) throws SQLException {
        List<Object> result = new ArrayList();
        ResultSet rs = null;
        try {
            getClientConn(request);
            StringBuilder querySql = new StringBuilder("SELECT * FROM " + tableName);
            StringJoiner queryCols = new StringJoiner(" and ");
            if (cols != null && cols.length != 0) {
                querySql.append(" WHERE ");
                for (int i = 0; i < cols.length; i++) {
                    queryCols.add(cols[i] + "=" + values[i]);
                }
                querySql.append(" " + queryCols.toString() + ";");
            }
            rs = ptmt.executeQuery(querySql.toString());
            Object o = null;
            Field[] fields = clazz.getDeclaredFields();
            while (rs.next()) {
                o = clazz.newInstance();
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    String filedName = fields[i].getName();
                    Object filedValue = rs.getObject(filedName);
                    fields[i].set(o, filedValue);
                }
                result.add(o);
            }
            log.info("query successfully: {}", querySql);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
            deSQLiteConnection();
        }
        return result;
    }


    /**
     * 根据ID更新数据
     *
     * @param object
     * @param tableName
     * @throws SQLException
     */
    public static void updateById(Object object, String tableName, HttpServletRequest request) throws SQLException {
        try {
            getClientConn(request);
            conn.setAutoCommit(false);
            StringBuilder updateSql = new StringBuilder("UPDATE " + tableName + " set ");
            StringJoiner updates = new StringJoiner(",");
            Class<? extends Object> clazz = object.getClass();
            Field[] fields = clazz.getDeclaredFields();
            int updateId = 0;
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                String filedName = fields[i].getName();
                if (filedName.equalsIgnoreCase("id")) {
                    updateId = (int) fields[i].get(object);
                    continue;
                }
                Object filedValue = fields[i].get(object);
                if (fields[i].getType() == String.class || fields[i].getType().getName().equals("java.lang.String")) {
                    filedValue = "'" + filedValue + "'";
                }
                updates.add(filedName + " = " + filedValue);
            }
            updateSql.append(updates).append(" WHERE id = " + updateId);
            ptmt.execute(updateSql.toString());
            conn.commit();
            log.info("update successfully: {}", updateSql);
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            deSQLiteConnection();
        }
    }


    /**
     * 根据ID删除数据
     *
     * @param tableName
     * @param deleteId
     * @throws SQLException
     */
    public static void deleteById(String tableName, int deleteId, HttpServletRequest request) throws SQLException {
        try {
            getClientConn(request);
            conn.setAutoCommit(false);
            String deleteSql = new String("DELETE FROM " + tableName + " WHERE id = " + deleteId + ";");
            ptmt.execute(deleteSql);
            conn.commit();
            log.info("delete successfully: {}", deleteSql);
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            deSQLiteConnection();
        }
    }

    private static void getClientConn(HttpServletRequest request) {
        String clientIp = HttpUtils.getClientIp(request);
        String dbsPath = FileUtils.getStringPath(System.getProperty("user.dir"), "dbs");
        String clientDbPath = FileUtils.getStringPath(dbsPath, clientIp) + ".db";
        File clientDb = new File(clientDbPath);
        String clientUrl = driverPath + "/" + clientIp + ".db";
        if (!clientDb.exists()) {
            try {
                String sqlPath = FileUtils.getStringPath(dbsPath, "create_table.sql");
                executeSqlFile(clientUrl, sqlPath);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            getSQLiteConnection(clientUrl);
        }
    }
}
