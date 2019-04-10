package com.fcm.admin.dto;

import lombok.Data;

/**
 * 푸시 예약 DTO
 *
 * @author  이재훈
 * @version 1.0
 * @since   2019-04-10
 */
@Data
public class PushReserveDTO {

    /**
     * 일련번호
     */
    private String seq;

    /**
     * 분류코드
     */
    private String typeCd;

    /**
     * 제목
     */
    private String title;

    /**
     * 내용
     */
    private String contents;

    /**
     * 상태(1.발송, 0. 미발송)
     */
    private String status;

    /**
     * 파라미터
     */
    private String parameter;

    /**
     * 예약일
     */
    private String reserveDt;

    /**
     * 발송일
     */
    private String sentDt;

    /**
     * 등록자
     */
    private String rgstUsr;

    /**
     * 등록일
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
