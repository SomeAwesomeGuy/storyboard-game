package utilities;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

public class LogFactory {
		
	public static void init(String configPath) {
		DOMConfigurator.configure(configPath);
	}
		
	@SuppressWarnings("rawtypes")
	public static Logger getLogger(Class clazz) {
		return Logger.getLogger(clazz);
	}
	
	public static Logger getLogger(String string) {
		return Logger.getLogger(string);
	}
}
