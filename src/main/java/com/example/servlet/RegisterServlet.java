package com.example.servlet;

import com.example.dao.UserDao;
import com.example.model.User;
import com.example.util.HibernateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDao userDao;

    @Override
    public void init() {
        userDao = new UserDao(HibernateUtil.getSessionFactory());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if (isEmpty(login) || isEmpty(email) || isEmpty(password)) {
            log("Пустые поля при регистрации");
            resp.sendRedirect(req.getContextPath() + "/register?error=empty_fields");
            return;
        }

        if (userDao.getUserByLogin(login) != null) {
            log("Пользователь уже существует: " + login);
            resp.sendRedirect(req.getContextPath() + "/register?error=user_exists");
            return;
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);

        try {
            userDao.saveUser(user);
        } catch (Exception e) {
            log("Ошибка при сохранении пользователя: " + e.getMessage(), e);
            resp.sendRedirect(req.getContextPath() + "/register?error=save_failed");
            return;
        }

        // Создаём директорию пользователя
        String baseDir = "C:\\Users\\serdy\\IdeaProjects\\filemanager";
        File userDir = new File(baseDir, login);
        if (!userDir.exists()) {
            boolean created = userDir.mkdirs();
            if (!created) {
                log("Не удалось создать директорию пользователя: " + userDir.getAbsolutePath());
                resp.sendRedirect(req.getContextPath() + "/register?error=home_dir_creation_failed");
                return;
            }
        }

        // Перенаправляем на логин
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
