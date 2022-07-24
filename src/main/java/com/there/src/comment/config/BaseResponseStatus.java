package com.there.src.comment.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    USERS_COMMENT_INVALID_ID(false, 2004, "댓글 작성 권한이 없습니다."),
    COMMENT_INVALID(false, 2005,"유효하지 않는 댓글입니다."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력하세요."),
    POST_USERS_EMPTY_PASSWORD(false, 2012, "비밀번호를 입력하세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXIST_PASSWORD(false, 2017, "중복된 이메일입니다."),

    // [POST] /comments
    COMMENTS_EMPTY_CONTENT(false, 2018, "댓글 내용을 입력하세요."),


    // [PATCH] /users
    POST_USER_EMPTY_NAME(false, 2020, "이름을 입력해주세요."),
    POST_USER_EMPTY_NICKNAME(false, 2021, "닉네임을 입력해주세요."),
    POST_USER_EMPTY_PROFILEIMG(false, 2020, "프로필 사진을 등록해주세요."),
    POST_USER_EMPTY_INFO(false, 2020, "소개를 입력해주세요."),


    /**
     * 3000 : Response 오류
     */

    USERS_EMPTY_USER_ID(false, 3001, "없는 아이디입니다."),
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    DELETE_FAIL_POST(false, 4002, "게시글 삭제를 실패하였습니다. "),
    DELETE_FAIL_USER(false, 4002, "유저 삭제를 실패하였습니다. "),
    DELETE_FAIL_COMMENT(false, 4003, "댓글 삭제를 실패하였습니다. "),

    CREATE_FAIL_COMMENT(false, 4010, "댓글 생성을 실패하였습니다. "),
    CREATE_FAIL_RECOMMENT(false, 4009, "대댓글 생성을 실패하였습니다. "),
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    UPDATE_FAIL_COMMENT(false, 4013,"댓글 수정에 실패하였습니다.");

    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
