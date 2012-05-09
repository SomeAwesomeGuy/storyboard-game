package servlet;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import enums.SBAttribute;
import enums.SBPages;

import utilities.ConfigAdaptor;
import utilities.DatabaseAdaptor;

/**
 * Servlet implementation class StoryboardServlet
 */
@WebServlet("/Welcome")
public class StoryboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String CONFIG_PATH = "WEB-INF/config.xml";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoryboardServlet() {
        super();
    }
    
    public void init() {
		final String path = getServletConfig().getServletContext().getRealPath(CONFIG_PATH);
		ConfigAdaptor.init(path);
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getSession(true);
		final RequestDispatcher view = request.getRequestDispatcher(SBPages.LOGIN.getAddress());
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final String type = request.getParameter("formType");
		if(type.equals("LOGIN")) {
			handleLogin(request, response);
		}
		else if(type.equals("REGISTER")) {
			handleRegister(request, response);
		}
	}

	/**
	 * Handle a login request
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	final String username = request.getParameter("user");
		final String password = request.getParameter("pass");
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			if(dbAdaptor.login(username, password, request.getRemoteAddr())) {
				// Login successful
				System.out.println("[INFO][WELCOME]: User \"" + username + "\" has successfully logged in");
				final HttpSession session = request.getSession();
				session.setAttribute(SBAttribute.USERNAME.name(), username);
				response.sendRedirect(SBPages.MAIN.getAddress());
			}
			else {
				// Login failed
				System.out.println("[INFO][WELCOME]: User \"" + username + "\" has failed to logged in");
				final RequestDispatcher view = request.getRequestDispatcher(SBPages.LOGIN.getAddress());
				request.setAttribute(SBAttribute.MESSAGE.name(), "Login failed");
				view.forward(request, response);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
    }
    
    /**
     * Handle a user registration request
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	final String username = request.getParameter("user");
		final String password1 = request.getParameter("pass1");
		final String password2 = request.getParameter("pass2");
		
		if(!password1.equals(password2)) {
			// Inconsistent passwords
			handleRegisterError(request, response, "Passwords do not match");
			return;
		}
		if(!username.matches("[a-zA-Z0-9]*")) {
			handleRegisterError(request, response, "Username must be alphanumeric");
			return;
		}
		if(!password1.matches("[a-zA-Z0-9]*")) {
			handleRegisterError(request, response, "Password must be alphanumeric");
			return;
		}
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			if(dbAdaptor.register(username, password1, request.getRemoteAddr())) {
				// Register successful
				System.out.println("[INFO][WELCOME]: Registered user \"" + username + "\"");
				final HttpSession session = request.getSession(true);
				session.setAttribute(SBAttribute.USERNAME.name(), username);
				response.sendRedirect(SBPages.MAIN.getAddress());
			}
			else {
				// Username already taken
				System.out.println("[INFO][WELCOME]: Username \"" + username + "\" is already taken");
				handleRegisterError(request, response, "Username already taken");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			//TODO: handle this
		}
    }
    
    /**
     * Handle register error
     * @param request
     * @param response
     * @param message	Error message
     * @throws ServletException
     * @throws IOException
     */
    private void handleRegisterError(HttpServletRequest request, HttpServletResponse response, final String message) throws ServletException, IOException {
    	final RequestDispatcher view = request.getRequestDispatcher(SBPages.REGISTER.getAddress());
		request.setAttribute(SBAttribute.MESSAGE.name(), message);
		view.forward(request, response);
    }
}
