<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Trail Logger</title>
</head>
<body>
<h1>Log Tailer</h1>
    <form action="test" method="post">
        <input type="hidden" name="command" value="start">
        <button type="submit">Start</button>
    </form>
    <form action="test" method="post">
        <input type="hidden" name="command" value="stop">
        <button type="submit">Stop</button>
    </form>
</body>
</html>