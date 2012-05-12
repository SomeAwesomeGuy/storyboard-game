<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Comment</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		<br>
		Leave a comment!<br>
		<form action="Game" method="post">
			<table>
				<tr>
					<td>Comment:</td>
					<td><textarea id="comment" name="comment" cols=100 rows=5></TEXTAREA></td>
				</tr>
			</table>
			<input type="hidden" id="formType" name="formType" value="COMMENT"/>
			<input type="submit" value="Submit"/>
		</form> 
	</body>
</html>