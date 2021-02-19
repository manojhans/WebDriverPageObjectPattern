package com.claritybot.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Manoj Hans
 **/
public class PropertyReader {

    private static final String path = String.format("%s/src/main/resources/config/", System.getProperty("user.dir"));
    private static Properties prop = new Properties();

    /***
     * It initialize the property file.
     * @param fileName
     */
    public PropertyReader(String fileName) {
        InputStream input;
        try {
            input = new FileInputStream(path + fileName);
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
