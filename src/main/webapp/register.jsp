<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Регистрация</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 40px;
        }

        .register-container {
            max-width: 400px;
            margin: 0 auto;
            padding: 20px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }

        .error {
            color: red;
            margin-bottom: 10px;
        }

        .success {
            color: green;
            margin-bottom: 10px;
        }

        label {
            display: block;
            margin-top: 10px;
        }

        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100%;
            padding: 8px;
            margin-top: 4px;
            box-sizing: border-box;
        }

        input[type="submit"] {
            margin-top: 15px;
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #218838;
        }

        .login-link {
            margin-top: 15px;
            display: block;
            text-align: center;
        }
    </style>
</head>
<body>

<div class="register-container">
    <h2>Регистрация</h2>

    <!-- Сообщения об ошибках -->
    <%
        String error = request.getParameter("error");
        if ("1".equals(error)) {
    %>
        <div class="error">Пожалуйста, заполните все поля.</div>
    <% } else if ("login_exists".equals(error)) { %>
        <div class="error">Логин уже занят. Попробуйте другой.</div>
    <% } %>

    <!-- Сообщение об успешной регистрации (если хочешь перенаправлять назад сюда) -->
    <% if ("1".equals(request.getParameter("success"))) { %>
        <div class="success">Регистрация прошла успешно! Вы можете войти.</div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">
        <label>
            Логин:
            <input type="text" name="login" required>
        </label>
        <label>
            Пароль:
            <input type="password" name="password" required>
        </label>
        <label>
            Email:
            <input type="email" name="email" required>
        </label>
        <input type="submit" value="Зарегистрироваться">
    </form>
    <div class="login-link">
        Уже есть аккаунт? <a href="${pageContext.request.contextPath}/login">Войти</a>
    </div>
</div>

</body>
</html>
