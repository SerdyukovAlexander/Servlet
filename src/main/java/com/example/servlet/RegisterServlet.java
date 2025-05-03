package com.example.servlet;

import com.example.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String BASE_DIR = "C:\\Users\\serdy\\IdeaProjects\\filemanager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");

        if (login == null || password == null || email == null ||
                login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/register?error=1");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Проверка: существует ли пользователь с таким логином
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT id FROM users WHERE login = ?")) {
                checkStmt.setString(1, login);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    resp.sendRedirect(req.getContextPath() + "/register?error=login_exists");
                    return;
                }
            }

            // Хешируем пароль
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Сохраняем пользователя в БД
            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO users (login, password_hash, email) VALUES (?, ?, ?)")) {
                insertStmt.setString(1, login);
                insertStmt.setString(2, hashedPassword);
                insertStmt.setString(3, email);
                insertStmt.executeUpdate();
            }

            // Создание домашней директории
            File userDir = new File(BASE_DIR, login);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }

            // Редирект на логин
            resp.sendRedirect(req.getContextPath() + "/login?registered=1");

        } catch (SQLException e) {
            throw new ServletException("Ошибка при регистрации пользователя", e);
        }
    }
}
