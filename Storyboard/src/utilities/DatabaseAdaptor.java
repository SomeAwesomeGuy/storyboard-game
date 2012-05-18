package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import objects.SBComment;
import objects.SBItem;
import objects.SBThread;
import objects.SBUser;

import enums.SBAction;
import enums.SBThreadGroup;

public class DatabaseAdaptor {
	private static final Logger s_log = Logger.getLogger(DatabaseAdaptor.class);
	
	private static DatabaseAdaptor s_databaseAdaptor = null;
	
	private Connection g_connection = null;
	
	private final String g_url;
	private final String g_username;
	private final String g_password;
	
	/**
	 * DatabaseAdaptor constructor
	 */
	private DatabaseAdaptor() {
		final ConfigAdaptor configAdaptor = ConfigAdaptor.getInstance();
		final String database = configAdaptor.getProperty("database");
		final String address = configAdaptor.getProperty("dbaddress");
		g_username = configAdaptor.getProperty("dbusername");
		g_password = configAdaptor.getProperty("dbpassword");
		
		g_url = "jdbc:mysql://" + address + "/" + database;
		
        connect();
	}
	
	/**
	 * Singleton pattern
	 * @return DatabaseAdaptor instance
	 */
	public static DatabaseAdaptor getInstance() {
		if(s_databaseAdaptor == null) {
			s_databaseAdaptor = new DatabaseAdaptor();
		}
		return s_databaseAdaptor;
	}
	
	/**
	 * Close database connection
	 */
	private void connect() {
		try {
			if(g_connection == null || g_connection.isClosed()) {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				g_connection = DriverManager.getConnection(g_url, g_username, g_password);
				s_log.info("Database connection established");
			}
		} catch (Exception e) {
			s_log.error("Unable to connect to the database", e);
		}
	}
	
	/**
	 * Compares the given password with the password in the database
	 * @param username	the user's name
	 * @param password	the user's password
	 * @return			SBUser if the login succeeded, null if the login failed
	 * @throws SQLException
	 */
	public SBUser login(final String username, final String password, final String ipAddress) throws SQLException {
		s_log.debug("Login");
		final String passwordQuery = "SELECT password, isAdmin FROM user where username = '" + username + "'";
		final ResultSet res = executeQuery(passwordQuery);
		if(res.next()) {
			final String dbPassword = res.getString("password");
			final boolean isAdmin = res.getInt("isAdmin") == 1;
			final String passHash = getPasswordHash(username, password);
			if(dbPassword.equals(passHash)) {
				final String updateTimeQuery = "UPDATE user SET last_login_datetime = '" + getCurrentTime() + "' where username = '" + username + "'";
				executeUpdate(updateTimeQuery);
				final String updateIPQuery = "UPDATE user SET last_ip_address = '" + ipAddress + "' where username = '" + username + "'";
				executeUpdate(updateIPQuery);
				return new SBUser(username, isAdmin);
			}
		}
		
		return null;
	}
	
	/**
	 * Inserts a new user into the database
	 * @param username	the user's name
	 * @param password	the user's password
	 * @return			true if the register succeeded, false if the username is already taken
	 * @throws SQLException
	 */
	public SBUser register(final String username, final String password, final String ipAddress) throws SQLException {
		s_log.debug("Register");
		final String usernameQuery = "SELECT username FROM user where username = '" + username + "'";
		final ResultSet res = executeQuery(usernameQuery);
		if(res.next()) {
			return null;
		}
		
		final String currentTime = getCurrentTime();
		final String passHash = getPasswordHash(username, password);
		final String userQuery = "INSERT INTO user VALUES('" + username + "', '" + passHash + "', '" + currentTime + "', '" + currentTime + "', '" + ipAddress +"', NULL)";
		executeUpdate(userQuery);
		
		return new SBUser(username, false);
	}
	
