package com.warehouse.servlet;

import com.warehouse.bean.User;
import com.warehouse.dao.UserDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 登录处理Servlet
 */
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1.设置请求编码（解决中文乱码）
        request.setCharacterEncoding("UTF-8");
        // 2.获取表单提交的用户名和密码
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // 3.调用UserDAO进行登录验证
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        // 4.处理验证结果
        if (user != null) {
            // 登录成功：将用户信息存入Session，跳转到物资列表页面
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", user); // 存储登录用户
            // 重定向到物资列表页面（替换为你的主页面路径）
            response.sendRedirect(request.getContextPath() + "/goods?action=list");
        } else {
            // 登录失败：返回登录页，显示错误信息
            request.setAttribute("errorMsg", "用户名或密码错误，请重新登录！");
            request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
        }
    }

    // 处理GET请求（直接跳转到登录页）
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }
}