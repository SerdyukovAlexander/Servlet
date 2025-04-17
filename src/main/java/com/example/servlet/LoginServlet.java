package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final String BASE_DIR = "C:\\Users\\serdy\\IdeaProjects\\filemanager";
    private static final String USERS_FILE = BASE_DIR + File.separator + "users.txt";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        boolean authenticated = false;

        if (login != null && password != null && !login.isEmpty() && !password.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length >= 2 && parts[0].equals(login)) {
                        String storedHash = parts[1];
                        if (org.mindrot.jbcrypt.BCrypt.checkpw(password, storedHash)) {
                            authenticated = true;
                            break;
                        }
                    }
                }
            }
        }

        if (authenticated) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", login);

            // Создание домашней папки
            File userDir = new File(BASE_DIR, login);
            if (!userDir.exists()) {
                userDir.mkdirs();
            }

            session.setAttribute("home", userDir.getAbsolutePath());
            resp.sendRedirect(req.getContextPath() + "/catalog");
        } else {
            resp.sendRedirect(req.getContextPath() + "/login?error=1");
        }
    }
}

