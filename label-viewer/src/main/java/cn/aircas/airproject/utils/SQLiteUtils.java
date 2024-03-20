package cn.aircas.airproject.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.sql.*;


@Slf4j
@Component
public class SQLiteUtils {

    public Connection conn = null;
    
    public Statement ptmt = null;


    /**
     * 获取连接
     * @param databasePath
     */
    @Before(value = "getSQLiteConnetion")
    public void getSQLiteConnetion(String databasePath) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(databasePath);
            ptmt = conn.createStatement();
            log.info("Getting the SQLite connection succeeded.");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * 释放连接
     */
    //@After(value = "deSQLiteConnetion")
    public void after() {
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


    public void createTable(String createSql) throws SQLException {
        try {
            conn.setAutoCommit(false);
            ptmt.execute(createSql);
            conn.commit();
            log.info("Execute SQL successfully.");
        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
        }
    }
}
