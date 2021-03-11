package com.claritybot.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

import static java.lang.String.format;

/**
 * @author Manoj Hans
 **/
public class PropertyReader {

    private static final Properties prop = new Properties();

    /***
     * It initialize the property file.
     * @param fileName: this is config file name
     */
    public PropertyReader(String fileName) {
        try {
            var input = new FileInputStream(Objects.requireNonNull(getClass()
                .getClassLoader()
                .getResource(format("config/%s", fileName)))
                .getFile());
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }
}
