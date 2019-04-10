package com.fcm.admin;

import com.fcm.admin.dto.PushReserveDTO;
import com.fcm.admin.dto.PushResultDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DB 처리용 Runnable 구현 클래스
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class DbRunnableImpl implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(DbRunnableImpl.class);

    private List<PushReserveDTO> pushReserveList = new ArrayList<>();

    private List<PushResultDTO> pushResultList = new ArrayList<>();

    private static SqlSessionFactory sqlSessionFactory = SqlFactoryClient.getSqlSessionFactory();

    DbRunnableImpl() {}

    DbRunnableImpl(List<PushReserveDTO> pushReserveList, List<PushResultDTO> pushResultList) {
        this.pushReserveList = pushReserveList;
        this.pushResultList = pushResultList;
    }

    @Override
    public void run() {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        try {
            logger.debug("final db update =============================================");

            if (this.pushReserveList.size() > 0) {
                sqlSession.update("push_reserve-sql.updateStatus4List", this.pushReserveList);
                logger.debug("update pushReserveList ::: \n" + Arrays.asList(this.pushReserveList));
            }

            if (this.pushResultList.size() > 0) {
                sqlSession.update("push_result-sql.updateResult4List", this.pushResultList);
                logger.debug("update pushReserveList ::: \n" + Arrays.asList(this.pushReserveList));
            }
        } catch(Exception e) {
            logger.debug(e.getMessage());
        } finally {
            sqlSession.close();
        }
    }
}
