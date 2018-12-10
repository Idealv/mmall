package com.mall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    private static Properties properties;

    static {
        properties = new Properties();
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream("mall.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            logger.error("配置文件读取异常",e);
        }
    }
    //8088
    public static String getPropery(String key){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            return null;
        }
        return value;
    }

    public static String getPropery(String key,String defaultValue){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)){
            value= defaultValue;
        }
        return value;
    }
}
