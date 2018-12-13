package com.mall.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigsUtil {
    private static Properties properties;
    private static Logger logger = LoggerFactory.getLogger(ConfigsUtil.class);

    private ConfigsUtil() {
    }

    public static Properties getProperties(String filename) {
        if (properties==null){
            properties = new Properties();
        }
        InputStream is = ConfigsUtil.class.getClassLoader().getResourceAsStream(filename);
        try {
            properties.load(is);
        } catch (IOException e) {
            logger.error("配置文件读取错误,文件名:" + filename, e);
        }
        return properties;
    }

    public static void main(String[] args) {
        Properties properties = ConfigsUtil.getProperties("mall.properties");
        System.out.println(properties.getProperty("ftp.user"));
    }
}
