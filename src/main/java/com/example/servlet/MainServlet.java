package com.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/catalog")
public class MainServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getParameter("path");
        if (path == null) {
            path = System.getProperty("user.dir");
        }

        File file = new File(path);
        if (file.isDirectory()) {
            req.setAttribute("files", file.listFiles());
            req.setAttribute("currentPath", path);
            req.setAttribute("parentPath", file.getParent());
            req.setAttribute("currentTime", new Date());
            req.getRequestDispatcher("/mypage.jsp").forward(req, resp);
        } else if (file.isFile()) {
            Path filePath = Paths.get(path);
            resp.setContentType(Files.probeContentType(filePath));
            resp.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
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