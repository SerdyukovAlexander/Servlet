<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Вход</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 40px;
        }

        .login-container {
            max-width: 400px;
            margin: 0 auto;
            padding: 20px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        label {
            display: block;
            margin-top: 10px;
        }

        input[type="text"], input[type="password"] {
            width: 100%;
            padding: 8px;
            margin-top: 4px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            margin-top: 15px;
            padding: 10px 20px;
            background-color: #0077cc;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #005fa3;
        }

        .register-link {
            margin-top: 15px;
            display: block;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="login-container">
    <h2>Вход</h2>
    <form method="post" action="${pageContext.request.contextPath}/login">
        <label>
            Логин:
            <input type="text" name="login" required>
        </label>
        <label>
            Пароль:
            <input type="password" name="password" required>
        </label>
        <input type="submit" value="Войти">
    </form>
    <div class="register-link">
        Нет аккаунта? <a href="${pageContext.request.contextPath}/register">Регистрация</a>
    </div>
</div>

</body>
</html>
