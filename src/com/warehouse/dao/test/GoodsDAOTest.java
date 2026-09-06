package com.warehouse.dao.test;

import com.warehouse.bean.Goods;
import com.warehouse.dao.GoodsDAO;
import com.warehouse.util.DBUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 物资操作模块单元测试类
 */
public class GoodsDAOTest extends TestCase {
    private GoodsDAO goodsDAO;
    private Connection conn; // 数据库连接，用于测试数据的初始化和清理

    /**
     * 测试初始化方法，每个测试用例执行前执行：
     * 1. 初始化GoodsDAO
     * 2. 获取数据库连接
     * 3. 清空测试表数据（保证数据隔离）
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        goodsDAO = new GoodsDAO();
        // 获取数据库连接（使用项目的DBUtil工具类）
        conn = DBUtil.getConn();
        // 清空oper_record和goods表（先清空从表，再清空主表，避免外键约束）
        clearTable("oper_record");
        clearTable("goods");
    }

    /**
     * 测试资源释放方法，每个测试用例执行后执行：
     * 1. 关闭数据库连接
     * 2. 释放GoodsDAO对象
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
        goodsDAO = null;
    }

    /**
     * 辅助方法：清空指定表的数据
     */
    private void clearTable(String tableName) throws Exception {
        String sql = "DELETE FROM " + tableName;
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.executeUpdate();
        pstmt.close();
        // 重置自增ID（MySQL），避免ID累加导致的测试问题
        if (tableName.equals("goods")) {
            pstmt = conn.prepareStatement("ALTER TABLE goods AUTO_INCREMENT = 1");
            pstmt.executeUpdate();
            pstmt.close();
        }
        if (tableName.equals("oper_record")) {
            pstmt = conn.prepareStatement("ALTER TABLE oper_record AUTO_INCREMENT = 1");
            pstmt.executeUpdate();
            pstmt.close();
        }
    }

    /**
     * 辅助方法：添加商品并返回生成的goodsId（解决硬编码ID问题）
     */
    private int addTestGoods(String goodsName, String goodsType, int stockNum) throws Exception {
        Goods goods = new Goods();
        goods.setGoodsName(goodsName);
        goods.setGoodsType(goodsType);
        goods.setStockNum(stockNum);
        // 调用addGoods添加商品
        goodsDAO.addGoods(goods);
        // 查询刚添加的商品的ID
        String sql = "SELECT goods_id FROM goods WHERE goods_name = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, goodsName);
        ResultSet rs = pstmt.executeQuery();
        int goodsId = -1;
        if (rs.next()) {
            goodsId = rs.getInt("goods_id");
        }
        rs.close();
        pstmt.close();
        return goodsId;
    }

    /**
     * 测试商品名称唯一性校验方法
     * 修复点：先添加商品，再校验，且数据隔离
     */
    public void testIsGoodsNameExists() {
        try {
            // 1. 添加测试商品：笔记本
            addTestGoods("笔记本", "文具", 50);

            // 2. 测试存在的商品名称
            boolean exists = goodsDAO.isGoodsNameExists("笔记本");
            Assert.assertEquals("存在的商品名称校验失败", true, exists);

            // 3. 测试不存在的商品名称
            exists = goodsDAO.isGoodsNameExists("书包");
            Assert.assertEquals("不存在的商品名称校验失败", false, exists);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testIsGoodsNameExists执行异常：" + e.getMessage());
        }
    }

    /**
     * 测试添加商品方法（正常添加）
     * 修复点：数据隔离，避免重复添加
     */
    public void testAddGoods() {
        try {
            Goods goods = new Goods();
            goods.setGoodsName("钢笔");
            goods.setGoodsType("文具");
            goods.setStockNum(0);
            boolean flag = goodsDAO.addGoods(goods);
            Assert.assertEquals("商品添加失败", true, flag);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAddGoods执行异常：" + e.getMessage());
        }
    }

    /**
     * 测试出库方法（库存不足场景）
     * 修复点：先添加商品（指定库存），再执行出库，避免硬编码ID
     */
    public void testReduceStock() {
        try {
            // 1. 添加测试商品：goodsId=1（因为表已清空，自增ID从1开始），库存20
            int goodsId = addTestGoods("测试商品", "测试类型", 20);

            // 2. 出库100（库存不足）
            boolean flag = goodsDAO.reduceStock(goodsId, 100);
            Assert.assertEquals("库存不足时出库应失败", false, flag);

            // 额外验证：库存仍为20（可选，增强测试严谨性）
            // 可添加查询库存的逻辑，此处省略
        } catch (Exception e) {
            e.printStackTrace();
            fail("testReduceStock执行异常：" + e.getMessage());
        }
    }

    /**
     * 测试删除商品方法
     * 修复点：先添加商品，再删除，避免硬编码ID
     */
    public void testDeleteGoods() {
        try {
            // 1. 添加测试商品
            int goodsId = addTestGoods("删除测试商品", "测试类型", 30);

            // 2. 删除商品
            boolean flag = goodsDAO.deleteGoods(goodsId);
            Assert.assertEquals("商品删除失败", true, flag);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testDeleteGoods执行异常：" + e.getMessage());
        }
    }

    /**
     * 测试入库方法
     * 修复点：先添加商品，再入库，避免硬编码ID
     */
    public void testAddStock() {
        try {
            // 1. 添加测试商品
            int goodsId = addTestGoods("入库测试商品", "测试类型", 20);

            // 2. 入库10
            boolean flag = goodsDAO.addStock(goodsId, 10);
            Assert.assertEquals("商品入库失败", true, flag);
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAddStock执行异常：" + e.getMessage());
        }
    }
}