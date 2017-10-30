<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Welcome!</title>
</head>
<body>
<form action="postMessage" method="get">
<br>
<input type="text" name="message" value="">Message<br>
<input type="text" name="latitude" value="">latitude <br>
<input type="text" name="longitude" value="">longitude <br>
<input type="submit"></input>
</form>

<form action="postMessage" method="get">
<br>
<input type="text" name="latitude" value="">latitude <br>
<input type="text" name="longitude" value="">longitude <br>
<input type="submit">Update</input>
</form>

</body>
</html>