	/**
	 * Inserts a new thread into the database
	 * @param threadTitle	the title of the thread
	 * @param story			the first story of the thread
	 * @param username		the username of the thread creator
	 * @throws SQLException
	 */
	public void newThread(final String threadTitle, final String story, final String username) throws SQLException {
		s_log.debug("New thread");
		final String id = createId();
		
		// Create thread
		final String threadQuery = "INSERT INTO thread VALUES('" + id + "', ?, '')";
		executePrepared(threadQuery, threadTitle);
		
		// Create story
		newStory(id, username, story);
	}
	
	/**
	 * Inserts a new story into the database
	 * @param threadId	the id of the thread
	 * @param username	the username of the creator
	 * @param story		the story
	 * @throws SQLException
	 */
	public void newStory(final String threadId, final String username, final String story) throws SQLException {
		s_log.debug("New story");
		final String id = createId();
		
		final String maxSeqNumQuery = "SELECT max(seq_num) AS max FROM item WHERE thread_id = '" + threadId + "'";
		final ResultSet res = executeQuery(maxSeqNumQuery);
		if(res.next()) {
			final int seqNum = res.getInt("max") + 1;
			
			final String itemQuery = "INSERT INTO item VALUES('" + id + "', '" + threadId + "', " + seqNum + ", '" + username + "', '" + getCurrentTime() + "', '" + SBAction.STORY.name() + "')";
			executeUpdate(itemQuery);
			
			final String storyQuery = "INSERT INTO story VALUES('" + id + "', ?)";
			executePrepared(storyQuery, story);
			
			final String threadQuery = "UPDATE thread SET last_item_id = '" + id + "' WHERE id = '" + threadId + "'";
			executeUpdate(threadQuery);
		}
	}
	
	/**
	 * Inserts a new drawing into the database
	 * @param threadId	the id of the thread
	 * @param username	the username of the creator
	 * @param story		the story
	 * @throws SQLException
	 */
	public String newDrawing(final String threadId, final String username) throws SQLException {
		s_log.debug("New drawing");
		final String id = createId();
		final String filename = id + ".png";
		
		final String maxSeqNumQuery = "SELECT max(seq_num) AS max FROM item WHERE thread_id = '" + threadId + "'";
		final ResultSet res = executeQuery(maxSeqNumQuery);
		if(res.next()) {
			final int seqNum = res.getInt("max") + 1;
			
			final String itemQuery = "INSERT INTO item VALUES('" + id + "', '" + threadId + "', " + seqNum + ", '" + username + "', '" + getCurrentTime() + "', '" + SBAction.PICTURE.name() + "')";
			executeUpdate(itemQuery);
			
			final String storyQuery = "INSERT INTO drawing VALUES('" + id + "','" + filename + "')";
			executeUpdate(storyQuery);
			
			final String threadQuery = "UPDATE thread SET last_item_id = '" + id + "' WHERE id = '" + threadId + "'";
			executeUpdate(threadQuery);
		}
		
		return ConfigAdaptor.getInstance().getProperty("drawingsDirectory") + filename;
	}
	
