package com.example.servlet;

import com.example.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String BASE_DIR = "C:\\Users\\serdy\\IdeaProjects\\filemanager";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (login == null || password == null || login.isEmpty() || password.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/login?error=1");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT password_hash FROM users WHERE login = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, login);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (BCrypt.checkpw(password, storedHash)) {
                        HttpSession session = req.getSession(true);
                        session.setAttribute("user", login);

                        // Создание домашней директории
                        File userDir = new File(BASE_DIR, login);
                        if (!userDir.exists()) {
                            userDir.mkdirs();
                        }

                        session.setAttribute("home", userDir.getAbsolutePath());
                        resp.sendRedirect(req.getContextPath() + "/catalog");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Ошибка при попытке входа", e);
        }

        // Если не прошла проверка
        resp.sendRedirect(req.getContextPath() + "/login?error=1");
    }
}
