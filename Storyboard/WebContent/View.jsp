<%@page import="utilities.DatabaseAdaptor"%>
<%@page import="enums.SBAction"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBItem"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<title>Storyboard View</title>
	<body>
		<a href="Main.jsp">Return</a><br>
		Storyboard View<br><br>
		<table>
			<% 
			final String threadId = request.getParameter("thread");
			if(threadId == null) {
				response.sendRedirect(SBPages.WELCOME.getAddress());
				//TODO: handle this
			}
			final List<SBItem> itemList = DatabaseAdaptor.getInstance().getItemList(threadId);
			for(final SBItem item : itemList) {
				out.println("<tr>");
				if(item.getAction() == SBAction.STORY) {
					out.println("Story by " + item.getCreator() + ", posted on " + item.getCreateDateString() + ":<br>");
					out.println(item.getStory() + "<br><br>");
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
		<a href="Main.jsp">Return</a>
	</body>
</html>