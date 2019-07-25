package com.eyevision.esearch.conf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description 原始操作数据库类
 * @Author caoxb
 * @since 2019-07-24 11:01
 */
@Slf4j
@Component
public class MysqlJdbcFactory {

    private static String driver = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://localhost:3306/mytest?characterEncoding=utf8&&serverTimezone=UTC";
    private static String userName = "root";
    private static String password = "cao520";

    public static Connection getConnect() {
        Connection con = null;
        try {
            Class.forName(driver);
            con = DriverManager.getConnection(url, userName, password);
            log.info("Connection Successful !");
        } catch (Exception e) {
            log.error("[Connection Error....]" + e.getMessage());
        }

        return con;
    }

    public static void getCloseAll(Connection con, Statement stat, ResultSet res) {
        try {
            if (res != null) {
                res.close();
            }
            if (stat != null) {
                stat.close();
            }
            if (con != null) {
                con.close();
            }
            log.info("[Database off...]");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
