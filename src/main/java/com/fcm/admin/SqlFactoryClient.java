package com.fcm.admin;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * SqlSessionFactory 생성 클래스
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class SqlFactoryClient {
    private static SqlSessionFactory SqlSessionFactory;

    static {
        try {
            String resource = "mybatis-config.xml";
            Reader reader = Resources.getResourceAsReader(resource);
            SqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SqlSessionFactory ;
    }

}
