package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;

@WebServlet("/catalog")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Проверка сессии и авторизации
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Получаем домашнюю директорию пользователя
        String userHome = (String) session.getAttribute("home");
        String requestedPath = req.getParameter("path");

        File currentDir;

        if (requestedPath == null) {
            currentDir = new File(userHome);
        } else {
            // Нормализуем путь и проверяем, что он внутри домашней директории
            File requestedFile = new File(requestedPath).getCanonicalFile();
            if (!requestedFile.getAbsolutePath().startsWith(new File(userHome).getCanonicalPath())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Доступ запрещён.");
                return;
            }
            currentDir = requestedFile;
        }

        if (currentDir.isDirectory()) {
            req.setAttribute("files", currentDir.listFiles());
            req.setAttribute("currentPath", currentDir.getAbsolutePath());

            // Определяем parentPath только если он внутри домашней директории
            String parentPath = currentDir.getParentFile() != null
                    && new File(currentDir.getParent()).getCanonicalPath().startsWith(new File(userHome).getCanonicalPath())
                    ? currentDir.getParent()
                    : null;
            req.setAttribute("parentPath", parentPath);

            req.setAttribute("currentTime", new Date());
            req.getRequestDispatcher("/mypage.jsp").forward(req, resp);
        } else if (currentDir.isFile()) {
            // Отдаём файл на скачивание
            Path filePath = currentDir.toPath();
            resp.setContentType(Files.probeContentType(filePath));
            resp.setHeader("Content-Disposition", "attachment; filename=" + currentDir.getName());
            Files.copy(filePath, resp.getOutputStream());
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
