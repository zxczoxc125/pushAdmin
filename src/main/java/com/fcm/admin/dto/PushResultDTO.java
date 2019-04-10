package com.fcm.admin.dto;

import lombok.Data;

/**
 * 푸시 결과 DTO
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
@Data
public class PushResultDTO {

    /**
     * 푸시 예약 일련번호
     */
    private String pushReserveSeq;

    /**
     * 회원 아이디
     */
    private String userId;

    /**
     * 디바이스 토큰
     */
    private String deviceToken;

    /**
     * 결과(1. 성공, 0. 미발송, 2. 실패)
     */
    private String result;

    /**
     * 에러 코드
     */
    private String errorCode;

    /**
     * 에러 메세지
     */
    private String errorMessage;

    /**
     * 에러 상태
     */
    private String errorStatus;

    /**
     * 확인 여부
     */
    private String chkYn;

    /**
     * 발송일
     */
    private String sentDt;

    /**
     * 등록자
     */
    private String rgstUsr;

    /**
     * 수정자
     */
    private String rgstDt;

    /**
     * 수정자
     */
    private String updUsr;

    /**
     * 수정일
     */
    private String updDt;

}
