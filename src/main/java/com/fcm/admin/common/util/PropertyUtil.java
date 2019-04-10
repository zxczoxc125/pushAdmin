package com.fcm.admin.common.util;

import org.apache.ibatis.io.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 프로퍼티 유틸 클래스
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class PropertyUtil {
    private static final Logger logger = LoggerFactory.getLogger(PropertyUtil.class);

    static final String resource = "system.properties";

    static Properties properties = new Properties();

    /**
     * 프로퍼티 값 읽기
     * @param key
     * @return
     */
    public static String getProperty(String key) {
        try {
            Reader reader = Resources.getResourceAsReader(resource);
            properties.load(reader);
        } catch(IOException e) {
            logger.debug(e.getMessage());
        }

        return properties.getProperty(key);
    }

    /**
     * 배열 형태 프로퍼티 값 읽기
     * @param keyList
     * @return
     */
    public static List<String> getProperties(String... keyList) {
        List<String> propertyList = new ArrayList<>();

        try {
            Reader reader = Resources.getResourceAsReader(resource);
            properties.load(reader);

            for (String key: keyList) {
                propertyList.add(properties.getProperty(key));
            }
        } catch(IOException e) {
            logger.debug(e.getMessage());
        }

        return propertyList;
    }
}
