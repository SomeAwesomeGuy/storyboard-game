package objects;

import java.util.Date;

import utilities.DatabaseAdaptor;

import enums.SBAction;

public class SBThread {
	private final String g_id;
	private final String g_title;
	private final String g_creator;
	private final String g_poster;
	private final Date g_create;
	private final Date g_latest;
	private final int g_lastSeqNum;
	private SBAction g_action;
	
	public SBThread(
			final String id, 
			final String title,
			final String creator,
			final Date createDate,
			final String latestPoster,
			final Date latestDate,
			final int lastSeqNum,
			final SBAction action) {
		g_id = id;
		g_title = title;
		g_creator = creator;
		g_create = createDate;
		g_poster = latestPoster;
		g_latest = latestDate;
		g_lastSeqNum = lastSeqNum;
		g_action = action;
	}
	
	public String getId() {
		return g_id;
	}
	
	public String getTitle() {
		return g_title;
	}
	
	public String getCreator() {
		return g_creator;
	}
	
	public Date getCreateDate() {
		return g_create;
	}
	
	public String getCreateDateString() {
		return DatabaseAdaptor.s_timeFormat.format(g_create);
	}
	
	public String getLatestPoster() {
		return g_poster;
	}
	
	public Date getLatestDate() {
		return g_latest;
	}
	
	public String getLatestDateString() {
		return DatabaseAdaptor.s_timeFormat.format(g_latest);
	}
	
	public int getLastSeqNum() {
		return g_lastSeqNum;
	}
	
	public SBAction getNextAction() {
		return g_action;
	}
	
	public void setNextAction(final SBAction action) {
		g_action = action;
	}
}
