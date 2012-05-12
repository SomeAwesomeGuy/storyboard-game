package objects;

public class SBUser {
	private final String g_username;
	private final boolean g_isAdmin;
	
	public SBUser(final String username, final boolean isAdmin) {
		g_username = username;
		g_isAdmin = isAdmin;
	}
	
	public String getUsername() {
		return g_username;
	}
	
	public boolean isAdmin() {
		return g_isAdmin;
	}
}
