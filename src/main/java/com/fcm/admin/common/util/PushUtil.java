package com.fcm.admin.common.util;

import com.fcm.admin.PushJob;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * 푸시 유틸 클래스
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public class PushUtil {
    private static final Logger logger = LoggerFactory.getLogger(PushJob.class);

    /**
     * FCM admin 접근 토큰 얻기
     * @return
     * @throws IOException
     */
    private static String getAccessToken() throws IOException {
        GoogleCredential googleCredential = GoogleCredential
                .fromStream(new FileInputStream("./serviceAccountKey.json"))
                .createScoped(PropertyUtil.getProperties("fcm.messaging.scope1", "fcm.messaging.scope2"));
        googleCredential.refreshToken();
        return googleCredential.getAccessToken();
    }

    /**
     * FCM admin 서버 연결
     * (push 전송 승인 얻기)
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getConnection() throws IOException {
        // [START use_access_token]
        URL url = new URL(PropertyUtil.getProperty("fcm.base.url") + PropertyUtil.getProperty("fcm.send.endpoint"));
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    /**
     * push 요청 프린트
     * @param jsonObject
     */
    public static void prettyPrint(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        logger.debug(gson.toJson(jsonObject) + "\n");
    }

    /**
     * InputStream -> String 반환
     * @param inputStream
     * @return
     */
    public static String inputStreamToString(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.nextLine());
        }
        return stringBuilder.toString();
    }

    /**
     * 현재 서버시각 yyyyMMddhhkkss 구하기
     * @return
     */
    public static String getServerDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhkkss");
        Date now = new Date();

        return sdf.format(now);
    }
}
