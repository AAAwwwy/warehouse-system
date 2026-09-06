package com.warehouse.servlet;

import com.warehouse.bean.OperRecord;
import com.warehouse.dao.OperRecordDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecordServlet extends HttpServlet {
    // 实例化DAO对象
    private OperRecordDAO recordDAO = new OperRecordDAO();
    // 日志对象，用于打印调试/错误信息
    private static final Logger logger = Logger.getLogger(RecordServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // 1. 彻底解决中文乱码（请求+响应）
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        // 2. 声明变量，初始化空列表（用ArrayList兼容JDK 8）
        List<OperRecord> records = new ArrayList<>();
        String type = null;

        try {
            // 获取前端传入的操作类型（type：in/out/add/delete，null表示所有）
            type = req.getParameter("type");
            // 过滤无效的类型参数（如传入空字符串、非法字符）
            type = (type == null || type.trim().isEmpty()) ? null : type.trim();

            // 3. 根据类型查询记录，添加日志
            logger.info("开始查询操作记录，类型：" + (type == null ? "所有" : type));
            if (type == null) {
                // 调用DAO的查询所有记录方法
                records = recordDAO.getAllRecords();
            } else {
                // 调用DAO的按类型查询方法
                records = recordDAO.getRecordsByType(type);
            }

            // 4. 日志打印查询结果数量
            logger.info("查询完成，共获取" + records.size() + "条记录");

        } catch (Exception e) {
            // 5. 捕获所有异常，打印日志并提示前端
            logger.log(Level.SEVERE, "查询操作记录时发生异常", e);
            // 向请求域存入错误信息，供前端显示（截取前50个字符，避免过长）
            String errorMsg = "查询记录失败：" + (e.getMessage() != null ? e.getMessage().substring(0, Math.min(e.getMessage().length(), 50)) : "未知错误");
            req.setAttribute("errorMsg", errorMsg);
            // 异常时重置为空列表，避免前端遍历时报空指针
            records = new ArrayList<>();
        }

        // 6. 把记录、当前类型、记录数量存入请求域（供前端使用）
        req.setAttribute("records", records);
        req.setAttribute("currentType", type);
        req.setAttribute("recordCount", records.size());

        // 7. 转发到记录列表页面
        req.getRequestDispatcher("/jsp/record_list.jsp").forward(req, resp);
    }

    // 兼容POST请求（如果前端用POST提交，也能处理）
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}