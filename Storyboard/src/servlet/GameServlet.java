package servlet;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.SBUser;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import utilities.ConfigAdaptor;
import utilities.DatabaseAdaptor;
import utilities.LogFactory;

import enums.SBAttribute;
import enums.SBPages;

/**
 * Servlet implementation class Game
 */
@WebServlet("/Game")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240;
	
	private static final Logger s_log = LogFactory.getLogger(GameServlet.class);
	       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GameServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String threadId = request.getParameter("threadId");
		final String itemId = request.getParameter("itemId");
		if(threadId != null) {
			handleLastDrawing(request, response, threadId);
		}
		else if(itemId != null) {
			handleDrawingRequest(request, response, itemId);
		}
		else {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String type = request.getParameter("formType");
		if(type == null) {
			s_log.warn("Invalid HTTP request received");
		}
		else if(type.equals("CREATE")) {
			handleCreate(request, response);
		}
		else if(type.equals("WRITE")) {
			handleWrite(request, response);
		}
		else if(type.equals("DRAW")) {
			handleDraw(request, response);
		}
		else if(type.equals("COMMENT")) {
			handleComment(request, response);
		}
		else if(type.equals("DELETEALL")) {
			handleDeleteAll(request, response);
		}
		else if(type.equals("DELETELAST")) {
			handleDeleteLast(request, response);
		}
	}
	
	/**
	 * Handle a thread creation request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleCreate(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String title = request.getParameter("title").trim();
		final String story = request.getParameter("story").trim();
		
		if(title.isEmpty()) {
			s_log.info(user.getUsername() + " - submitted thread with blank title");
			handleError(request, response, "The title cannot be blank.");
			return;
		}
		if(story.isEmpty()) {
			s_log.info(user.getUsername() + " - submitted thread with blank story");
			handleError(request, response, "The story cannot be blank.");
			return;
		}
				
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newThread(replaceChevrons(title), replaceChevrons(story), user.getUsername());
		} catch (SQLException e) {
			s_log.error("Database error while creating new thread", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Successfully submitted new thread \"" + title + "\"");
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle a new story request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleWrite(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		final String lastSeqNum = request.getParameter("lastSeqNum");
		final String story = request.getParameter("story");
		
		if(story.trim().isEmpty()) {
			s_log.info(user.getUsername() + " - submitted a blank story");
			handleError(request, response, "The story cannot be blank.");
			return;
		}
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final int dbSeqNum = dbAdaptor.getLastSeqNum(threadId);
			if(dbSeqNum != Integer.parseInt(lastSeqNum)) {
				s_log.info(user.getUsername() + " - submitted a story too late");
				handleError(request, response, "Someone beat you to the punch! A story was submitted for this drawing already.");
				return;
			}
			
			dbAdaptor.newStory(threadId, user.getUsername(), replaceChevrons(story));
		} catch (SQLException e) {
			s_log.error("Database error while creating new story", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Successfully submitted new story");
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle a new comment
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleComment(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String comment = request.getParameter("comment");
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newComment(user.getUsername(), comment);
		} catch (SQLException e) {
			s_log.error("Database error while creating new comment", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Successfully submitted new comment");
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle deletion of thread
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDeleteAll(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		if(!user.isAdmin()) {
			s_log.info(user.getUsername() + " - User does not have permission to delete threads");
			handleError(request, response, "You do not have permission to delete threads");
			return;
		}
		
		final String threadId = request.getParameter("thread");
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final List<String> imageList = dbAdaptor.deleteThread(threadId);
			if(imageList != null) {
				for(final String imageFile : imageList) {
					deleteImage(imageFile);
				}
			}
		} catch (SQLException e) {
			s_log.error("Database error while deleting thread", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Successfully deleted thread " + threadId);
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle deletion of last post in thread
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDeleteLast(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		if(!user.isAdmin()) {
			s_log.info(user.getUsername() + " - User does not have permission to delete posts");
			handleError(request, response, "You do not have permission to delete posts");
			return;
		}
		
		final String threadId = request.getParameter("thread");
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final String imageFile = dbAdaptor.deleteLastItem(threadId);
			if(imageFile != null) {
				deleteImage(imageFile);
			}
		} catch (SQLException e) {
			s_log.error("Database error while deleting last post", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Successfully deleted post from thread " + threadId);
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle a new drawing request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDraw(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final SBUser user = (SBUser) request.getSession().getAttribute(SBAttribute.USER.name());
		if(user == null) {
			s_log.debug("Session has expired");
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		final String lastSeqNum = request.getParameter("lastSeqNum");
		final String encodedPic = request.getParameter("drawing");
		
		final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
		try {
			final int dbSeqNum = dbAdaptor.getLastSeqNum(threadId);
			if(dbSeqNum != Integer.parseInt(lastSeqNum)) {
				s_log.info(user.getUsername() + " - submitted a drawing too late");
				request.setAttribute(SBAttribute.MESSAGE.name(), "Someone beat you to the punch! A drawing was submitted for this story already.");
				final RequestDispatcher view = request.getRequestDispatcher(SBPages.ERROR.getAddress());
				view.forward(request, response);
				return;
			}
		} catch (SQLException e) {
			s_log.error("Database error while checking for latest drawing", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		}
		
		if(encodedPic.indexOf("data:image/png;base64,") < 0) {
			s_log.error("Encoded image not found in HTTP request - " + encodedPic);
			handleError(request, response, "There was a problem receiving the drawing.");
			return;
		}
		
		final String data = encodedPic.substring(22);
		final byte[] imageByteArray = Base64.decodeBase64(data);
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
		final BufferedImage bufferedImage = ImageIO.read(inputStream);
		
		try {
			final String picPath = dbAdaptor.newDrawing(threadId, user.getUsername());
			
			final File outputfile = new File(picPath);
			ImageIO.write(bufferedImage, "png", outputfile);
			s_log.debug("Image written to " + picPath);
		} catch(SQLException e) {
			s_log.error("Database error while creating new drawing", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		} catch(IOException e) {
			s_log.error("Error while saving new drawing", e);
			handleError(request, response, "The was a problem saving the drawing to the server.");
			return;
		}
		
		s_log.info(user.getUsername() + " - Sucessfully submitted new drawing");
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handles a request for the last drawing in a thread
	 * @param request
	 * @param response
	 * @param threadId	the id of the thread
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleLastDrawing(final HttpServletRequest request, final HttpServletResponse response, final String threadId) throws ServletException, IOException {
		try {
			final String filename = DatabaseAdaptor.getInstance().getLastDrawing(threadId);
			handleImage(request, response, filename);
		} catch(SQLException e) {
			s_log.error("Database error while retrieving drawing", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		} catch(IOException e) {
			s_log.error("Error while retrieving image from the server", e);
			handleError(request, response, "Storyboard encountered a problem retrieving the image from the server.");
			return;
		}
	}
	
	/**
	 * Handles a request for a drawing by its id
	 * @param request
	 * @param response
	 * @param itemId	the id of the drawing
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDrawingRequest(final HttpServletRequest request, final HttpServletResponse response, final String itemId) throws ServletException, IOException {
		try {
			final String filename = DatabaseAdaptor.getInstance().getDrawingById(itemId);
			handleImage(request, response, filename);
		} catch(SQLException e) {
			s_log.error("Database error while retrieving drawing", e);
			handleError(request, response, "Storyboard encountered a database error. Please try again.");
			return;
		} catch(IOException e) {
			s_log.error("Error while retrieving image from the server", e);
			handleError(request, response, "Storyboard encountered a problem retrieving the image from the server.");
			return;
		}
	}

	/**
	 * Handles a request for an image
	 * @param response
	 * @param filename	the filename of the image
	 * @throws IOException
	 * @throws ServletException 
	 */
	private void handleImage(final HttpServletRequest request, final HttpServletResponse response, final String filename) throws IOException, ServletException {
		if(filename == null) {
			s_log.error("Drawing not found in the database");
			handleError(request, response, "Image not found in database.");
			return;
		}
		
		final File imageFile = new File(ConfigAdaptor.getInstance().getProperty("drawingsDirectory") + filename);
		
		if(!imageFile.exists()) {
			s_log.error("Image " + filename + " not found on server");
			handleError(request, response, "Image not found on server.");
			return;
		}
		
		final String contentType = getServletContext().getMimeType(imageFile.getAbsolutePath());
		
		if (contentType == null || !contentType.startsWith("image")) {
			s_log.error("File " + filename + " is not recognized as an image file");
			handleError(request, response, "Image not recognized.");
            return;
        }
		
		response.reset();
		response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setContentType(contentType);
		response.setHeader("Content-Length", String.valueOf(imageFile.length()));
        response.setHeader("Content-Disposition", "inline; filename=\"" + imageFile.getName() + "\"");
        
        final BufferedInputStream input = new BufferedInputStream(new FileInputStream(imageFile), DEFAULT_BUFFER_SIZE);
        final BufferedOutputStream output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
        
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int length;
        while ((length = input.read(buffer)) > 0) {
        	output.write(buffer, 0, length);
        }
        
        input.close();
        output.close();
	}
	
	/**
	 * Handle an error
	 * @param request
	 * @param response
	 * @param message	Error message
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleError(final HttpServletRequest request, final HttpServletResponse response, final String message) throws ServletException, IOException {
		request.setAttribute(SBAttribute.MESSAGE.name(), message);
		final RequestDispatcher view = request.getRequestDispatcher(SBPages.ERROR.getAddress());
		view.forward(request, response);
	}
	
	/**
	 * Delete the image file on the server
	 * @param filename	the filename of the image
	 */
	private static void deleteImage(final String filename) {
		final File imageFile = new File(ConfigAdaptor.getInstance().getProperty("drawingsDirectory") + filename);
		if(imageFile.exists()) {
			s_log.debug("Deleting " + filename + " from the server");
			imageFile.delete();
		}
	}
	
	/**
	 * Replace chevrons in a String with html-friendly versions
	 * @param s	the string
	 * @return	a string without chevrons
	 */
	private static String replaceChevrons(String s) {
		s = s.replaceAll("<", "&lt;");
		return s.replaceAll(">", "&gt;");
	}
}