	/**
	 * Retrieve all the threads in the database
	 * @param username	the username of the requester
	 * @return	a map containing two lists, one that contains all the threads in which the user has posted and the other containing the threads in which the user has not
	 * @throws SQLException
	 */
	public Map<SBThreadGroup, List<SBThread>> getThreads(final String username) throws SQLException {
		s_log.debug("Retrieve all threads");
		final List<SBThread> allThreads = new ArrayList<SBThread>();
		final List<SBThread> newThreads = new ArrayList<SBThread>();
		final List<SBThread> oldThreads = new ArrayList<SBThread>();
		final Set<String> oldIds = new HashSet<String>();
		
		final String threadsQuery = "SELECT t.id, t.title, i1.creator, i1.create_datetime AS create_date, i2.creator AS poster, i2.create_datetime AS last_date, i2.action, i2.seq_num FROM thread t, item i1, item i2 WHERE t.id = i1.thread_id AND i1.seq_num = 1 AND t.last_item_id = i2.id ORDER BY last_date desc";
		final ResultSet res1 = executeQuery(threadsQuery);
		while(res1.next()) {
			final String id = res1.getString("id");
			final String title = res1.getString("title");
			final String creator = res1.getString("creator");
			final Date createDate = res1.getTimestamp("create_date");
			final String poster = res1.getString("poster");
			final Date lastDate = res1.getTimestamp("last_date");
			final SBAction nextAction = SBAction.make(res1.getString("action")).getNextAction();
			final int lastSeqNum = res1.getInt("seq_num");
			
			final SBThread thread = new SBThread(id, title, creator, createDate, poster, lastDate, lastSeqNum, nextAction);
			allThreads.add(thread);
		}
		
		final String oldThreadsQuery = "SELECT t.id FROM thread t, item i WHERE t.id = i.thread_id AND i.creator = '" + username + "'";
		final ResultSet res2 = executeQuery(oldThreadsQuery);
		while(res2.next()) {
			final String id = res2.getString("id");
			oldIds.add(id);
		}
		
		for(final SBThread thread : allThreads) {
			if(oldIds.contains(thread.getId())) {
				thread.setNextAction(SBAction.VIEW);
				oldThreads.add(thread);
			}
			else {
				newThreads.add(thread);
			}
		}
		
		final Map<SBThreadGroup, List<SBThread>> threadMap = new HashMap<SBThreadGroup, List<SBThread>>();
		threadMap.put(SBThreadGroup.OLD, oldThreads);
		threadMap.put(SBThreadGroup.NEW, newThreads);
		return threadMap;
	}
	
	/**
	 * Retrieves the last story in a thread
	 * @param threadId	the id of the thread
	 * @return	the most recently posted story in a thread, or null if no story is found in the database
	 * @throws SQLException
	 */
	public String getLastStory(final String threadId) throws SQLException {
		s_log.debug("Retrieve last story");
		final String query = "SELECT s.story FROM thread t, story s WHERE t.id='" + threadId + "' AND t.last_item_id = s.id";
		final ResultSet res = executeQuery(query);
		if(res.next()) {
			return res.getString("story");
		}
		return null;
	}
	
	/**
	 * Retrieves the filename of the last drawing in a thread
	 * @param threadId	the id of the thread
	 * @return	the filename of the most recently posted drawing in a thread, or null if no filename is found
	 * @throws SQLException
	 */
	public String getLastDrawing(final String threadId) throws SQLException {
		s_log.debug("Retrieve last story");
		final String query = "SELECT d.filepath from thread t, drawing d WHERE t.id = '" + threadId + "' AND t.last_item_id = d.id";
		final ResultSet res = executeQuery(query);
		if(res.next()) {
			return res.getString("filepath");
		}
		return null;
	}
	
	/**
	 * Retrieves the filename of a drawing by its id
	 * @param itemId	the id of the drawing
	 * @return	the filename of the drawing, or null if no filename is found
	 * @throws SQLException
	 */
	public String getDrawingById(final String itemId) throws SQLException {
		s_log.debug("Retrieve drawing");
		final String query = "SELECT d.filepath from drawing d WHERE d.id = '" + itemId + "'";
		final ResultSet res = executeQuery(query);
		if(res.next()) {
			return res.getString("filepath");
		}
		return null;
	}
	
	/**
	 * Retrieves a list of all the items in a thread, sorted by the time the items were posted
	 * @param threadId	the id of the thread
	 * @return	a list containing all the items in a thread, sorted by the time the items were posted
	 * @throws SQLException
	 */
	public List<SBItem> getItemList(final String threadId) throws SQLException {
		s_log.debug("Retrieve item list");
		final List<SBItem> itemList = new ArrayList<SBItem>();
		final String query = "SELECT i.id, i.action, i.creator, i.create_datetime, s.story FROM item i LEFT JOIN story s ON i.id = s.id WHERE i.thread_id = '" + threadId + "' ORDER BY i.seq_num";
		final ResultSet res = executeQuery(query);
		while(res.next()) {
			final String itemId = res.getString("id");
			final SBAction action = SBAction.make(res.getString("action"));
			final String creator =  res.getString("creator");
			final String story = res.getString("story");
			final Date createDate = res.getTimestamp("create_datetime");
			itemList.add(new SBItem(action, itemId, story, creator, createDate));
		}
		return itemList;
	}
	
