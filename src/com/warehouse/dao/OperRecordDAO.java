package com.warehouse.dao;

import com.warehouse.bean.OperRecord;
import com.warehouse.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OperRecordDAO {
    // 带物资名称的添加操作记录方法（核心）
    public boolean addOperRecord(int goodsId, String operType, int operNum, String goodsName) {
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return false;
        }
        // SQL中新增goods_name字段的插入
        String sql = "INSERT INTO oper_record (goods_id, oper_type, oper_num, oper_time, goods_name) " +
                     "VALUES (?, ?, ?, NOW(), ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, goodsId);
            pstmt.setString(2, operType);
            pstmt.setInt(3, operNum);
            // 存入真实的物资名称，不再是默认值
            pstmt.setString(4, goodsName == null || goodsName.trim().isEmpty() ? "未知物资" : goodsName.trim());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DBUtil.closeConn(conn);
        }
    }

    // 兼容旧的无物资名称方法（内部调用带名称的方法，默认传"未知物资"）
    public void addOperRecord(int goodsId, String operType, int operNum) {
        this.addOperRecord(goodsId, operType, operNum, "未知物资");
    }

    // 查询所有操作记录
    public List<OperRecord> getAllRecords() {
        List<OperRecord> records = new ArrayList<>();
        Connection conn = DBUtil.getConn();
        if (conn == null) {
            return records;
        }
        // 查询所有记录并按操作时间倒序排列
        String sql = "SELECT record_id, goods_id, goods_name, oper_type, oper_num, oper_time FROM oper_record ORDER BY oper_time DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                OperRecord record = new OperRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setGoodsId(rs.getInt("goods_id"));
                record.setGoodsName(rs.getString("goods_name"));
                record.setOperType(rs.getString("oper_type"));
                record.setOperNum(rs.getInt("oper_num"));
                record.setOperTime(rs.getTimestamp("oper_time"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return records;
    }

    // 按操作类型查询记录
    public List<OperRecord> getRecordsByType(String operType) {
        List<OperRecord> records = new ArrayList<>();
        Connection conn = DBUtil.getConn();
        if (conn == null || operType == null || operType.trim().isEmpty()) {
            return records;
        }
        // 按类型查询并按操作时间倒序排列
        String sql = "SELECT record_id, goods_id, goods_name, oper_type, oper_num, oper_time FROM oper_record WHERE oper_type = ? ORDER BY oper_time DESC";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, operType.trim());
            rs = pstmt.executeQuery();
            pstmt.setString(1, operType.trim().toLowerCase());
            while (rs.next()) {
                OperRecord record = new OperRecord();
                record.setRecordId(rs.getInt("record_id"));
                record.setGoodsId(rs.getInt("goods_id"));
                record.setGoodsName(rs.getString("goods_name"));
                record.setOperType(rs.getString("oper_type"));
                record.setOperNum(rs.getInt("oper_num"));
                record.setOperTime(rs.getTimestamp("oper_time"));
                records.add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            DBUtil.closeRs(rs);
            DBUtil.closePstmt(pstmt);
            DBUtil.closeConn(conn);
        }
        return records;
    }
}