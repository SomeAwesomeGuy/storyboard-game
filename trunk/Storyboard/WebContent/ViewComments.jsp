<%@page import="java.util.List"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBComment"%>
<%@page import="objects.SBUser"%>
<%@page import="utilities.DatabaseAdaptor"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Comment View</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		Storyboard Comment View<br><br>
		<table>
			<% 
			final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
			if(user == null) {
				response.sendRedirect(SBPages.WELCOME.getAddress());
				return;
			}
			if(!user.isAdmin()) {
				response.sendRedirect(SBPages.MAIN.getAddress());
				return;
			}
			final List<SBComment> commentList = DatabaseAdaptor.getInstance().getCommentList();
			for(final SBComment comment : commentList) {
				out.println("<tr>Comment by " + comment.getUsername() + ", posted on " + comment.getDateString() + ":<br>");
				out.println("<pre>" + comment.getComment() + "</pre></tr>");
			}
			%>
		</table>
		<a href="Main.jsp">Return</a>
	</body>
</html>