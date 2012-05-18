<%@page import="org.apache.log4j.Logger"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBUser"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.CREATE.getAddress()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<%
	final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
	if(user == null) {
		s_log.debug("Session has expired");
		response.sendRedirect(SBPages.WELCOME.getAddress());
		return;
		//TODO: handle this
	}
	s_log.info(user.getUsername() + " - Create page");
	%>
	<title>Storyboard New Game</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		<br>
		Create a New Game<br>
		<form action="Game" method="post">
			<table>
				<tr>
					<td>Title:</td>
					<td><input type="text" id="title" name="title" size=50/></td>
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