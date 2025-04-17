package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final String BASE_DIR = "C:\\Users\\serdy\\IdeaProjects\\filemanager";
    private static final String USERS_FILE = BASE_DIR + File.separator + "users.txt";

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

        File userFile = new File(USERS_FILE);
        if (!userFile.exists()) {
            userFile.getParentFile().mkdirs();
            userFile.createNewFile();
        }

        // Проверка, есть ли такой логин
        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length > 0 && parts[0].equals(login)) {
                    resp.sendRedirect(req.getContextPath() + "/register?error=login_exists");
                    return;
                }
            }
        }

        // Хешируем пароль перед сохранением
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(userFile, true))) {
            writer.write(login + ";" + hashedPassword + ";" + email);
            writer.newLine();
        }

        // Создаём домашнюю директорию
        File userDir = new File(BASE_DIR, login);
        if (!userFile.exists()) {
            File parentDir = userFile.getParentFile();
            if (!parentDir.exists()) {
                boolean dirCreated = parentDir.mkdirs();
                if (!dirCreated) {
                    throw new IOException("Не удалось создать директорию: " + parentDir.getAbsolutePath());
                }
            }

            boolean fileCreated = userFile.createNewFile();
            if (!fileCreated) {
                throw new IOException("Не удалось создать файл: " + userFile.getAbsolutePath());
            }
        }


        // Редирект на логин с сообщением
        resp.sendRedirect(req.getContextPath() + "/login?registered=1");
    }
}
