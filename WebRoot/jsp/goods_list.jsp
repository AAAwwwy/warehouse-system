<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>仓库物资列表</title>
    <style>
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        h2 {
            color: #333;
            margin-bottom: 20px;
        }
        table {
            width: 80%;
            border-collapse: collapse;
            background-color: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        th, td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: center;
        }
        th {
            background-color: #f0f0f0;
            font-weight: bold;
        }
        tr:hover {
            background-color: #f9f9f9;
        }
        /* 按钮样式统一 */
        .btn {
            display: inline-block;
            padding: 5px 10px;
            margin: 0 3px;
            text-decoration: none;
            color: white;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn-add {
            background-color: #4CAF50; /* 绿色：添加 */
            margin-bottom: 15px;
        }
        .btn-delete {
            background-color: #f44336; /* 红色：删除 */
        }
        .btn-in {
            background-color: #2196F3; /* 蓝色：入库 */
        }
        .btn-out {
            background-color: #ff9800; /* 橙色：出库 */
        }
        /* 鼠标悬浮按钮效果 */
        .btn:hover {
            opacity: 0.9;
            text-decoration: none;
        }
        .msg-box {
            padding: 8px 15px;
            border-radius: 4px;
            margin-bottom: 15px;
            display: inline-block;
            font-size: 14px;
        }
        /* 成功提示：绿色 */
        .msg-success {
            background-color: #e8f5e9;
            color: #2e7d32;
            border: 1px solid #c8e6c9;
        }
        /* 失败提示：红色 */
        .msg-error {
            background-color: #ffebee;
            color: #c62828;
            border: 1px solid #ffcdd2;
        }
    </style>
</head>
<body>
    <h2>仓库物资管理</h2>
    <c:if test="${not empty msg}">
        <div class="msg-box ${msgType == 'success' ? 'msg-success' : 'msg-error'}">
            ${msg}
        </div>
    </c:if>
    
    <div style="margin: 10px 0;">
    	<a href="${pageContext.request.contextPath}/logout" style="color: #dc3545; text-decoration: none;">
            退出登录
        </a>
        <a href="${pageContext.request.contextPath}/goods?action=typeList">🔍 按分类查看物资</a>
        <a href="${pageContext.request.contextPath}/record?type=in" style="margin-left: 10px; padding: 5px 10px; background: #28a745; color: white; text-decoration: none; border-radius: 3px;">
            入库记录
        </a>
        <a href="${pageContext.request.contextPath}/record?type=out" style="margin-left: 5px; padding: 5px 10px; background: #dc3545; color: white; text-decoration: none; border-radius: 3px;">
            出库记录
        </a>
    </div>
    
    <!--添加商品按钮，跳转到添加商品页面 -->
    <a href="${pageContext.request.contextPath}/jsp/add_goods.jsp" class="btn btn-add">添加新商品</a>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>物资ID</th>
            <th>物资名称</th>
            <th>库存数量</th>
            <th>物资类型</th>
            <th>操作</th>
        </tr>
        <c:forEach items="${goodsList}" var="goods">
            <tr>
                <td>${goods.goodsId}</td>
                <td>${goods.goodsName}</td>
                <td>${goods.stockNum}</td>
                <td>${goods.goodsType}</td>
                <td>
                    <!-- 入库/出库功能 -->
                    <a href="${pageContext.request.contextPath}/jsp/oper_in.jsp?goodsId=${goods.goodsId}" class="btn btn-in">入库</a>
                    <a href="${pageContext.request.contextPath}/jsp/oper_out.jsp?goodsId=${goods.goodsId}" class="btn btn-out">出库</a>
                    <!-- 删除商品功能-->
                    <a href="${pageContext.request.contextPath}/goods?action=delete&goodsId=${goods.goodsId}" 
                       class="btn btn-delete" 
                       onclick="return confirm('确定要删除该商品吗？删除后相关操作记录也会被移除！')">删除</a>
                     <a href="${pageContext.request.contextPath}/record?goodsId=${goods.goodsId}" style="margin-left: 5px; color: #17a2b8;">
                        查看操作记录
                    </a>
                       
                </td>
            </tr>
        </c:forEach>
        <!-- 无数据时的提示 -->
        <c:if test="${empty goodsList}">
            <tr>
                <td colspan="5" style="color: #999;">暂无物资数据</td>
            </tr>
        </c:if>
    </table>
</body>
</html>