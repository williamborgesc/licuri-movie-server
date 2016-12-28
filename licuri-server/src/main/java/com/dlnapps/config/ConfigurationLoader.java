package com.dlnapps.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

public class ConfigurationLoader {

    private static Properties systemProperties;

    static {

	systemProperties = new Properties();
	InputStream input = null;

	try {

	    input = new FileInputStream("system.properties");
	    
	    // load a properties file
	    systemProperties.load(input);

	} catch (IOException ex) {
	    ex.printStackTrace();
	} finally {
	    IOUtils.closeQuietly(input);
	}

    }

    public static String getDefaultDirectory() {
	return systemProperties.getProperty("default.directory");
    }

    public static String getServerPort() {
	return systemProperties.getProperty("server.port");
    }

    public static String getYtsListUrlBase() {

	return systemProperties.getProperty("yts.list.url.base");
    }
    
    public static String getYtsSearchUrlBase() {
	
	return systemProperties.getProperty("yts.search.url.base");
    }
}
