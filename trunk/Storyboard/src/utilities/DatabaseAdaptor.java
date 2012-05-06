package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import objects.SBItem;
import objects.SBThread;

import enums.SBAction;
import enums.SBThreadGroup;

public class DatabaseAdaptor {
	public static final SimpleDateFormat s_timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private static DatabaseAdaptor s_databaseAdaptor = null;
	
	private Connection g_connection = null;
	private boolean g_printQueries = false;
	
	/**
	 * DatabaseAdaptor constructor
	 */
	private DatabaseAdaptor() {
		final ConfigAdaptor configAdaptor = ConfigAdaptor.getInstance();
		final String database = configAdaptor.getProperty("database");
		final String address = configAdaptor.getProperty("dbaddress");
		final String username = configAdaptor.getProperty("dbusername");
		final String password = configAdaptor.getProperty("dbpassword");
		
		final String url = "jdbc:mysql://" + address + "/" + database;
		
        try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			g_connection = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.err.println("[ERROR][DATABASE]: unable to connect to the database");
			e.printStackTrace();
			//TODO: handle this
		}
        
        System.out.println ("[EVENT][DATABASE]: Database connection established");
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
	public void closeConnection() {
		try {
			g_connection.close();
		} catch (SQLException e) {
			System.err.println("[ERROR][DATABASE]: unable to close database connection");
			e.printStackTrace();
			//TODO: handle this
		}
	}
	
