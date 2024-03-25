package cn.aircas.airproject.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;



/**
 * SQLite操作类
 */
@Slf4j
@Component
public class SQLiteUtils {

    public static String parentTabelName;

    public static String childrenTabelName;

    private static String databasePath;

    public static Connection conn = null;
    
    public static Statement ptmt = null;

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

    @Value(value = "${database.databasePath}")
    public void setDatabasePath(String databasePath) {
        SQLiteUtils.databasePath = databasePath;
    }

    static {
        databasePath = "jdbc:sqlite:dbs/tb_label_tag.db";
        getSQLiteConnection(databasePath);
    }


    /**
     * 获取连接
     * @param databasePath
     */
    public static void getSQLiteConnection(String databasePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(databasePath);
            ptmt = conn.createStatement();
            log.info("Getting the SQLite connection succeeded: {}", databasePath);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
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
        }
    }


    /**
     * 创建数据库表
     * @param createSql
     * @throws SQLException
     */
    public static void createTable(String createSql) throws SQLException {
        try {
            conn.setAutoCommit(false);
            ptmt.execute(createSql);
            conn.commit();
            log.info("create table successfully: {}", createSql);
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
    }


    /**
     * 插入数据
     * @param object
     * @param tableName
     * @throws SQLException
     */
    public static void insert(Object object, String tableName) throws SQLException {
        try {
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
            insertSql.append(cols + ") ").append("VALUES("+values+");");
            ptmt.execute(insertSql.toString());
            conn.commit();
            log.info("insert successfully: {}", insertSql);
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
        }
    }


    /**
     * 查询数据返回集合
     * @param clazz
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static List<Object> queryList(Class clazz, String tableName) throws SQLException {
        List<Object> result = new ArrayList();
        try {
            StringBuilder querySql = new StringBuilder("SELECT * FROM " + tableName);
            ResultSet rs = ptmt.executeQuery(querySql.toString());
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
        }
        return result;
    }


    /**
     * 查询数据返回集合
     * @param clazz
     * @param tableName
     * @return
     * @throws SQLException
     */
    public static List<Object> queryListByCol(Class clazz, String tableName, String[] cols, Object[] values) throws SQLException {
        List<Object> result = new ArrayList();
        try {
            StringBuilder querySql = new StringBuilder("SELECT * FROM " + tableName);
            StringJoiner queryCols = new StringJoiner(" and ");
            if (cols != null && cols.length != 0) {
                querySql.append(" WHERE ");
                for (int i = 0; i < cols.length; i++) {
                    queryCols.add(cols[i] + "=" + values[i]);
                }
                querySql.append(" " + queryCols.toString() + ";");
            }
            ResultSet rs = ptmt.executeQuery(querySql.toString());
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
        }
        return result;
    }


    /**
     * 根据ID更新数据
     * @param object
     * @param tableName
     * @throws SQLException
     */
    public static void updateById(Object object, String tableName) throws SQLException {
        try {
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
            e.printStackTrace();
            conn.rollback();
        }
    }


    /**
     * 根据ID删除数据
     * @param tableName
     * @param deleteId
     * @throws SQLException
     */
    public static void deleteById(String tableName, int deleteId) throws SQLException {
        try {
            conn.setAutoCommit(false);
            String deleteSql = new String("DELETE FROM " + tableName + " WHERE id = " + deleteId + ";");
            ptmt.execute(deleteSql);
            conn.commit();
            log.info("delete successfully: {}", deleteSql);
        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
        }
    }
}
