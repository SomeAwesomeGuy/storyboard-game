<%@page import="org.apache.log4j.Logger"%>
<%@page import="utilities.DatabaseAdaptor"%>
<%@page import="enums.SBAttribute"%>
<%@page import="enums.SBPages"%>
<%@page import="objects.SBUser"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%!final static Logger s_log = Logger.getLogger(SBPages.DRAW.getAddress()); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <title>Storyboard Draw</title>
    <a href="Main.jsp">Return</a> <a href="javascript:location.reload(true);">Start Over</a><br>
    <style type="text/css"><!--
      #container { position: relative; }
      #imageView { border: 1px solid #000; }
html, body {
width: 100%;
height: 100%;
margin: 0px;
}
pre {
			white-space: pre-wrap; /* css-3 */
			white-space: -moz-pre-wrap !important; /* Mozilla, since 1999 */
			white-space: -pre-wrap; /* Opera 4-6 */
			white-space: -o-pre-wrap; /* Opera 7 */
			word-wrap: break-word; /* Internet Explorer 5.5+ */
			}
    --></style>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.3/jquery.min.js"></script>
    <script type="text/javascript" src="draw.js?n=5"></script>
    <script type="text/javascript" src="init.js"></script>
  </head>
  <body>
  	<table>
  		<tr>
  			<td>Story:</td>
  			<td>
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
  				s_log.info(user.getUsername() + " - Draw page for thread " + threadId + " \"" + threadTitle + "\"");
  				out.println("<pre>" + DatabaseAdaptor.getInstance().getLastStory(threadId) + "</pre>");
  				%>
  			</td>
  		</tr>
  	</table>
    <div id="container" style="padding:5px 0px 0px 0px;">
<div id="colorToolbar" style="border: 1px solid black; float: left;">
<div id="red" style="background:red; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="pink" style="background:pink; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="fuchsia" style="background:fuchsia; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="orange" style="background:orange; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="yellow" style="background:yellow; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="lime" style="background:lime; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="green" style="background:green; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="blue" style="background:blue; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="purple" style="background:purple; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="brown" style="background:brown; width:50px; height:50px; float:left;"></div><div style="clear: both;"></div>
<div id="black" style="background:black; width:50px; height:50px; float:left; border: 1px dashed white;"></div><div style="clear: both;"></div>
<hr/>
<button id="save" style="width:50px; height:50px; float:left;">Done!</button>
<div style="clear: both;"></div>
</div>

<div id="canvasDiv" style="float: left;">
<canvas id="imageView">
<p>Unfortunately, your browser is currently unsupported by our web
application. We are sorry for the inconvenience. Please use one of the
supported browsers listed below, or draw the image you want using an
offline tool.</p>
<p>Supported browsers: <a href="http://www.opera.com">Opera</a>, <a
href="http://www.mozilla.com">Firefox</a>, <a
href="http://www.apple.com/safari">Safari</a>, and <a
href="http://www.konqueror.org">Konqueror</a>.</p>
</canvas>
<form id="submit" action="Game" method="post">
	<input type="hidden" id="drawing" name="drawing"/>
	<input type="hidden" id="lastSeqNum" name="lastSeqNum" value="<%out.print(lastSeqNum);%>"/>
	<input type="hidden" id="thread" name="thread" value="<%out.print(threadId);%>"/>
	<input type="hidden" id="formType" name="formType" value="DRAW"/>
</form>
  </body>
</html>