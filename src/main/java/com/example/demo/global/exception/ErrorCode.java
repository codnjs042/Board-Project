package com.example.demo.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_ALREADY_EXIST(HttpStatus.CONFLICT, "U001", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U002", "사용자가 존재하지 않습니다."),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "I001", "입력 형식이 올바르지 않습니다."),
    MISMATCH(HttpStatus.BAD_REQUEST, "M001", "입력값이 일치하지 않습니다."),
    POLICY_VIOLATION(HttpStatus.UNPROCESSABLE_ENTITY, "P001", "정책상 허용되지 않습니다."),
    FORCE_LOGOUT(HttpStatus.FOUND, "F002", "정책상 강제 로그아웃됩니다. 다시 로그인해주세요."),
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "P002", "게시글이 존재하지 않습니다."),
    INVALID_PERMISSION(HttpStatus.BAD_REQUEST, "I002", "권한이 없습니다."),
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "C001", "댓글이 존재하지 않습니다."),
    DATA_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "D001", "데이터 처리 중 오류가 발생했습니다."),
    KAKAO_API_ERROR(HttpStatus.BAD_GATEWAY, "K001", "카카오 API 호출 중 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
