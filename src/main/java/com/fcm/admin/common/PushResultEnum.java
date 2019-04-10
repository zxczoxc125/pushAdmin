package com.fcm.admin.common;

/**
 * 푸시 결과 상태값 Enum
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public enum PushResultEnum {

    /**
     * 성공
     */
    SUCCESS("1"),

    /**
     * 미발송
     */
    NOT_SENT("0"),

    /**
     * 실패
     */
    FAIL("2")
    ;

    private String result;

    PushResultEnum(String result) {
        this.result = result;
    }
    public String getResult() {
        return result;
    }
}