	/**
	 * Compares the given password with the password in the database
	 * @param username	the user's name
	 * @param password	the user's password
	 * @return			true if the login succeeded, false if the login failed
	 * @throws SQLException
	 */
	public boolean login(final String username, final String password, final String ipAddress) throws SQLException {
		System.out.println("[EVENT][DATABASE]: login");
		final String passwordQuery = "SELECT password FROM user where username = '" + username + "'";
		final ResultSet res = executeQuery(passwordQuery);
		if(res.next()) {
			final String dbPassword = res.getString("password");
			final String passHash = getPasswordHash(username, password);
			if(dbPassword.equals(passHash)) {
				final String updateTimeQuery = "UPDATE user SET last_login_datetime = '" + getCurrentTime() + "' where username = '" + username + "'";
				executeUpdate(updateTimeQuery);
				final String updateIPQuery = "UPDATE user SET last_ip_address = '" + ipAddress + "' where username = '" + username + "'";
				executeUpdate(updateIPQuery);
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Inserts a new user into the database
	 * @param username	the user's name
	 * @param password	the user's password
	 * @return			true if the register succeeded, false if the username is already taken
	 * @throws SQLException
	 */
	public boolean register(final String username, final String password, final String ipAddress) throws SQLException {
		System.out.println("[EVENT][DATABASE]: inserting new user");
		final String usernameQuery = "SELECT username FROM user where username = '" + username + "'";
		final ResultSet res = executeQuery(usernameQuery);
		if(res.next()) {
			
			return false;
		}
		
		final String currentTime = getCurrentTime();
		final String passHash = getPasswordHash(username, password);
		final String userQuery = "INSERT INTO user VALUES('" + username + "', '" + passHash + "', '" + currentTime + "', '" + currentTime + "', '" + ipAddress +"')";
		executeUpdate(userQuery);
		
		return true;
	}
	
	/**
	 * Inserts a new thread into the database
	 * @param threadTitle	the title of the thread
	 * @param story			the first story of the thread
	 * @param username		the username of the thread creator
	 * @throws SQLException
	 */
	public void newThread(final String threadTitle, final String story, final String username) throws SQLException {
		System.out.println("[EVENT][DATABASE]: inserting new thread");
		final String id = createId();
		
		// Create thread
		final String threadQuery = "INSERT INTO thread VALUES('" + id + "', '" + threadTitle + "', '')";
		executeUpdate(threadQuery);
		
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
		System.out.println("[EVENT][DATABASE]: inserting new story");
		final String id = createId();
		
		final String maxSeqNumQuery = "SELECT max(seq_num) as max FROM item where thread_id = '" + threadId + "'";
		final ResultSet res = executeQuery(maxSeqNumQuery);
		if(res.next()) {
			final int seqNum = res.getInt("max") + 1;
			
			final String itemQuery = "INSERT INTO item VALUES('" + id + "', '" + threadId + "', " + seqNum + ", '" + username + "', '" + getCurrentTime() + "', '" + SBAction.STORY.name() + "')";
			executeUpdate(itemQuery);
			
			final String storyQuery = "INSERT INTO story VALUES('" + id + "','" + story + "')";
			executeUpdate(storyQuery);
			
			final String threadQuery = "UPDATE thread SET last_item_id = '" + id + "' WHERE id = '" + threadId + "'";
			executeUpdate(threadQuery);
		}
		else {
			System.err.println("[ERROR][DATABASE]: unable to find max seq_num");
			//TODO: handle this
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
		System.out.println("[EVENT][DATABASE]: inserting new drawing");
		final String id = createId();
		final String filename = id + ".png";
		
		final String maxSeqNumQuery = "SELECT max(seq_num) as max FROM item where thread_id = '" + threadId + "'";
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
		else {
			System.err.println("[ERROR][DATABASE]: unable to find max seq_num");
			//TODO: handle this
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
		System.out.println("[EVENT][DATABASE]: Retrieve threads");
		final List<SBThread> allThreads = new ArrayList<SBThread>();
		final List<SBThread> newThreads = new ArrayList<SBThread>();
		final List<SBThread> oldThreads = new ArrayList<SBThread>();
		final Set<String> oldIds = new HashSet<String>();
		
		final String threadsQuery = "SELECT t.id, t.title, i1.creator, i1.create_datetime AS create_date, i2.creator AS poster, i2.create_datetime AS last_date, i2.type FROM thread t, item i1, item i2 WHERE t.id = i1.thread_id AND i1.seq_num = 1 AND t.last_item_id = i2.id ORDER BY last_date desc";
		final ResultSet res1 = executeQuery(threadsQuery);
		while(res1.next()) {
			final String id = res1.getString("id");
			final String title = res1.getString("name");
			final String creator = res1.getString("creator");
			final Date createDate = res1.getTimestamp("create_date");
			final String poster = res1.getString("poster");
			final Date lastDate = res1.getTimestamp("last_date");
			final SBAction nextAction = SBAction.make(res1.getString("type")).getNextAction();
			
			final SBThread thread = new SBThread(id, title, creator, createDate, poster, lastDate, nextAction);
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
		System.out.println("[EVENT][DATABASE]: Retrieve story");
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
		System.out.println("[EVENT][DATABASE]: Retrieve drawing");
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
		System.out.println("[EVENT][DATABASE]: Retrieve drawing");
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
		System.out.println("[EVENT][DATABASE]: Retrieve item list");
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
		final Statement st = g_connection.createStatement();
		final ResultSet res = st.executeQuery(query);
		if(g_printQueries) {
			System.out.println("[INFO][DATABASE]: " + query);
		}
		return res;
	}
	
	/**
	 * Executes and logs a query
	 * @param update	the query
	 * @throws SQLException
	 */
	private void executeUpdate(final String update) throws SQLException {
		final Statement st = g_connection.createStatement();
		st.executeUpdate(update);
		if(g_printQueries) {
			System.out.println("[INFO][DATABASE]: " + update);
		}
	}
	
	/**
	 * Creates an id for threads and items
	 * @return
	 */
	public static synchronized String createId() {
		return "" + new Date().getTime();
	}
	
	/**
	 * Gets the current time, formatted for the database
	 * @return	the current time
	 */
	private String getCurrentTime() {
		return s_timeFormat.format(new Date());
	}
}
