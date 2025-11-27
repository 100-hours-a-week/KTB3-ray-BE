package com.example.spring_practice.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // Auth
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "로그인 실패하였습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자 입니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "만료된 엑세스 토큰입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.FORBIDDEN, "유효하지 않은 엑세스 토큰입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "중복된 아이디 입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST,"비밀번호가 올바르지 않습니다."),
    INVALID_PASSWORD_FORM(HttpStatus.BAD_REQUEST,"비밀번호는 8자 이상 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다."),

    SERVER_ERROR(HttpStatus.BAD_REQUEST, "서버 내부 오류입니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NO_PERMISSION(HttpStatus.FORBIDDEN, "수정/삭제 권한이 없습니다."),

    IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장에 실패했습니다"),
    TEST(HttpStatus.FORBIDDEN, "Test");
    private final HttpStatus httpStatus;
    private final String message;
    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
