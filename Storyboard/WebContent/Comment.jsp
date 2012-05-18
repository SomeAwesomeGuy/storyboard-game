<%@page import="org.apache.log4j.Logger"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBUser"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.COMMENT.getAddress()); %>
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
	s_log.info(user.getUsername() + " - Comment page");
	%>
	<title>Storyboard Comment</title>
	<body>
		<a href="<%out.print(SBPages.MAIN.getAddress()); %>">Return</a><br>
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