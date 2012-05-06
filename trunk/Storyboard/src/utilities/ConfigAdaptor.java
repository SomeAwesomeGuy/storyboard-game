package utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ConfigAdaptor {	
	private static ConfigAdaptor s_configAdaptor;
	private static String s_configPath;
	
	private Properties g_properties;
		
	private ConfigAdaptor() {
		if(s_configPath == null) {
			System.err.println("Initialize ConfigAdaptor with the config path before using it");
			System.exit(1);
		}
		
		try {
			final FileInputStream input = new FileInputStream(s_configPath);
			g_properties = new Properties();
			g_properties.loadFromXML(input);
		} catch (FileNotFoundException e) {
			System.err.println("Unable to find config file");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Unable to load config file");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public String getProperty(String name) {
		return g_properties.getProperty(name);
	}
	
	public static ConfigAdaptor getInstance() {
		if(s_configAdaptor == null) {
			s_configAdaptor = new ConfigAdaptor();
		}
		
		return s_configAdaptor;
	}
	
	public static void init(String configPath) {
		s_configPath = configPath;
	}
	
	public static void main(String[] args) {
		ConfigAdaptor.init("WebContent/WEB-INF/config.xml");
		System.out.println(ConfigAdaptor.getInstance().getProperty("database"));
	}
}
