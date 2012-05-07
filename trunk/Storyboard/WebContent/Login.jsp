<%@page import="enums.SBAttribute"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Login</title>
	<body>
		Storyboard(alpha) Login
		<br>
		<%
		session.removeAttribute(SBAttribute.USERNAME.name());
		final String message = (String)request.getAttribute(SBAttribute.MESSAGE.name());
		if(message != null) {
			out.println(message);
		}
		%>
		<form action="Welcome" method="post">
			<table>
				<tr>
					<td>Username:</td>
					<td><input type="text" id="user" name="user"/></td>
				</tr>
				<tr>
					<td>Password:</td>
					<td><input type="password" id="pass" name="pass"/></td>
				</tr>
			</table>
			<input type="hidden" id="formType" name="formType" value="LOGIN" />
			<input type="submit" value="Submit"/>
			<br>
			<a href="Register.jsp">Register</a> 
		</form> 
		<br>
		Warning: The site works on Firefox, Chrome, and Safari (including iPhone and iPad), but not Internet Explorer
	</body>
</html>
