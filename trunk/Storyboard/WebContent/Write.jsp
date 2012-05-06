<%@page import="enums.SBPages"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Write</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		Storyboard Write!<br>
		<% 
		final String threadId = request.getParameter("thread");	
		if(threadId == null) {
			response.sendRedirect(SBPages.WELCOME.getAddress());
			//TODO: handle this
		}
		%>
		
		<img src="Game?threadId=<%out.print(threadId); %>" width="100%" height="100%"/>
		
		<form action="Game" method="post">
			<table>
				<tr>
					<td>Story:</td>
					<td><textarea id="story" name="story" cols=100 rows=5></TEXTAREA></td>
				</tr>
			</table>
			<input type="hidden" id="formType" name="formType" value="WRITE"/>
			<input type="hidden" id="thread" name="thread" value="<%out.print(threadId);%>"/>
			<input type="submit" value="Submit"/>
		</form> 
	</body>
</html>