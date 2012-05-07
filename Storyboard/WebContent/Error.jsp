<%@page import="enums.SBAttribute"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Error</title>
	<body>
		Storyboard Error
		<br>
		<a href="Main.jsp">Return to Storyboard</a><br>
		<%
		final String message = (String)request.getAttribute(SBAttribute.MESSAGE.name());
		if(message != null) {
			out.println(message);
		}
		%>
	</body>
</html>