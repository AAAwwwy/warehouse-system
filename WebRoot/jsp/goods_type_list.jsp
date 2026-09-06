<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>物资分类列表</title>
    <style>
        .type-item {
            margin: 10px;
            padding: 10px;
            border: 1px solid #ccc;
            display: inline-block;
        }
    </style>
</head>
<body>
    <h2>物资分类列表</h2>
    <div>
        <c:forEach items="${typeList}" var="type">
            <div class="type-item">
                <a href="${pageContext.request.contextPath}/goods?action=listByType&type=${type}">
                    ${type}
                </a>
            </div>
        </c:forEach>
    </div>
    <br>
    <a href="${pageContext.request.contextPath}/goods?action=list">返回全部物资</a>
</body>
</html>