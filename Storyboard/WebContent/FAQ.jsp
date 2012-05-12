<%@page import="enums.SBPages"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>Storyboard FAQ</title>
	<body>
		<a href="<%out.print(SBPages.MAIN.getAddress()); %>">Return</a><br>
		Storyboard FAQ<br>
		<br>
		<i>How do you play this game?</i><br>
		This game is based on a simple drawing game called <a href="http://boardgamegeek.com/boardgame/30618/eat-poop-you-cat">Eat Poop You Cat</a>. 
		To start off, the first player clicks "Create Thread" on the left side of the main page. The player picks a title for the thread and writes a simple story.
		The next player can then see the thread and has the option to draw a picture of based on the story, by clicking the "Draw" link. 
		Once that player submits his drawing, a different player can write a story based on the drawing, by clicking the "Write" link.
		This can continue indefinitely, and the fun is in seeing where the story leads! Once a player has posted in a thread, he will be able to see the entire thread history.
		<br>
		<br>
		<i>Why is this site so ugly?</i><br>
		This was the best I could do with my lack of design sense and html experience. If you think you can help me out, let me know!
		<br>
		<br>
		<i>I found a bug. Now what?</i><br>
		Kill it with your shoe. Don't miss. If it escapes, it may come back while you're sleeping.
		<br>
		<br>
		<i>Seriously, I found a problem with your game. Now what?</i><br>
		Let me know by submitting a <a href="<%out.print(SBPages.COMMENT.getAddress()); %>">comment</a>. 
		Describe the problem and what you were doing when it happened, and I'll look into it! 
		Also, if you have any suggestions about how to improve the game, I'd love to hear them!
		<br>
		<br>
		<i>You're so awesome! How do I compensate you for your efforts?</i><br>
		No, <i>you're</i> awesome! If you insist, just go to the aforementioned comments page and submit your credit card number! I'll handle the rest.
	</body>
</html>