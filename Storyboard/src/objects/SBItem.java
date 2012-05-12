package objects;

import java.text.SimpleDateFormat;
import java.util.Date;

import enums.SBAction;

public class SBItem {
	private static final SimpleDateFormat s_timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private final SBAction g_action;
	private final String g_itemId;
	private final String g_story;
	private final String g_creator;
	private final Date g_createDate;
	
	public SBItem(final SBAction action, final String itemId, final String story, final String creator, final Date createDate) {
		g_action = action;
		g_itemId = itemId;
		g_story = story;
		g_creator = creator;
		g_createDate = createDate;
	}
	
	public SBAction getAction() {
		return g_action;
	}
	
	public String getStory() {
		return g_story;
	}
	
	public String getItemId() {
		return g_itemId;
	}
	
	public String getCreator() {
		return g_creator;
	}
	
	public Date getCreateDate() {
		return g_createDate;
	}
	
	public String getCreateDateString() {
		return s_timeFormat.format(g_createDate);
	}
}
