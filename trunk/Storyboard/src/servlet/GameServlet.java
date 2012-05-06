package servlet;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unrecognized form");
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
	}
	
	/**
	 * Handle a thread creation request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String username = (String) request.getSession().getAttribute(SBAttribute.USERNAME.name());
		if(username == null) {
			response.sendRedirect(SBPages.WELCOME.getAddress());
			//TODO: handle this
		}
		
		final String title = request.getParameter("title");
		final String story = request.getParameter("story");
				
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newThread(title, story, username);
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
	private void handleWrite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String username = (String) request.getSession().getAttribute(SBAttribute.USERNAME.name());
		if(username == null) {
			response.sendRedirect(SBPages.WELCOME.getAddress());
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		final String story = request.getParameter("story");
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			dbAdaptor.newStory(threadId, username, story);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
		
		System.out.println("[INFO][GAME]: new story created");
		response.sendRedirect(SBPages.MAIN.getAddress());
	}
	
	/**
	 * Handle a new drawing request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleDraw(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String username = (String) request.getSession().getAttribute(SBAttribute.USERNAME.name());
		if(username == null) {
			response.sendRedirect(SBPages.WELCOME.getAddress());
			//TODO: handle this
		}
		
		final String threadId = request.getParameter("thread");
		final String encodedPic = request.getParameter("pic");
		
		if(encodedPic.indexOf("data:image/png;base64,") < 0) {
			System.err.println("Error: encoded image not found");
			return;
			//TODO: handle this
		}
		
		final String data = encodedPic.substring(22);
		final byte[] imageByteArray = Base64.decodeBase64(data);
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(imageByteArray);
		final BufferedImage bufferedImage = ImageIO.read(inputStream);
		
		try {
			final String picPath = DatabaseAdaptor.getInstance().newDrawing(threadId, username);
			
			final File outputfile = new File(picPath);
			ImageIO.write(bufferedImage, "png", outputfile);
			System.out.println("[INFO][GAME]: new drawing written to " + picPath);
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
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
	private void handleLastDrawing(HttpServletRequest request, HttpServletResponse response, final String threadId) throws ServletException, IOException {
		try {
			final String filename = DatabaseAdaptor.getInstance().getLastDrawing(threadId);
			handleImage(response, filename);
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
	private void handleDrawingRequest(HttpServletRequest request, HttpServletResponse response, final String itemId) throws ServletException, IOException {
		try {
			final String imagePath = DatabaseAdaptor.getInstance().getDrawingById(itemId);
			handleImage(response, imagePath);
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
	 */
	private void handleImage(final HttpServletResponse response, final String filename) throws IOException {
		if(filename == null) {
			// image not found in database
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
			//TODO: handle this
		}
		
		final File imageFile = new File(ConfigAdaptor.getInstance().getProperty("drawingsDirectory") + filename);
		
		if(!imageFile.exists()) {
			// image file not found on server
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
			//TODO: handle this
		}
		
		final String contentType = getServletContext().getMimeType(imageFile.getAbsolutePath());
		
		if (contentType == null || !contentType.startsWith("image")) {
            // file is not recognized as an image
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
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
}