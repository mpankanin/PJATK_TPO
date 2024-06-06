<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: panka
  Date: 06.06.2024
  Time: 21:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Vehicle List</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #4CAF50;
            color: white;
        }
    </style>
</head>
<body>
<% List<String> vehicles = (List<String>) request.getAttribute("vehicles");
    if (vehicles == null || vehicles.isEmpty()) {
%>
<p>No data found</p>
<% } else { %>
<table>
    <tr>
        <th>Type</th>
        <th>Brand</th>
        <th>Model</th>
        <th>Production Year</th>
        <th>Fuel Consumption</th>
    </tr>
    <% for (String vehicle : vehicles) {
        String[] properties = vehicle.split(";");
    %>
    <tr>
        <% for (String property : properties) { %>
        <td><%= property %></td>
        <% } %>
    </tr>
    <% } %>
</table>
<% } %>
</body>
</html>
