package objects;

import java.util.Date;

import utilities.DateStringUtil;

public class SBComment {
	private final String g_username;
	private final String g_comment;
	private final Date g_datetime;
	
	public SBComment(final String username, final String comment, final Date datetime) {
		g_username = username;
		g_comment = comment;
		g_datetime = datetime;
	}
	
	public String getUsername() {
		return g_username;
	}
	
	public String getComment() {
		return g_comment;
	}
	
	public Date getDate() {
		return g_datetime;
	}
	
	public String getDateString() {
		return DateStringUtil.getDatetime(g_datetime);
	}
}
