package com.warehouse.dao;

import com.warehouse.bean.User;
import com.warehouse.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public User login(String username, String password) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            // 1.获取连接（新增：打印参数，方便调试）
            System.out.println("登录参数：username=" + username + ", password=" + password);
            conn = DBUtil.getConn();

            // 2.非空判断：连接为null直接返回
            if (conn == null) {
                System.err.println("登录失败：数据库连接为null！");
                return null;
            }

            // 3.执行SQL（确保表名是user，且字段正确）
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            // 4.处理结果集
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                System.out.println("登录成功：找到用户" + user.getUsername());
            } else {
                System.out.println("登录失败：用户名或密码错误！");
            }

        } catch (SQLException e) {
            System.err.println("登录失败：SQL执行异常！原因：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 5.关闭资源（确保方法存在且无错）
            DBUtil.close(conn, ps, rs);
        }
        return user;
    }
}