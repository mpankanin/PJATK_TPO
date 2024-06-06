<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>TPO5 - S24188</title>
  <style>
    body {
      font-family: Arial, sans-serif;
    }
    form {
      width: 300px;
      margin: 0 auto;
    }
    form input[type="text"], form select, form input[type="number"] {
      width: 100%;
      padding: 10px;
      margin-bottom: 10px;
      box-sizing: border-box;
    }
    form input[type="submit"] {
      background-color: #4CAF50;
      color: white;
      padding: 10px;
      border: none;
      cursor: pointer;
    }
    form input[type="submit"]:hover {
      background-color: #45a049;
    }
  </style>
</head>
<body>
<h1>TPO5 - S24188</h1>
<form action="/controller" method="post">
  Type: <select name="type">
  <option value="">Select type</option>
  <option value="car">Car</option>
  <option value="truck">Truck</option>
  <option value="f1">F1</option>
  <option value="pickup">Pickup</option>
</select><br>
  Brand: <input type="text" name="brand"><br>
  Model: <input type="text" name="model"><br>
  Production Year: <select name="productionYear">
  <option value="">Select year</option>
  <% for (int year = 1980; year <= 2024; year++) { %>
  <option value="<%= year %>"><%= year %></option>
  <% } %>
</select><br>
  Fuel Consumption: <input type="number" name="fuelConsumption" min="1.0" max="100.0" step="0.1"><br>
  <input type="submit" value="Submit">
</form>
</body>
</html>