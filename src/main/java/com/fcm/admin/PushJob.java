package com.fcm.admin;

import com.fcm.admin.common.PushReserveEnum;
import com.fcm.admin.common.PushResultEnum;
import com.fcm.admin.common.util.PushUtil;
import com.fcm.admin.dto.PushReserveDTO;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fcm.admin.dto.PushResultDTO;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;

/**
 * 구체적인 작업 내용
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class PushJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(PushJob.class);

    private static SqlSessionFactory sqlSessionFactory = SqlFactoryClient.getSqlSessionFactory();

    /**
     * push 내용 세팅
     * -> 키값은 변경될 수 없음.(fcm에 맞게 세팅)
     * -> 그 외 정보는 "data" 키에 담아 전송
     * @param deviceToken
     * @param title
     * @param contents
     * @return
     */
    private static JsonObject buildNotificationMessage(String deviceToken, String title, String contents) {
        JsonObject jNotification = new JsonObject();
        jNotification.addProperty("title", title);
        jNotification.addProperty("body", contents);

        JsonObject jMessage = new JsonObject();
        jMessage.add("notification", jNotification);

        /* 추가 데이터가 필요할 시 "data"에 담아 전송 */
//        JsonObject jData = new JsonObject();
//        jData.addProperty("k1", "v1");
//        jData.addProperty("k2", "v2");
//        jMessage.add("data", jData);

        /* topic, token은 함께 사용할 수 X */
//        jMessage.addProperty("topic", "news");
        jMessage.addProperty("token", deviceToken);

        JsonObject jFcm = new JsonObject();
        jFcm.add("message", jMessage);

        return jFcm;
    }

    /**
     * push 보내기
     * @param pushResult
     * @param title
     * @param contents
     * @throws IOException
     */
    public static void sendCommonMessage(PushResultDTO pushResult, String title, String contents) throws IOException {
        JsonObject notificationMessage = buildNotificationMessage(pushResult.getDeviceToken(), title, contents);
        logger.debug("FCM request body for message using common notification object:");
        /* push 요청 프린트  */
        PushUtil.prettyPrint(notificationMessage);
        /* push 실제 전송 */
        sendMessage(pushResult, notificationMessage);
    }

    /**
     * push 실제 전송
     * @param pushResult
     * @param fcmMessage
     * @throws IOException
     */
    private static void sendMessage(PushResultDTO pushResult, JsonObject fcmMessage) throws IOException {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        try {
            /* FCM admin 서벼 연결 */
            HttpURLConnection connection = PushUtil.getConnection();
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(fcmMessage.toString());
            /* push 전송 */
            outputStream.flush();
            outputStream.close();

            /* 발송시간 세팅 */
            pushResult.setSentDt(PushUtil.getServerDate());

            /* 푸쉬 후 cs_push_reserve 상태 변경 */
            if (connection.getResponseCode() == 200) {
                String response = PushUtil.inputStreamToString(connection.getInputStream());
                logger.debug("Message sent to Firebase for delivery, response: \n" + response);

                pushResult.setResult(PushResultEnum.SUCCESS.getResult());

                logger.debug("update pushResult(success): " + pushResult.toString());
            } else {
                String response = PushUtil.inputStreamToString(connection.getErrorStream());
                logger.debug("Unable to send message to Firebase: \n" + response);

                pushResult.setResult(PushResultEnum.FAIL.getResult());

                /* 에러 메세지 세팅 */
                JsonParser jsonParser = new JsonParser();
                JsonObject jsonResponse = (JsonObject) jsonParser.parse(response);

                JsonObject jsonError = (JsonObject) jsonParser.parse(String.valueOf(jsonResponse.get("error")));
                pushResult.setErrorCode(String.valueOf(jsonError.get("code")));
                pushResult.setErrorMessage(String.valueOf(jsonError.get("message")));
                pushResult.setErrorStatus(String.valueOf(jsonError.get("status")));

                logger.debug("update pushResult(fail): " + pushResult.toString());
            }
        } catch(Exception e) {
            logger.debug(e.getMessage());
        } finally {
            sqlSession.close();
        }
    }

    /**
     * Job 실행사항
     * @param jobExecutionContext
     */
    public void execute(JobExecutionContext jobExecutionContext) {
        SqlSession sqlSession = sqlSessionFactory.openSession(true);

        try {
            /* 푸시 대상 리스트(회차 단위) */
            List<PushReserveDTO> pushReserveList = sqlSession.selectList("push_reserve-sql.selectListNotSent");

            /* 발송대기 상태로 변경 */
            for (PushReserveDTO pushReserve: pushReserveList) {
                pushReserve.setStatus(PushReserveEnum.WAITING_SEND.getStatus());
            }
            if (pushReserveList.size() > 0)
                sqlSession.update("push_reserve-sql.updateStatus4List", pushReserveList);

            /* 푸시건별 세부 작업 */
            List<PushResultDTO> pushResultList = new ArrayList<>();
            /* Db Thread에서 처리할 최종 값이 담길 세부 리스트 */
            List<PushResultDTO> finalPushResultList = new ArrayList<>();
            for (PushReserveDTO pushReserve: pushReserveList) {
                pushResultList = sqlSession.selectList("push_result-sql.selectListNotSentDetail", pushReserve.getSeq());

                /* 발송대기 상태로 변경 */
                for (PushResultDTO pushResult: pushResultList) {
                    pushResult.setResult(PushResultEnum.WATING_SEND.getResult());
                }
                if (pushResultList.size() > 0)
                    sqlSession.update("push_result-sql.updateResult4List", pushResultList);

                /* 발송시간 세팅 */
                pushReserve.setSentDt(PushUtil.getServerDate());

                /* 푸시 대상 세부 리스트(회원 단위) */
                for (PushResultDTO pushResult: pushResultList) {
                    sendCommonMessage(pushResult, pushReserve.getTitle(), pushReserve.getContents());
                    finalPushResultList.add(pushResult);
                }

                pushReserve.setStatus(PushReserveEnum.SENT.getStatus());
            }

            /* push 기능과 db저장 기능 분리 */
            DbRunnableImpl dbRunnable = new DbRunnableImpl(pushReserveList, finalPushResultList);
            Thread dbThread = new Thread(dbRunnable);

            dbThread.start();
        } catch (IOException e) {
            logger.debug(e.getMessage());
        } finally {
            sqlSession.close();
        }


    }




}
