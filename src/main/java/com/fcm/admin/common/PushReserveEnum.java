package com.fcm.admin.common;

/**
 * 푸시 예약 상태값 Enum
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
public enum PushReserveEnum {

    /**
     * 발송
     */
    SENT("1"),

    /**
     * 미발송
     */
    NOT_SENT("0")
    ;

    private String status;

    PushReserveEnum(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
}
