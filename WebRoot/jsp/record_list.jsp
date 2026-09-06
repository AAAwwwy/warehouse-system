<%-- warehouse/jsp/record_list.jsp --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>物资操作记录</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            padding: 20px;
            font-family: "Microsoft YaHei", sans-serif;
        }

        h2 {
            margin-bottom: 20px;
            color: #333;
        }

        .menu {
            margin-bottom: 20px;
        }

        .menu a {
            display: inline-block;
            margin-right: 15px;
            padding: 8px 15px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .menu a:hover {
            background-color: #0056b3;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        th, td {
            padding: 12px;
            text-align: center;
            border: 1px solid #dee2e6;
        }

        th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }

        tr:nth-child(even) {
            background-color: #f8f9fa;
        }

        .oper-type {
            color: white;
            padding: 3px 8px;
            border-radius: 3px;
        }

        .type-in {
            background-color: #28a745;
        }

        .type-out {
            background-color: #dc3545;
        }

        .type-add {
            background-color: #17a2b8;
        }

        .type-delete {
            background-color: #6c757d;
        }
    </style>
</head>
<body>
    <h2>物资操作记录</h2>

    <%-- 分类查看菜单 --%>
    <div class="menu">
    	<a href="${pageContext.request.contextPath}/logout" style="color: #dc3545; text-decoration: none;">退出登录</a>
    	<a href="${pageContext.request.contextPath}/goodsList">返回物资列表</a>
        <a href="${pageContext.request.contextPath}/record">所有记录</a>
        <a href="${pageContext.request.contextPath}/record?type=in">入库记录</a>
        <a href="${pageContext.request.contextPath}/record?type=out">出库记录</a>
        <a href="${pageContext.request.contextPath}/record?type=add">添加记录</a>
        <a href="${pageContext.request.contextPath}/record?type=delete">删除记录</a>
    </div>

    <%-- 操作记录表格 --%>
    <table>
        <tr>
            <%-- 适配recordId字段 --%>
            <th>记录ID</th>
            <th>物资名称</th>
            <th>操作类型</th>
            <th>操作数量</th>
            <th>操作时间</th>
        </tr>
        <%-- 遍历记录列表 --%>
        <c:forEach items="${records}" var="record">
            <tr>
                <td>${record.recordId}</td>
                <td>${record.goodsName}</td>
                <td>
                    <c:choose>
                        <c:when test="${record.operType == 'in'}">
                            <span class="oper-type type-in">入库</span>
                        </c:when>
                        <c:when test="${record.operType == 'out'}">
                            <span class="oper-type type-out">出库</span>
                        </c:when>
                        <c:when test="${record.operType == 'add'}">
                            <span class="oper-type type-add">添加</span>
                        </c:when>
                        <c:when test="${record.operType == 'delete'}">
                            <span class="oper-type type-delete">删除</span>
                        </c:when>
                        <c:otherwise>
                            <span>未知操作</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${record.operNum}</td>
                <td>${record.operTime}</td>
            </tr>
        </c:forEach>
        <%-- 无记录时显示 --%>
        <c:if test="${empty records}">
            <tr>
                <td colspan="5">暂无操作记录</td>
            </tr>
        </c:if>
    </table>
</body>
</html>