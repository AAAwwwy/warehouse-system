package com.warehouse.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库工具类（纯原生JDBC优化，不依赖连接池，解决卡顿问题）
 */
public class DBUtil {
    // ========== 请严格替换为你的MySQL配置 ==========
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL = "jdbc:mysql://localhost:3306/warehouse_db?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&rewriteBatchedStatements=true";
    private static final String USER = "root";
    private static final String PWD = "w2004ym";

    // 优化1：静态连接（单用户系统复用同一连接，避免频繁创建）
    private static Connection staticConn = null;

    // 静态代码块加载驱动（仅执行一次）
    static {
        try {
            Class.forName(DRIVER);
            System.out.println("MySQL驱动加载成功！");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败！原因：" + e.getMessage());
            e.printStackTrace();
        }
    }

    // 优化2：获取连接（优先复用静态连接，连接失效则重新创建）
    public static Connection getConn() {
        // 检查静态连接是否有效
        try {
            if (staticConn != null && !staticConn.isClosed() && staticConn.isValid(1)) {
                return staticConn; // 复用现有连接
            }
        } catch (SQLException e) {
            System.err.println("静态连接失效，重新创建...");
            e.printStackTrace();
        }

        // 重新创建连接（仅在连接失效时执行）
        try {
            staticConn = DriverManager.getConnection(URL, USER, PWD);
            // 关闭自动提交（可选，事务时手动控制）
            staticConn.setAutoCommit(true);
            System.out.println("数据库连接成功（复用模式）！");
        } catch (SQLException e) {
            System.err.println("数据库连接失败！原因：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
        return staticConn;
    }

    // 优化3：统一关闭资源（删除冗余日志，加快执行速度）
    // 批量关闭：Connection + PreparedStatement + ResultSet
    public static void close(Connection conn, PreparedStatement ps, ResultSet rs) {
        closeRs(rs);
        closePstmt(ps);
        // 静态连接不关闭（复用），仅关闭非静态连接
        if (conn != null && conn != staticConn) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 单独关闭ResultSet（适配OperRecordDAO）
    public static void closeRs(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 单独关闭PreparedStatement（适配OperRecordDAO）
    public static void closePstmt(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 优化4：保留closeConn方法（静态连接不关闭，兼容业务代码）
    public static void closeConn(Connection conn) {
        // 静态连接不关闭，避免业务代码调用后断开连接
        if (conn != null && conn != staticConn) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 可选：程序退出时关闭静态连接（用于桌面应用，Web应用可忽略）
    public static void destroy() {
        if (staticConn != null) {
            try {
                staticConn.close();
                System.out.println("静态连接已关闭！");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 测试方法（验证连接复用）
    public static void main(String[] args) {
        // 多次获取连接，验证是否复用
        for (int i = 0; i < 5; i++) {
            Connection conn = DBUtil.getConn();
            System.out.println("第" + (i + 1) + "次获取连接：" + conn);
        }
    }
}