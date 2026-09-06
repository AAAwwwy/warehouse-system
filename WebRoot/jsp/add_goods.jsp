<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>添加新商品</title>
    <style>
        /* 基础样式，与原有列表页视觉统一 */
        body {
            font-family: "Microsoft YaHei", Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        /* 表单容器，提升视觉体验 */
        .form-container {
            width: 450px;
            padding: 25px;
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 5px;
            box-shadow: 0 2px 3px rgba(0,0,0,0.1);
        }
        h3 {
            color: #333;
            margin-top: 0;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        /* 表单组样式 */
        .form-group {
            margin-bottom: 18px;
        }
        label {
            display: inline-block;
            width: 100px;
            text-align: right;
            margin-right: 12px;
            font-weight: 500;
            color: #555;
        }
        input {
            padding: 6px 8px;
            width: 220px;
            border: 1px solid #ddd;
            border-radius: 3px;
            font-size: 14px;
        }
        input:focus {
            outline: none;
            border-color: #2196F3;
            box-shadow: 0 0 3px rgba(33, 150, 243, 0.2);
        }
        /* 按钮样式 */
        .btn-group {
            margin-left: 112px;
            margin-top: 10px;
        }
        .btn-submit {
            padding: 7px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 14px;
            margin-right: 10px;
        }
        .btn-submit:hover {
            background-color: #45a049;
        }
        .btn-back {
            padding: 7px 20px;
            background-color: #999;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
            font-size: 14px;
            text-decoration: none;
        }
        .btn-back:hover {
            background-color: #888;
        }
        /* 错误提示样式 */
        .error-msg {
            color: #f44336;
            margin-left: 112px;
            margin-bottom: 15px;
            font-size: 14px;
        }
        /* 实时校验提示 */
        .tips-msg {
            font-size: 12px;
            margin-left: 112px;
            margin-top: 5px;
        }
        .tips-error {
            color: #f44336;
        }
        .tips-success {
            color: #4CAF50;
        }
    </style>
    <script>
        // 前端表单验证，避免无效请求
        function validateForm() {
            // 获取表单元素
            let goodsName = document.getElementById("goodsName").value.trim();
            let goodsType = document.getElementById("goodsType").value.trim();
            let stockNum = document.getElementById("stockNum").value.trim();

            // 验证商品名称
            if (goodsName === "") {
                alert("商品名称不能为空！");
                document.getElementById("goodsName").focus();
                return false;
            }
            // 验证商品类型
            if (goodsType === "") {
                alert("商品类型不能为空！");
                document.getElementById("goodsType").focus();
                return false;
            }
            // 验证库存数量
            if (stockNum === "") {
                alert("初始库存不能为空！");
                document.getElementById("stockNum").focus();
                return false;
            }
            if (isNaN(stockNum) || parseInt(stockNum) < 0) {
                alert("初始库存必须是大于等于0的数字！");
                document.getElementById("stockNum").focus();
                return false;
            }

            // 验证通过
            return true;
        }

        // ========== 新增：实时校验商品名称是否重复（AJAX） ==========
        window.onload = function() {
            let goodsNameInput = document.getElementById("goodsName");
            let tipsMsg = document.getElementById("tipsMsg");

            goodsNameInput.onblur = function() {
                let goodsName = this.value.trim();
                if (goodsName === "") {
                    tipsMsg.innerHTML = "";
                    return;
                }

                // 创建AJAX请求
                let xhr = new XMLHttpRequest();
                xhr.open("GET", "${pageContext.request.contextPath}/goods?action=checkName&goodsName=" + encodeURIComponent(goodsName), true);
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        let result = xhr.responseText;
                        if (result === "exists") {
                            tipsMsg.innerHTML = "商品名称已存在！";
                            tipsMsg.className = "tips-msg tips-error";
                        } else {
                            tipsMsg.innerHTML = "商品名称可用！";
                            tipsMsg.className = "tips-msg tips-success";
                        }
                    }
                };
                xhr.send();
            };

            // 输入时清空提示
            goodsNameInput.oninput = function() {
                document.getElementById("tipsMsg").innerHTML = "";
            };
        };
    </script>
</head>
<body>
    <div class="form-container">
        <h3>添加新商品</h3>

        <!-- 错误提示：接收后端传递的msg参数 -->
        <c:if test="${not empty msg}">
            <div class="error-msg">${msg}</div>
        </c:if>

        <!-- 表单提交到GoodsServlet，action=add -->
        <form action="${pageContext.request.contextPath}/goods" method="post" onsubmit="return validateForm()">
            <!-- 隐藏参数：指定动作为add -->
            <input type="hidden" name="action" value="add">

            <div class="form-group">
                <label for="goodsName">商品名称：</label>
                <input type="text" id="goodsName" name="goodsName" placeholder="请输入商品名称">
                <!-- 实时校验提示 -->
                <div id="tipsMsg" class="tips-msg"></div>
            </div>

            <div class="form-group">
                <label for="goodsType">商品类型：</label>
                <input type="text" id="goodsType" name="goodsType" placeholder="请输入商品类型">
            </div>

            <div class="form-group">
                <label for="stockNum">初始库存：</label>
                <input type="number" id="stockNum" name="stockNum" value="0" min="0" placeholder="请输入初始库存">
            </div>

            <div class="btn-group">
                <button type="submit" class="btn-submit">提交</button>
                <a href="${pageContext.request.contextPath}/goods?action=list" class="btn-back">返回列表</a>
            </div>
        </form>
    </div>
</body>
</html>