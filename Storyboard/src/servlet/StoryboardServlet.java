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

import org.apache.log4j.Logger;

import objects.SBUser;

import enums.SBAttribute;
import enums.SBPages;

import utilities.ConfigAdaptor;
import utilities.DatabaseAdaptor;
import utilities.LogFactory;

/**
 * Servlet implementation class StoryboardServlet
 */
@WebServlet("/Welcome")
public class StoryboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final String CONFIG_PATH = "WEB-INF/config.xml";
	private static final String LOG_CONFIG_PATH = "WEB-INF/log4j.xml";
	
	private static final Logger s_log = LogFactory.getLogger(StoryboardServlet.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StoryboardServlet() {
        super();
    }
    
    public void init() {
		final String path = getServletConfig().getServletContext().getRealPath(CONFIG_PATH);
		final String logPath = getServletConfig().getServletContext().getRealPath(LOG_CONFIG_PATH);
		ConfigAdaptor.init(path);
		LogFactory.init(logPath);
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
		if(type == null) {
			s_log.warn("Invalid HTTP request received");
		}
		else if(type.equals("LOGIN")) {
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
    	final String username = request.getParameter("user").trim();
		final String password = request.getParameter("pass").trim();
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final SBUser user = dbAdaptor.login(username, password, request.getRemoteAddr());
			if(user != null) {
				s_log.info(username + " - Login successful");
				final HttpSession session = request.getSession();
				session.setAttribute(SBAttribute.USER.name(), user);
				response.sendRedirect(SBPages.MAIN.getAddress());
			}
			else {
				s_log.info(username + " - Login failed");
				final RequestDispatcher view = request.getRequestDispatcher(SBPages.LOGIN.getAddress());
				request.setAttribute(SBAttribute.MESSAGE.name(), "Login failed");
				view.forward(request, response);
			}
		} catch (SQLException e) {
			s_log.error("Database error", e);
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
    	final String username = request.getParameter("user").trim();
		final String password1 = request.getParameter("pass1").trim();
		final String password2 = request.getParameter("pass2").trim();
		
		if(!password1.equals(password2)) {
			handleRegisterError(request, response, "Passwords do not match");
			s_log.info(username + " - Registration - Inconsistent passwords");
			return;
		}
		if(!username.matches("[a-zA-Z0-9]*")) {
			handleRegisterError(request, response, "Username must be alphanumeric");
			s_log.info(username + " - Registration - Username is not alphanumeric");
			return;
		}
		if(!password1.matches("[a-zA-Z0-9]*")) {
			handleRegisterError(request, response, "Password must be alphanumeric");
			s_log.info(username + " - Registration - Password is not alphanumeric");
			return;
		}
		
		try {
			final DatabaseAdaptor dbAdaptor = DatabaseAdaptor.getInstance();
			final SBUser user = dbAdaptor.register(username, password1, request.getRemoteAddr());
			if(user != null) {
				s_log.info(username + " - Registration successful");
				final HttpSession session = request.getSession(true);
				session.setAttribute(SBAttribute.USER.name(), user);
				response.sendRedirect(SBPages.MAIN.getAddress());
			}
			else {
				s_log.info(username + " - Registration - Username already taken");
				handleRegisterError(request, response, "Username already taken");
			}
		} catch (SQLException e) {
			s_log.error("Database error", e);
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
