package com.example.servlet;

import com.example.dao.UserDao;
import com.example.model.User;
import com.example.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() {
        userDao = new UserDao(HibernateUtil.getSessionFactory());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        if (isEmpty(login) || isEmpty(password)) {
            log("Пустой логин или пароль");
            resp.sendRedirect(req.getContextPath() + "/login?error=empty_fields");
            return;
        }

        User user = userDao.getUserByLogin(login);

        if (user == null) {
            log("Пользователь не найден: " + login);
            resp.sendRedirect(req.getContextPath() + "/login?error=user_not_found");
            return;
        }

        String storedHash = user.getPasswordHash();

        if (isEmpty(storedHash)) {
            log("У пользователя отсутствует хэш пароля: " + login);
            resp.sendRedirect(req.getContextPath() + "/login?error=internal");
            return;
        }

        try {
            if (BCrypt.checkpw(password, storedHash)) {
                HttpSession session = req.getSession(true);
                session.setAttribute("user", user.getLogin());

                // Устанавливаем путь к домашней директории
                String homeDir = "C:\\Users\\serdy\\IdeaProjects\\filemanager" + File.separator + user.getLogin();
                session.setAttribute("home", homeDir);

                resp.sendRedirect(req.getContextPath() + "/catalog");
            } else {
                log("Неверный пароль: " + login);
                resp.sendRedirect(req.getContextPath() + "/login?error=invalid_credentials");
            }
        } catch (Exception e) {
            log("Ошибка проверки пароля для пользователя " + login + ": " + e.getMessage(), e);
            resp.sendRedirect(req.getContextPath() + "/login?error=internal");
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
