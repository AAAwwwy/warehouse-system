package com.warehouse.servlet;

import com.warehouse.bean.Goods;
import com.warehouse.dao.GoodsDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoodsServlet extends HttpServlet {
    private GoodsDAO goodsDAO = new GoodsDAO();
    // 日志对象，用于打印调试信息
    private static final Logger logger = Logger.getLogger(GoodsServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String action = req.getParameter("action");

        // 空值处理：默认显示列表
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                // 查询所有物资，转发到列表页
                queryGoodsList(req, resp);
                break;
            case "delete":
                // 处理删除商品的GET请求（因为是链接跳转，用GET）
                deleteGoods(req, resp);
                break;
            case "toAdd":
                // 跳转到添加商品的表单页面
                req.getRequestDispatcher("/jsp/add_goods.jsp").forward(req, resp);
                break;
            // 处理商品名称实时校验
            case "checkName":
                checkGoodsName(req, resp);
                break;
            case "typeList":
                showTypeList(req, resp);
                break;
            case "listByType":
                queryGoodsByType(req, resp);
                break;
            default:
                queryGoodsList(req, resp);
                break;
        }
    }

    private void checkGoodsName(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/plain");

        String goodsName = req.getParameter("goodsName");
        if (goodsDAO.isGoodsNameExists(goodsName)) {
            resp.getWriter().write("exists"); // 名称存在
        } else {
            resp.getWriter().write("notExists"); // 名称不存在
        }
    }

    private void showTypeList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> typeList = goodsDAO.getAllGoodsTypes();
        req.setAttribute("typeList", typeList);
        // 跳转到分类列表页面（需新建）
        req.getRequestDispatcher("/jsp/goods_type_list.jsp").forward(req, resp);
    }

    private void queryGoodsByType(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        List<Goods> goodsList = goodsDAO.getGoodsByType(type);
        req.setAttribute("goodsList", goodsList);
        req.setAttribute("currentType", type); // 保存当前分类用于页面显示
        req.getRequestDispatcher("/jsp/goods_list.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("utf-8");
        String action = req.getParameter("action");

        switch (action) {
            case "in":
                // 入库操作
                stockIn(req, resp);
                break;
            case "out":
                // 出库操作
                stockOut(req, resp);
                break;
            case "add":
                // 处理添加商品的POST请求（表单提交，用POST）
                addGoods(req, resp);
                break;
            default:
                // 默认返回列表页
                resp.sendRedirect(req.getContextPath() + "/goods?action=list");
                break;
        }
    }

    /**
     * 查询物资列表并转发到列表页
     */
    private void queryGoodsList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Goods> goodsList = goodsDAO.getAllGoods();
        req.setAttribute("goodsList", goodsList);
        req.getRequestDispatcher("/jsp/goods_list.jsp").forward(req, resp);
    }

    /**
     * 入库操作
     */
    private void stockIn(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int goodsId = Integer.parseInt(req.getParameter("goodsId"));
        int num = Integer.parseInt(req.getParameter("num"));
        boolean flag = goodsDAO.addStock(goodsId, num);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/goods?action=list");
        } else {
            resp.getWriter().write("入库失败！");
        }
    }

    /**
     * 出库操作
     */
    private void stockOut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int goodsId = Integer.parseInt(req.getParameter("goodsId"));
        int num = Integer.parseInt(req.getParameter("num"));
        boolean flag = goodsDAO.reduceStock(goodsId, num);
        if (flag) {
            resp.sendRedirect(req.getContextPath() + "/goods?action=list");
        } else {
            resp.getWriter().write("出库失败（库存不足）！");
        }
    }

    /**
     * 添加新商品（处理POST请求）
     */
    private void addGoods(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            // 获取表单提交的参数（与add_goods.jsp的表单name对应）
            String goodsName = req.getParameter("goodsName").trim(); // 去除首尾空格
            String goodsType = req.getParameter("goodsType").trim();
            int stockNum = Integer.parseInt(req.getParameter("stockNum"));

            // 校验商品名称是否重复
            if (goodsDAO.isGoodsNameExists(goodsName)) {
                req.setAttribute("msg", "商品名称已存在，无法重复添加！");
                req.setAttribute("msgType", "error");
                req.getRequestDispatcher("/jsp/add_goods.jsp").forward(req, resp);
                return; // 终止后续操作
            }

            // 封装为Goods对象
            Goods goods = new Goods();
            goods.setGoodsName(goodsName);
            goods.setGoodsType(goodsType);
            goods.setStockNum(stockNum); // 初始库存

            // 调用DAO添加商品
            boolean flag = goodsDAO.addGoods(goods);

            // 控制台打印，验证是否添加成功
            System.out.println("添加商品结果：" + flag);

            if (flag) {
                req.setAttribute("msg", "商品添加成功！");
                req.setAttribute("msgType", "success");
                // 转发到列表页，保留request参数
                queryGoodsList(req, resp);
            } else {
                req.setAttribute("msg", "添加商品失败，请重试！");
                req.setAttribute("msgType", "error");
                req.getRequestDispatcher("/jsp/add_goods.jsp").forward(req, resp);
            }
        } catch (NumberFormatException e) {
            // 处理库存数量非数字的异常
            req.setAttribute("msg", "初始库存必须是数字！");
            req.setAttribute("msgType", "error");
            req.getRequestDispatcher("/jsp/add_goods.jsp").forward(req, resp);
            // 打印异常，便于调试
            e.printStackTrace();
        }
        // 防止后续代码触发重定向
        return;
    }

    /**
     * 删除商品（处理GET请求）- 优化后
     */
    private void deleteGoods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. 获取并校验goodsId参数
        String goodsIdStr = req.getParameter("goodsId");
        if (goodsIdStr == null || goodsIdStr.trim().isEmpty()) {
            req.setAttribute("msg", "商品ID为空，删除失败！");
            req.setAttribute("msgType", "error");
            queryGoodsList(req, resp);
            return;
        }

        int goodsId;
        try {
            goodsId = Integer.parseInt(goodsIdStr.trim()); // 去除首尾空格，避免空格导致的转换失败
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "商品ID格式错误：" + goodsIdStr, e);
            req.setAttribute("msg", "商品ID格式错误（必须是数字），删除失败！");
            req.setAttribute("msgType", "error");
            queryGoodsList(req, resp);
            return;
        }

        // 2. 调用DAO删除，添加日志打印
        try {
            logger.info("开始删除商品，ID：" + goodsId);
            boolean isSuccess = goodsDAO.deleteGoods(goodsId);
            if (isSuccess) {
                req.setAttribute("msg", "商品删除成功！");
                req.setAttribute("msgType", "success");
                logger.info("商品ID：" + goodsId + " 删除成功");
            } else {
                req.setAttribute("msg", "商品删除失败！可能原因：商品不存在/库存非零/存在关联数据/数据库约束限制");
                req.setAttribute("msgType", "error");
                logger.warning("商品ID：" + goodsId + " 删除失败，DAO返回false");
            }
        } catch (Exception e) {
            // 捕获DAO层的所有异常（如SQL异常、外键约束异常）
            logger.log(Level.SEVERE, "删除商品ID：" + goodsId + " 时发生异常", e);
            // 截取异常信息，避免过长
            String errorMsg = e.getMessage() != null ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 50)) : "未知异常";
            req.setAttribute("msg", "商品删除失败！原因：" + errorMsg);
            req.setAttribute("msgType", "error");
        }

        // 3. 转发到列表页（保留提示信息）
        queryGoodsList(req, resp);
    }
}