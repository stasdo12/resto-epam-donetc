package com.epam.donetc.restaurant.util;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    private PropertiesUtil(){}
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }

    private static void loadProperties() {
        try(java.io.InputStream inputStream =
                    PropertiesUtil
                            .class
                            .getClassLoader()
                            .getResourceAsStream("application.properties")){
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
