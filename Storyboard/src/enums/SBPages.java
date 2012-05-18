package enums;

public enum SBPages {
	WELCOME("Welcome"),
	GAME("Game"),
	LOGIN("Login.jsp"),
	REGISTER("Register.jsp"),
	MAIN("Main.jsp"),
	CREATE("Create.jsp"),
	WRITE("Write.jsp"),
	DRAW("Draw.jsp"),
	ERROR("Error.jsp"),
	COMMENT("Comment.jsp"),
	VIEW("View.jsp"),
	VIEWCOMMENTS("ViewComments.jsp"),
	FAQ("FAQ.jsp");
	
	private String g_address;
	
	private SBPages(final String address) {
		g_address = address;
	}
	
	public String getAddress() {
		return g_address;
	}
}
