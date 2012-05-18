<%@page import="org.apache.log4j.Logger"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBUser"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.CREATE.getAddress()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard Write</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		Storyboard Write!<br>
		<% 
		final String threadId = request.getParameter("thread");
		final String threadTitle = request.getParameter("title");
		final String lastSeqNum = request.getParameter("lastSeqNum");
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(threadId == null || user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		s_log.info(user.getUsername() + " - Write page for thread " + threadId + " \"" + threadTitle + "\"");
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
			<input type="hidden" id="lastSeqNum" name="lastSeqNum" value="<%out.print(lastSeqNum);%>"/>
			<input type="hidden" id="thread" name="thread" value="<%out.print(threadId);%>"/>
			<input type="submit" value="Submit"/>
		</form> 
	</body>
</html>