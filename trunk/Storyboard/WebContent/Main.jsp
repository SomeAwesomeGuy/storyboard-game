<%@page import="utilities.DatabaseAdaptor"%>
<%@page import="enums.SBAction"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="enums.SBThreadGroup"%>
<%@page import="objects.SBThread"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard</title>
	<body>
		<table border=0 cellspacing=0 cellpadding=10 width="100%">
			<tr valign=top>
				<td nowrap valign=top>
					<a href="FAQ.jsp">FAQ</a><br>
					<a href="Create.jsp">Create Game</a><br>
					<a href="Welcome">Log Out</a><br>
				</td>
				<td width="100%">
					New Games<br>
					<table border="1">
						<tr>
							<td>Action</td>
							<td>Title</td>
							<td>Creator</td>
							<td>Create Date</td>
							<td>Last Poster</td>
							<td>Last Added</td>
						</tr>
						<%
						final String username = (String) request.getSession().getAttribute((SBAttribute.USERNAME.name()));
						if(username == null) {
							response.sendRedirect(SBPages.WELCOME.getAddress());
							//TODO: handle this
						}
						final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
						final Map<SBThreadGroup, List<SBThread>> threadMap = dbAdaptor.getThreads(username);
													
						for(SBThread thread : threadMap.get(SBThreadGroup.NEW)) {
							final SBAction action = thread.getNextAction();
							
							out.println("<tr>");
							out.println("<td><a href=\"" + action.getJSPFile() + "?thread=" + thread.getId() + "&lastSeqNum=" + thread.getLastSeqNum() + "\">" + action.getDisplayName() + "</a></td>");
							out.println("<td>" + thread.getTitle() + "</td>");
							out.println("<td>" + thread.getCreator() + "</td>");
							out.println("<td>" + thread.getCreateDateString() + "</td>");
							out.println("<td>" + thread.getLatestPoster() + "</td>");
							out.println("<td>" + thread.getLatestDateString() + "</td>");
							out.println("</tr>");
						}
						%>
					</table>
					<br>
					Old Games
					<table border="1">
						<tr>
							<td>Action</td>
							<td>Title</td>
							<td>Creator</td>
							<td>Create Date</td>
							<td>Last Poster</td>
							<td>Last Added</td>
						</tr>
						<%
						for(SBThread thread : threadMap.get(SBThreadGroup.OLD)) {
							final SBAction action = thread.getNextAction();
							
							out.println("<tr>");
							out.println("<td><a href=\"" + action.getJSPFile() + "?thread=" + thread.getId() + "&lastSeqNum=" + thread.getLastSeqNum() + "\">" + action.getDisplayName() + "</a></td>");
							out.println("<td>" + thread.getTitle() + "</td>");
							out.println("<td>" + thread.getCreator() + "</td>");
							out.println("<td>" + thread.getCreateDateString() + "</td>");
							out.println("<td>" + thread.getLatestPoster() + "</td>");
							out.println("<td>" + thread.getLatestDateString() + "</td>");
							out.println("</tr>");
						}
						%>
					</table>
				</td>
			</tr>
		</table>
	</body>
</html>