	/**
	 * Retrieves the last sequence number in a thread
	 * @param threadId	the id of the thread
	 * @return	the highest sequence number in the thread
	 * @throws SQLException
	 */
	public int getLastSeqNum(final String threadId) throws SQLException {
		s_log.debug("Retrieve last sequence number");
		final String query = "SELECT max(seq_num) AS max FROM item WHERE thread_id = '" + threadId + "'";
		final ResultSet res = executeQuery(query);
		if(res.next()) {
			return res.getInt("max");
		}
		return -1;
	}
	
	/**
	 * Inserts a new comment into the database
	 * @param username	the username of the submitter
	 * @param comment	the user's comment
	 * @throws SQLException
	 */
	public void newComment(final String username, final String comment) throws SQLException {
		s_log.debug("New comment");
		final String threadQuery = "INSERT INTO comment VALUES('" + username + "', ?, '" + getCurrentTime() + "')";
		executePrepared(threadQuery, comment);
	}
	
	/**
	 * Retrieves all the comments from the database
	 * @return
	 * @throws SQLException
	 */
	public List<SBComment> getCommentList() throws SQLException {
		s_log.debug("Retrieve all comments");
		final List<SBComment> commentList = new ArrayList<SBComment>();
		final String query = "SELECT username, comment, create_datetime FROM comment ORDER BY create_datetime DESC";
		final ResultSet res = executeQuery(query);
		while(res.next()) {
			final String username = res.getString("username");
			final String comment = res.getString("comment");
			final Date datetime = res.getTimestamp("create_datetime");
			commentList.add(new SBComment(username, comment, datetime));
		}
		return commentList;
	}
	
	/**
	 * Deletes a thread and all objects related to it
	 * @param threadId	the id of the thread
	 * @return	a list of the filepaths of the images on the server to be deleted
	 * @throws SQLException
	 */
	public List<String> deleteThread(final String threadId) throws SQLException {
		s_log.debug("Delete thread");
		final List<String> drawingPathList = new ArrayList<String>();
		final String getDrawingsQuery = "SELECT filepath FROM item i, drawing d WHERE i.thread_id = '" + threadId + "' AND i.id = d.id";
		final ResultSet res = executeQuery(getDrawingsQuery);
		while(res.next()) {
			final String filepath = res.getString("filepath");
			drawingPathList.add(filepath);
		}
		
		final String deleteStoryQuery = "DELETE FROM s USING story AS s INNER JOIN item AS i WHERE i.id = s.id AND i.thread_id = '" + threadId + "'";
		final String deleteDrawingQuery = "DELETE FROM d USING drawing AS d INNER JOIN item AS i WHERE i.id = d.id AND i.thread_id = '" + threadId + "'";
		final String deleteItemQuery = "DELETE FROM item WHERE thread_id = '"+ threadId + "'";
		final String deleteThreadQuery = "DELETE FROM thread WHERE id = '" + threadId + "'";
		
		executeUpdate(deleteStoryQuery);
		executeUpdate(deleteDrawingQuery);
		executeUpdate(deleteItemQuery);
		executeUpdate(deleteThreadQuery);
				
		return drawingPathList;
	}
	
