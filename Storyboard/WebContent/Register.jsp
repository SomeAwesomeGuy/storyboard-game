<%@page import="enums.SBAttribute"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Register</title>
	<body>
		Storyboard Register
		<br>
		<%
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
					<td><input type="password" id="pass1" name="pass1"/></td>
				</tr>
				<tr>
					<td>Confirm:</td>
					<td><input type="password" id="pass2" name="pass2"/></td>
				</tr>
			</table>
			<input type="hidden" id="formType" name="formType" value="REGISTER" />
			<input type="submit" value="Submit"/>
			<br><br>
			Disclaimer: I hash your passwords before storing them, but regardless, don't trust me with your common passwords.
		</form> 
	</body>
</html>