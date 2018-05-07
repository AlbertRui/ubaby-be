package com.ubaby.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

/**
 * @author AlbertRui
 * @date 2018-05-06 23:12
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties props;

    static {

        String fileName = "ubaby.properties";
        props = new Properties();

        ClassLoader classLoader = PropertiesUtil.class.getClassLoader();
        InputStream inStream = classLoader.getResourceAsStream(fileName);
        try {
            Reader reader = new InputStreamReader(inStream, "utf-8");
            props.load(reader);
        } catch (IOException e) {
            logger.error("配置文件读取异常", e);
        }

    }

    public static String getProperty(String key) {

        String value = props.getProperty(key.trim());

        if (StringUtils.isBlank(value))
            return null;

        return value.trim();

    }

    public static String getProperty(String key, String defaultValue) {

        String value = props.getProperty(key.trim());

        if (StringUtils.isBlank(value))
            value = defaultValue;

        return value.trim();

    }

}
