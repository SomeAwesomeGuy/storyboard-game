<%@page import="enums.SBPages"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard New Game</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		Create a New Game<br>
		<form action="Game" method="post">
			<table>
				<tr>
					<td>Title:</td>
					<td><input type="text" id="title" name="title"/></td>
				</tr>
				<tr>
					<td>Story:</td>
					<td><textarea id="story" name="story" cols=100 rows=5></TEXTAREA></td>
				</tr>
			</table>
			<input type="hidden" id="formType" name="formType" value="CREATE"/>
			<input type="submit" value="Submit"/>
		</form> 
	</body>
</html>