package enums;

public enum SBAction {
	INVALID("", ""),
	PICTURE("Draw", SBPages.DRAW.getAddress()),
	STORY("Write", SBPages.WRITE.getAddress()),
	VIEW("View", SBPages.VIEW.getAddress());
	
	private final String g_displayName;
	private final String g_jspFile;
	
	private SBAction(final String action, final String jspFile) {
		g_displayName = action;
		g_jspFile = jspFile;
	}
	
	public String getDisplayName() {
		return g_displayName;
	}
	
	public String getJSPFile() {
		return g_jspFile;
	}
	
	/**
	 * Returns the action of the next item
	 * @return the action of the next item
	 */
	public SBAction getNextAction() {
		switch(this) {
		case PICTURE:
			return STORY;
		case STORY:
			return PICTURE;
		default:
			return INVALID;
		}
	}
	
	public static SBAction make(String name) {
		for(SBAction type : values()) {
			if(type.name().equals(name)) {
				return type;
			}
		}
		return INVALID;
	}
}