	/**
	 * Delete the last post in a thread and all related objects
	 * @param threadId	the id of the thread
	 * @return	the filename of the image to be deleted if the last post was a drawing
	 * @throws SQLException
	 */
	public String deleteLastItem(final String threadId) throws SQLException {
		s_log.debug("Delete last item");
		String filename = null;
		final String lastItemQuery = "SELECT i.id, i.seq_num, i.action FROM thread t, item i WHERE t.id = i.thread_id AND t.last_item_id = i.id AND t.id = '" + threadId + "'";
		final ResultSet itemRes = executeQuery(lastItemQuery);
		if(itemRes.next()) {
			final String itemId = itemRes.getString("id");
			final int seqNum = itemRes.getInt("seq_num");
			final SBAction action = SBAction.make(itemRes.getString("action"));
			if(action == SBAction.PICTURE) {
				final String fileNameQuery = "SELECT filepath FROM drawing WHERE id = '" + itemId + "'";
				final ResultSet fileRes = executeQuery(fileNameQuery);
				if(fileRes.next()) {
					filename = fileRes.getString("filepath");
					final String deleteDrawingQuery = "DELETE FROM drawing WHERE id = '" + itemId + "'";
					executeUpdate(deleteDrawingQuery);
				}
				else {
					s_log.error("Drawing not found in the database");
					return null;
				}
			}
			else if(action == SBAction.STORY) {
				final String deleteStoryQuery = "DELETE FROM story WHERE id = '" + itemId + "'";
				executeUpdate(deleteStoryQuery);
			}
			
			final String deleteItemQuery = "DELETE FROM item WHERE id = '" + itemId + "'";
			executeUpdate(deleteItemQuery);
			
			if(seqNum == 1) {
				// Since we're deleting the only item in the thread, we'll delete the thread as well
				s_log.debug("Only one item in the thread, deleting thread");
				final String deleteThreadQuery = "DELETE FROM thread WHERE id = '" + threadId + "'";
				executeUpdate(deleteThreadQuery);
			}
			else {
				// Reassign the last_item_id of the thread
				s_log.debug("Reassigning thread.last_item_id");
				final String prevItemQuery = "SELECT id FROM item WHERE thread_id = '" + threadId + "' AND seq_num = " + (seqNum - 1);
				final ResultSet prevItemRes = executeQuery(prevItemQuery);
				if(prevItemRes.next()) {
					final String prevItemId = prevItemRes.getString("id");
					
					final String threadQuery = "UPDATE thread SET last_item_id = '" + prevItemId + "' WHERE id = '" + threadId + "'";
					executeUpdate(threadQuery);
				}
				else {
					s_log.error("Previous item not found in the database");
					return null;
				}
			}
		}
		else {
			s_log.error("Last item not found");
			return null;
		}
		
		return filename;
	}
	
	/**
	 * Hashes a password
	 * @param username	the username of the user
	 * @param password	the password of the user
	 * @return	the hashed password
	 */
	private String getPasswordHash(final String username, final String password) {
		final String concat = username + password;
		return "" + concat.hashCode();
	}
	
	/**
	 * Executes and logs a query
	 * @param query	the query
	 * @return	the results of the query
	 * @throws SQLException
	 */
	private ResultSet executeQuery(final String query) throws SQLException {
		connect();
		final Statement st = g_connection.createStatement();
		final ResultSet res = st.executeQuery(query);
		s_log.debug(query);
		return res;
	}
	
	/**
	 * Executes and logs a query
	 * @param query	the query
	 * @throws SQLException
	 */
	private void executeUpdate(final String query) throws SQLException {
		connect();
		final Statement st = g_connection.createStatement();
		st.executeUpdate(query);
		s_log.debug(query);
	}
	
	/**
	 * Executes a prepared statement
	 * @param query	the query
	 * @param str		the string to insert into the query
	 * @throws SQLException
	 */
	private void executePrepared(final String query, final String str) throws SQLException {
		connect();
		final PreparedStatement ps = g_connection.prepareStatement(query);
		ps.setString(1, str);
		ps.executeUpdate();
		s_log.debug(query + " - " + str);
	}
	
	/**
	 * Creates an id for threads and items
	 * @return
	 */
	private static synchronized String createId() {
		return "" + new Date().getTime();
	}
	
	/**
	 * Gets the current time, formatted for the database
	 * @return	the current time
	 */
	private String getCurrentTime() {
		return DateStringUtil.getDatetime(new Date());
	}
}
