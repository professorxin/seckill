package com.lzx.seckill.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    private static Properties properties;

    //加载属性配置文件
    static {
        try {
            InputStream resource = DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
            properties = new Properties();
            properties.load(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接对象
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConn() throws SQLException, ClassNotFoundException {
        String url = properties.getProperty("spring.datasource.url");
        String username = properties.getProperty("spring.datasource.username");
        String password = properties.getProperty("spring.datasource.password");
        String driver = properties.getProperty("spring.datasource.driver-class-name");
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }
}
