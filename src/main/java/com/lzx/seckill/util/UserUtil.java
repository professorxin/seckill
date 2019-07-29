package com.lzx.seckill.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzx.seckill.domain.SeckillUser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*生成用户数据，用于压测*/
public class UserUtil {

    private static String PASSWORD = "000000";

    public static void createUser(int count) throws IOException, SQLException, ClassNotFoundException {
        List<SeckillUser> users = new ArrayList<>();

        //生成用户信息
        generateSeckillUserList(count, users);

        //将用户信息插入数据库
        //insertSeckillUserToDB(users);

        //模拟用户登录，生成token
        System.out.println("start to login...");
        String urlString = "http://localhost:8080/login/do_login";
        File file = new File("E:\\JAVAProject\\seckill\\tokens.txt");
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
        file.createNewFile();
        accessFile.seek(0);

        for (int i = 0; i < users.size(); i++) {
            //模拟用户登录
            SeckillUser seckillUser = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            OutputStream out = httpURLConnection.getOutputStream();
            String params = "mobile=" + seckillUser.getId() + "&password=" + MD5Util.inputPassToFormPass(PASSWORD);
            out.write(params.getBytes());
            out.flush();

            //生成token
            InputStream inputStream = httpURLConnection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                byteArrayOutputStream.write(buff, 0, len);
            }
            inputStream.close();
            byteArrayOutputStream.close();
            String resopnse = new String(byteArrayOutputStream.toByteArray());
            JSONObject jsonObject = JSON.parseObject(resopnse);
            String token = jsonObject.getString("data");
            System.out.println("create token : " + seckillUser.getId());
            //将token写入文件中
            String row = seckillUser.getId() + "," + token;
            accessFile.seek(accessFile.length());
            accessFile.write(row.getBytes());
            accessFile.write("\r\n".getBytes());
            System.out.println("write to file" + seckillUser.getId());
        }
        accessFile.close();
        System.out.println("write token to file done!");

    }


    private static void generateSeckillUserList(int count, List<SeckillUser> users) {
        for (int i = 0; i < count; i++) {
            SeckillUser seckillUser = new SeckillUser();
            seckillUser.setId(19800000000L + i);
            seckillUser.setNickname("user-" + i);
            seckillUser.setLoginCount(1);
            seckillUser.setSalt("1a2b3c4d");
            seckillUser.setRegisterDate(new Date());
            seckillUser.setPassword(MD5Util.inputPassToDbPass(PASSWORD, seckillUser.getSalt()));
            System.out.println(seckillUser.toString());
            users.add(seckillUser);
        }
    }

    private static void insertSeckillUserToDB(List<SeckillUser> users) throws SQLException, ClassNotFoundException {
        System.out.println("start insert user...");
        Connection conn = DBUtil.getConn();
        String sql = "INSERT INTO seckill_user(login_count, nickname, register_date, salt, password, id)VALUES(?,?,?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < users.size(); i++) {
            SeckillUser user = users.get(i);
            pstmt.setInt(1, user.getLoginCount());
            pstmt.setString(2, user.getNickname());
            pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
            pstmt.setString(4, user.getSalt());
            pstmt.setString(5, user.getPassword());
            pstmt.setLong(6, user.getId());
            pstmt.addBatch();
        }
        pstmt.executeBatch();
        pstmt.close();
        conn.close();
        System.out.println("insert to db done!");
    }


    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        createUser(5000);
    }
}
