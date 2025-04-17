<%@ page import="java.io.File" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.nio.file.Files" %>
<%@ page import="java.nio.file.attribute.BasicFileAttributes" %>
<%@ page import="java.nio.file.attribute.FileTime" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.net.URLEncoder" %>

<html>
<head>
    <title>Обзор директории</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/styles.css">
</head>
<body>

<div style="text-align: right;">
    <form action="${pageContext.request.contextPath}/logout" method="get" style="display:inline;">
        <button type="submit">Выйти</button>
    </form>
</div>

<h1>Обзор директории</h1>
<p>Время онлайн: <%= request.getAttribute("currentTime") %></p>
<p>Выбранный путь: <%= request.getAttribute("currentPath") %></p>
<%
    String parentPath = (String) request.getAttribute("parentPath");
    String encodedParentPath = parentPath != null ? URLEncoder.encode(parentPath, "UTF-8") : "";
%>
<table>
    <tr>
        <th>Тип</th>
        <th>Имя</th>
        <th class="size">Размер</th>
        <th>Последние изменения</th>
    </tr>
    <% if (parentPath != null) { %>
    <tr>
        <td colspan="4">
            <a href="${pageContext.request.contextPath}/catalog?path=<%= encodedParentPath %>">Вверх</a>
        </td>
    </tr>
    <% } %>
    <%
        File[] files = (File[]) request.getAttribute("files");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (File file : files) {
            // Кодируем путь для каждой ссылки
            String encodedPath = URLEncoder.encode(file.getAbsolutePath(), "UTF-8");
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime creationTime = attrs.creationTime();
            String formattedCreationTime = dateFormat.format(new Date(creationTime.toMillis()));
            String size = file.isDirectory() ? "" : file.length() + " B";
    %>
    <tr>
        <td>
            <% if (file.isDirectory()) { %>
                <span class="directory">Папка</span>
            <% } else { %>
                <span class="file">Файл</span>
            <% } %>
        </td>
        <td>
            <% if (file.isDirectory()) { %>
                <a class="directory" href="${pageContext.request.contextPath}/catalog?path=<%= encodedPath %>">
                    <%= file.getName() %>/
                </a>
            <% } else { %>
                <a class="file" href="${pageContext.request.contextPath}/catalog?path=<%= encodedPath %>">
                    <%= file.getName() %>
                </a>
            <% } %>
        </td>
        <td class="size"><%= size %></td>
        <td><%= formattedCreationTime %></td>
    </tr>
    <% } %>
</table>
</body>
</html>