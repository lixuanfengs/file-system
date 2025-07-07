package net.cactus.utils.file;

import java.io.IOException;
import java.util.Properties;


public class PropertiesUtils {
    public static String getDataBaseProperty(String key) throws Exception {
        Properties prop = new Properties();
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String property = prop.getProperty(key);
        if (property == null) {
            throw new Exception("无法获取key: " + key + " 的值");
        }
        return property;
    }

    public static String getConfigProperty(String key) throws Exception {
        Properties prop = new Properties();
        try {
            prop.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String property = prop.getProperty(key);
        if (property == null) {
            throw new Exception("无法获取key: " + key + " 的值");
        }
        return property;
    }
}
