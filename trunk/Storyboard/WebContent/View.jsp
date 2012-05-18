<%@page import="java.util.List"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="enums.SBAction"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBItem"%>
<%@page import="objects.SBUser"%>
<%@page import="utilities.DatabaseAdaptor"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.VIEW.getAddress()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard View</title>
	<head>
		<style type="text/css">
			pre {
			white-space: pre-wrap; /* css-3 */
			white-space: -moz-pre-wrap !important; /* Mozilla, since 1999 */
			white-space: -pre-wrap; /* Opera 4-6 */
			white-space: -o-pre-wrap; /* Opera 7 */
			word-wrap: break-word; /* Internet Explorer 5.5+ */
			}
		</style>
		<script type="text/javascript">
			function deleteLast() {
				if (confirm("Delete the LAST post?") == true) {
					document.getElementById('formType').value = "DELETELAST"; 
					document.forms["submit"].submit();
				}
			}
			function deleteAll() {
				if (confirm("Delete the ENTIRE thread?") == true) {
					document.getElementById('formType').value = "DELETEALL"; 
					document.forms["submit"].submit();
				}
			}
		</script>
	</head>
	<body>
		<a href="Main.jsp">Return</a><br>
		Storyboard View<br><br>
		<table>
			<% 
			final String threadId = request.getParameter("thread");
			final String threadTitle = request.getParameter("title");
			final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
			if(threadId == null || user == null) {
				s_log.debug("Session has expired");
				response.sendRedirect(SBPages.WELCOME.getAddress());
				return;
				//TODO: handle this
			}
			s_log.info(user.getUsername() + " - View page for thread " + threadId + " \"" + threadTitle + "\"");
			final List<SBItem> itemList = DatabaseAdaptor.getInstance().getItemList(threadId);
			for(final SBItem item : itemList) {
				out.println("<tr>");
				if(item.getAction() == SBAction.STORY) {
					out.println("Story by " + item.getCreator() + ", posted on " + item.getCreateDateString() + ":<br>");
					out.println("<pre>" + item.getStory() + "</pre><br><br>");
				}
				else if(item.getAction() == SBAction.PICTURE) {
					out.println("Drawing by " + item.getCreator() + ", posted on " + item.getCreateDateString() + ":<br>");
					final String html = "<img src=\"Game?itemId=" + item.getItemId() + "\" width=\"100%\"/>";
					out.println(html);
				}
				out.println("</tr>");
			}
			%>
		</table>
		<form id="submit" action="Game" method="post">
			<input type="hidden" id="thread" name="thread" value="<%out.print(threadId);%>"/>
			<input type="hidden" id="formType" name="formType"/>
		</form>
		<%
		if(user.isAdmin()) {
			out.println("<input type=\"button\" onclick=\"deleteLast()\" value=\"Delete Last\"/>");
			out.println("<input type=\"button\" onclick=\"deleteAll()\" value=\"Delete All\"/><br><br>");
		}
		%>
		<a href="Main.jsp">Return</a>
	</body>
</html>