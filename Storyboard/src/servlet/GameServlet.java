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

import utilities.ConfigAdaptor;
import utilities.DatabaseAdaptor;

import enums.SBAttribute;
import enums.SBPages;

/**
 * Servlet implementation class Game
 */
@WebServlet("/Game")
public class GameServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_BUFFER_SIZE = 10240;
	       
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			//TODO: handle this
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String type = request.getParameter("formType");
		if(type == null) {
			//TODO: handle this
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String title = request.getParameter("title");
		final String story = request.getParameter("story");
		
		if(title.trim().isEmpty()) {
			handleError(request, response, "The title cannot be blank.");
			return;
		}
		if(story.trim().isEmpty()) {
			handleError(request, response, "The story cannot be blank.");
			return;
		}
				
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newThread(replaceChevrons(title), replaceChevrons(story), user.getUsername());
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: new thread created");
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		final String lastSeqNum = request.getParameter("lastSeqNum");
		final String story = request.getParameter("story");
		
		if(story.trim().isEmpty()) {
			handleError(request, response, "The story cannot be blank.");
			return;
		}
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final int dbSeqNum = dbAdaptor.getLastSeqNum(threadId);
			if(dbSeqNum != Integer.parseInt(lastSeqNum)) {
				handleError(request, response, "Someone beat you to the punch! A story was submitted for this drawing already.");
				return;
			}
			
			dbAdaptor.newStory(threadId, user.getUsername(), replaceChevrons(story));
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: new story created");
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String comment = request.getParameter("comment");
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newComment(user.getUsername(), comment);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: new comment created");
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
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
			e.printStackTrace();
			return;
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: thread deleted");
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
			response.sendRedirect(SBPages.WELCOME.getAddress());
			return;
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final String imageFile = dbAdaptor.deleteLastPost(threadId);
			if(imageFile != null) {
				deleteImage(imageFile);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: post deleted");
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
				request.setAttribute(SBAttribute.MESSAGE.name(), "Someone beat you to the punch! A drawing was submitted for this story already.");
				final RequestDispatcher view = request.getRequestDispatcher(SBPages.ERROR.getAddress());
				view.forward(request, response);
				return;
			}
		} catch (SQLException e1) {
			e1.printStackTrace();
			//TODO: handle this
		}
		
		if(encodedPic.indexOf("data:image/png;base64,") < 0) {
			System.err.println("Error: encoded image not found");
			handleError(request, response, "There was a problem receiving the drawing.");
			return;
			//TODO: handle this
		}
		
		final String data = encodedPic.substring(22);
		final byte[] imageByteArray = Base64.decodeBase64(data);
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
		final BufferedImage bufferedImage = ImageIO.read(inputStream);
		
		try {
			final String picPath = dbAdaptor.newDrawing(threadId, user.getUsername());
			
			final File outputfile = new File(picPath);
			ImageIO.write(bufferedImage, "png", outputfile);
			System.out.println("[INFO][GAME]: new drawing written to " + picPath);
		} catch(SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		} catch(IOException e) {
			e.printStackTrace();
			handleError(request, response, "The was a problem saving the drawing to the server.");
			return;
		}
		
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
			e.printStackTrace();
			//TODO: handle this
		} catch(IOException e) {
			//TODO: handle this
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
			final String imagePath = DatabaseAdaptor.getInstance().getDrawingById(itemId);
			handleImage(request, response, imagePath);
		} catch(SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		} catch(IOException e) {
			//TODO: handle this
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
			// image not found in database
			handleError(request, response, "Image not found in database.");
			return;
		}
		
		final File imageFile = new File(ConfigAdaptor.getInstance().getProperty("drawingsDirectory") + filename);
		
		if(!imageFile.exists()) {
			// image file not found on server
			handleError(request, response, "Image not found on server.");
			return;
			//TODO: handle this
		}
		
		final String contentType = getServletContext().getMimeType(imageFile.getAbsolutePath());
		
		if (contentType == null || !contentType.startsWith("image")) {
            // file is not recognized as an image
			handleError(request, response, "Image not recognized.");
            return;
          //TODO: handle this
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
