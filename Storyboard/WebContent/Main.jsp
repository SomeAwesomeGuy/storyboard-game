<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="enums.SBAction"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="enums.SBThreadGroup"%>
<%@page import="objects.SBThread"%>
<%@page import="objects.SBUser"%>
<%@page import="utilities.DatabaseAdaptor"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.MAIN.getAddress()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard</title>
	<body>
		<table border=0 cellspacing=0 cellpadding=10 width="100%">
			<tr valign=top>
				<td nowrap valign=top>
					<%
					final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
					if(user == null) {
						s_log.debug("Session has expired");
						response.sendRedirect(SBPages.WELCOME.getAddress());
						return;
						//TODO: handle this
					}
					s_log.info(user.getUsername() + " - Main page");
					if(user.isAdmin()) {
						out.println("Admin<br>");
						out.println("<a href=\"" + SBPages.VIEWCOMMENTS.getAddress() + "\">View Comments</a><br>");
						out.println("<a href=\"" + SBPages.WELCOME.getAddress() + "\">Log Out</a><br>");
					}
					else {
						out.println("<a href=\"" + SBPages.FAQ.getAddress() + "\">FAQ</a><br>");
						out.println("<a href=\"" + SBPages.CREATE.getAddress() + "\">Create Thread</a><br>");
						out.println("<a href=\"" + SBPages.COMMENT.getAddress() + "\">Submit Feedback</a><br>");
						out.println("<a href=\"" + SBPages.WELCOME.getAddress() + "\">Log Out</a><br>");
					}
					%>
				</td>
				<td width="100%">
					Welcome to Storyboard! [Awesome logo goes here]<br>
					If you're a new player, read over the FAQ for instructions on how to play.<br>
					Check back often for updates and new threads!<br>
					<br>
					New Threads<br>
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
						final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
						final Map<SBThreadGroup, List<SBThread>> threadMap = dbAdaptor.getThreads(user.getUsername());
													
						for(SBThread thread : threadMap.get(SBThreadGroup.NEW)) {
							final SBAction action = user.isAdmin() ? SBAction.VIEW : thread.getNextAction();
							
							out.println("<tr>");
							out.println("<td><a href=\"" + action.getJSPFile() + 
									"?thread=" + thread.getId() + 
									"&lastSeqNum=" + thread.getLastSeqNum() + 
									"&title=" + thread.getTitle() +
									"\">" + action.getDisplayName() + "</a></td>");
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
					Old Threads
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
							out.println("<td><a href=\"" + action.getJSPFile() + 
									"?thread=" + thread.getId() + 
									"&lastSeqNum=" + thread.getLastSeqNum() + 
									"&title=" + thread.getTitle() +
									"\">" + action.getDisplayName() + "</a></td>");
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