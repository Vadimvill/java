<%--
  Created by IntelliJ IDEA.
  User: botme
  Date: 18.02.2024
  Time: 19:45
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Send Data to Server</title>
</head>
<body>

<h1>Send Data to Server</h1>

<form action="DataServlet" method="get">
  <label for="dataInput">Enter data:</label>
  <input type="text" id="dataInput" name="data" required>
  <input type="submit" value="Send Data">
</form>

</body>
</html>

