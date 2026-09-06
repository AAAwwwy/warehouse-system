<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>仓库管理系统 - 登录</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Microsoft YaHei", "Segoe UI", sans-serif;
        }

        /* 背景美化：渐变+模糊效果 */
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px;
        }

        /* 登录卡片：悬浮阴影+圆角+过渡 */
        .login-card {
            background: rgba(255, 255, 255, 0.95);
            border-radius: 16px;
            box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
            backdrop-filter: blur(8px);
            -webkit-backdrop-filter: blur(8px);
            border: 1px solid rgba(255, 255, 255, 0.18);
            width: 100%;
            max-width: 420px;
            padding: 40px 30px;
            transition: all 0.3s ease;
        }

        .login-card:hover {
            box-shadow: 0 12px 40px rgba(31, 38, 135, 0.3);
            transform: translateY(-2px);
        }

        /* 标题样式 */
        .login-title {
            text-align: center;
            margin-bottom: 30px;
            color: #2d3748;
            font-size: 24px;
            font-weight: 600;
            position: relative;
        }

        .login-title::after {
            content: '';
            display: block;
            width: 60px;
            height: 3px;
            background: linear-gradient(90deg, #667eea, #764ba2);
            margin: 10px auto 0;
            border-radius: 3px;
        }

        /* 表单组样式 */
        .form-group {
            margin-bottom: 25px;
            position: relative;
        }

        /* 输入框样式 */
        .form-input {
            width: 100%;
            height: 50px;
            padding: 0 15px 0 45px;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            font-size: 16px;
            color: #2d3748;
            background: #f8fafc;
            transition: all 0.3s ease;
            outline: none;
        }

        .form-input:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            background: #ffffff;
        }

        .form-input::placeholder {
            color: #a0aec0;
        }

        /* 输入框图标 */
        .form-icon {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #a0aec0;
            font-size: 20px;
        }

        /* 密码显示/隐藏按钮 */
        .password-toggle {
            position: absolute;
            right: 15px;
            top: 50%;
            transform: translateY(-50%);
            color: #a0aec0;
            cursor: pointer;
            font-size: 20px;
            transition: color 0.3s ease;
        }

        .password-toggle:hover {
            color: #667eea;
        }

        /* 记住密码 + 忘记密码 */
        .form-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            font-size: 14px;
        }

        .remember-me {
            display: flex;
            align-items: center;
            color: #4a5568;
            cursor: pointer;
        }

        .remember-me input {
            margin-right: 8px;
            width: 16px;
            height: 16px;
            accent-color: #667eea;
        }

        .forgot-password {
            color: #667eea;
            text-decoration: none;
            transition: color 0.3s ease;
        }

        .forgot-password:hover {
            color: #764ba2;
            text-decoration: underline;
        }

        /* 登录按钮 */
        .login-btn {
            width: 100%;
            height: 50px;
            background: linear-gradient(90deg, #667eea, #764ba2);
            border: none;
            border-radius: 8px;
            color: white;
            font-size: 18px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            margin-bottom: 20px;
        }

        .login-btn:hover {
            background: linear-gradient(90deg, #5a67d8, #6b46c1);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
        }

        .login-btn:active {
            transform: translateY(0);
        }

        /* 错误提示：动画效果 */
        .error-message {
            text-align: center;
            color: #e53e3e;
            font-size: 14px;
            padding: 10px;
            border-radius: 8px;
            background: #fef2f2;
            margin-bottom: 20px;
            animation: shake 0.5s ease-in-out, fadeIn 0.3s ease;
            display: none;
        }

        /* 错误提示显示时的样式 */
        .error-message.show {
            display: block;
        }

        /* 底部版权信息 */
        .login-footer {
            text-align: center;
            font-size: 13px;
            color: #718096;
            margin-top: 10px;
        }

        /* 动画：抖动（错误提示） */
        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        /* 动画：淡入 */
        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        /* 响应式适配：手机端 */
        @media (max-width: 480px) {
            .login-card {
                padding: 30px 20px;
            }

            .login-title {
                font-size: 22px;
            }

            .form-input {
                height: 45px;
                font-size: 15px;
            }

            .login-btn {
                height: 45px;
                font-size: 16px;
            }
        }
    </style>
    <!-- 引入图标库（用户、锁、眼睛图标） -->
    <link rel="stylesheet" href="https://cdn.bootcdn.net/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <div class="login-card">
        <h2 class="login-title">仓库管理系统</h2>

        <!-- 错误提示：有错误时显示，带动画 -->
        <% String errorMsg = (String) request.getAttribute("errorMsg"); %>
        <div class="error-message <%= errorMsg != null ? "show" : "" %>">
            <%= errorMsg != null ? errorMsg : "" %>
        </div>

        <!-- 登录表单：提交到LoginServlet -->
        <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
            <!-- 用户名输入框 -->
            <div class="form-group">
                <i class="fas fa-user form-icon"></i>
                <input type="text" class="form-input" id="username" name="username" 
                       placeholder="请输入用户名" required 
                       value="<%= request.getParameter("username") != null ? request.getParameter("username") : "" %>">
            </div>

            <!-- 密码输入框 + 显示/隐藏按钮 -->
            <div class="form-group">
                <i class="fas fa-lock form-icon"></i>
                <input type="password" class="form-input" id="password" name="password" 
                       placeholder="请输入密码" required>
                <i class="fas fa-eye-slash password-toggle" id="passwordToggle"></i>
            </div>

            <!-- 记住密码 + 忘记密码 -->
            <div class="form-options">
                <label class="remember-me">
                    <input type="checkbox" name="rememberMe" id="rememberMe">
                    记住密码
                </label>
            </div>

            <!-- 登录按钮 -->
            <button type="submit" class="login-btn">登录</button>
        </form>

        <!-- 底部版权信息 -->
        <div class="login-footer">
            © 2025 仓库管理系统 - 版权所有
        </div>
    </div>

    <script>
        // 1. 密码显示/隐藏功能
        const passwordToggle = document.getElementById('passwordToggle');
        const passwordInput = document.getElementById('password');

        passwordToggle.addEventListener('click', function() {
            // 切换密码输入框的类型
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            // 切换图标
            this.classList.toggle('fa-eye');
            this.classList.toggle('fa-eye-slash');
        });

        // 2. 表单前端验证（补充后端验证，提升用户体验）
        const loginForm = document.getElementById('loginForm');
        loginForm.addEventListener('submit', function(e) {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();

            // 简单验证：非空
            if (!username) {
                showError('请输入用户名！');
                e.preventDefault(); // 阻止表单提交
                return;
            }
            if (!password) {
                showError('请输入密码！');
                e.preventDefault();
                return;
            }
        });

        // 3. 显示错误提示的函数
        function showError(msg) {
            const errorElement = document.querySelector('.error-message');
            errorElement.textContent = msg;
            errorElement.classList.add('show');
            // 3秒后自动隐藏（可选）
            setTimeout(() => {
                errorElement.classList.remove('show');
            }, 3000);
        }

        // 4. 记住密码功能（基于本地存储，可选）
        const rememberMe = document.getElementById('rememberMe');
        // 页面加载时，读取本地存储的用户名和密码
        window.onload = function() {
            const savedUsername = localStorage.getItem('loginUsername');
            const savedPassword = localStorage.getItem('loginPassword');
            if (savedUsername && savedPassword) {
                document.getElementById('username').value = savedUsername;
                document.getElementById('password').value = savedPassword;
                rememberMe.checked = true;
            }
        };

        // 勾选记住密码时，存储到本地；取消则清除
        rememberMe.addEventListener('change', function() {
            const username = document.getElementById('username').value.trim();
            const password = document.getElementById('password').value.trim();
            if (this.checked && username && password) {
                localStorage.setItem('loginUsername', username);
                localStorage.setItem('loginPassword', password);
            } else {
                localStorage.removeItem('loginUsername');
                localStorage.removeItem('loginPassword');
            }
        });
    </script>
</body>
</html>