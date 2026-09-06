<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>物资出库</title>
</head>
<body>
    <h2>物资出库</h2>
    <form action="${pageContext.request.contextPath}/goods" method="post">
        <input type="hidden" name="action" value="out">
        <input type="hidden" name="goodsId" value="${param.goodsId}">
        出库数量：<input type="number" name="num" min="1" required>
        <button type="submit">确认出库</button>
    </form>
</body>
</html>