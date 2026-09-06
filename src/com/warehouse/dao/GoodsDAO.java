package com.warehouse.dao;

import com.warehouse.bean.Goods;
import com.warehouse.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoodsDAO {
    // 查询所有物资
    public List<Goods> getAllGoods() {
        List<Goods> goodsList = new ArrayList<>();
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return goodsList;
        }
        String sql = "SELECT * FROM goods";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Goods goods = new Goods();
                goods.setGoodsId(rs.getInt("goods_id"));
                goods.setGoodsName(rs.getString("goods_name"));
                goods.setStockNum(rs.getInt("stock_num"));
                goods.setGoodsType(rs.getString("goods_type"));
                goodsList.add(goods);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return goodsList;
    }

    // 入库操作（增加库存）
    public boolean addStock(int goodsId, int num) {
        if (goodsId <= 0 || num <= 0) {
            return false;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        // 查询物资名称的SQL
        String sqlGetName = "SELECT goods_name FROM goods WHERE goods_id = ?";
        String sqlUpdate = "UPDATE goods SET stock_num = stock_num + ? WHERE goods_id = ?";
        PreparedStatement pstmtName = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            // 1. 查询物资名称
            pstmtName = conn.prepareStatement(sqlGetName);
            pstmtName.setInt(1, goodsId);
            rs = pstmtName.executeQuery();
            String goodsName = "未知物资";
            if (rs.next()) {
                goodsName = rs.getString("goods_name");
            }
            // 2. 执行入库更新
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, num);
            pstmtUpdate.setInt(2, goodsId);
            int rows = pstmtUpdate.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
            // 3. 添加操作记录（传递真实物资名称）
            OperRecordDAO recordDAO = new OperRecordDAO();
            recordDAO.addOperRecord(goodsId, "in", num, goodsName);
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmtName);
            DBUtil.closePstmt(pstmtUpdate);
            DBUtil.closeConn(conn);
        }
    }

    // 出库操作（减少库存）
    public boolean reduceStock(int goodsId, int num) {
        if (goodsId <= 0 || num <= 0) {
            return false;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        // 查询库存和物资名称的SQL
        String sqlCheck = "SELECT stock_num, goods_name FROM goods WHERE goods_id = ?";
        String sqlUpdate = "UPDATE goods SET stock_num = stock_num - ? WHERE goods_id = ? AND stock_num >= ?";
        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            // 1. 查询库存和物资名称
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setInt(1, goodsId);
            rs = pstmtCheck.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int stockNum = rs.getInt("stock_num");
            String goodsName = rs.getString("goods_name");
            if (stockNum < num) {
                conn.rollback();
                return false;
            }
            // 2. 执行出库更新
            pstmtUpdate = conn.prepareStatement(sqlUpdate);
            pstmtUpdate.setInt(1, num);
            pstmtUpdate.setInt(2, goodsId);
            pstmtUpdate.setInt(3, num);
            int rows = pstmtUpdate.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
            // 3. 添加操作记录（传递真实物资名称）
            OperRecordDAO recordDAO = new OperRecordDAO();
            recordDAO.addOperRecord(goodsId, "out", num, goodsName);
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmtCheck);
            DBUtil.closePstmt(pstmtUpdate);
            DBUtil.closeConn(conn);
        }
    }

    // 添加新商品
    public boolean addGoods(Goods goods) {
        if (goods == null || goods.getGoodsName() == null || goods.getGoodsType() == null) {
            return false;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        String sql = "INSERT INTO goods (goods_name, goods_type, stock_num) VALUES (?, ?, ?)";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            // 1. 插入新商品
            pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, goods.getGoodsName());
            pstmt.setString(2, goods.getGoodsType());
            pstmt.setInt(3, goods.getStockNum());
            int rows = pstmt.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }
            // 2. 获取自增的goods_id
            rs = pstmt.getGeneratedKeys();
            int goodsId = -1;
            if (rs.next()) {
                goodsId = rs.getInt(1);
            }
            // 3. 添加操作记录（传递真实物资名称）
            OperRecordDAO recordDAO = new OperRecordDAO();
            recordDAO.addOperRecord(goodsId, "add", goods.getStockNum(), goods.getGoodsName());
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
    }

    // 删除物资（保留操作记录，处理外键约束）
 // GoodsDAO.java 的 deleteGoods 方法
    public boolean deleteGoods(int goodsId) {
        if (goodsId <= 0) {
            return false;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        // 查询物资库存和名称的SQL
        String sqlCheck = "SELECT stock_num, goods_name FROM goods WHERE goods_id = ?";
        // 删除goods表记录的SQL（无需关闭外键）
        String sqlDelGoods = "DELETE FROM goods WHERE goods_id = ?";

        PreparedStatement pstmtCheck = null;
        PreparedStatement pstmtDelGoods = null;
        ResultSet rs = null;
        try {
            conn.setAutoCommit(false);
            // 1. 查询物资库存和名称
            pstmtCheck = conn.prepareStatement(sqlCheck);
            pstmtCheck.setInt(1, goodsId);
            rs = pstmtCheck.executeQuery();
            if (!rs.next()) {
                conn.rollback();
                return false;
            }
            int stockNum = rs.getInt("stock_num");
            String goodsName = rs.getString("goods_name");
            
            // 2. 执行删除（外键已允许删除）
            pstmtDelGoods = conn.prepareStatement(sqlDelGoods);
            pstmtDelGoods.setInt(1, goodsId);
            int rows = pstmtDelGoods.executeUpdate();
            if (rows == 0) {
                conn.rollback();
                return false;
            }

            // 3. 添加删除操作记录（保留该物资的记录）
            OperRecordDAO recordDAO = new OperRecordDAO();
            recordDAO.addOperRecord(goodsId, "delete", stockNum, goodsName);
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmtCheck);
            DBUtil.closePstmt(pstmtDelGoods);
            DBUtil.closeConn(conn);
        }
    }

    // 校验商品名称是否存在
    public boolean isGoodsNameExists(String goodsName) {
        if (goodsName == null || goodsName.trim().isEmpty()) {
            return false;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM goods WHERE goods_name = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, goodsName.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return false;
    }

    // 根据ID查询商品
    public Goods getGoodsById(int goodsId) {
        if (goodsId <= 0) {
            return null;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return null;
        }
        String sql = "SELECT * FROM goods WHERE goods_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, goodsId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Goods goods = new Goods();
                goods.setGoodsId(rs.getInt("goods_id"));
                goods.setGoodsName(rs.getString("goods_name"));
                goods.setStockNum(rs.getInt("stock_num"));
                goods.setGoodsType(rs.getString("goods_type"));
                return goods;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return null;
    }

    // 获取所有唯一的物资分类
    public List<String> getAllGoodsTypes() {
        List<String> types = new ArrayList<>();
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return types;
        }
        String sql = "SELECT DISTINCT goods_type FROM goods";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                types.add(rs.getString("goods_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return types;
    }

    // 根据分类查询物资
    public List<Goods> getGoodsByType(String type) {
        List<Goods> goodsList = new ArrayList<>();
        if (type == null || type.trim().isEmpty()) {
            return goodsList;
        }
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return goodsList;
        }
        String sql = "SELECT * FROM goods WHERE goods_type = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type.trim());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Goods goods = new Goods();
                goods.setGoodsId(rs.getInt("goods_id"));
                goods.setGoodsName(rs.getString("goods_name"));
                goods.setStockNum(rs.getInt("stock_num"));
                goods.setGoodsType(rs.getString("goods_type"));
                goodsList.add(goods);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return goodsList;
    }
}