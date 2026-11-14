package com.diumi.backend.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다."),
    MISSION_SCENARIO_MISMATCH(HttpStatus.BAD_REQUEST, "잠시 문제가 생겼어요. 다시 시도해 주세요."),
    VISITOR_REQUIRED(HttpStatus.UNAUTHORIZED, "방문자 등록이 필요합니다."),
    FORBIDDEN_SESSION(HttpStatus.FORBIDDEN, "다른 방문자의 세션에는 접근할 수 없습니다."),
    SCENARIO_NOT_FOUND(HttpStatus.NOT_FOUND, "시나리오를 찾을 수 없어요."),
    MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "미션을 찾을 수 없어요."),
    SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "세션을 찾을 수 없어요."),
    STEP_NOT_FOUND(HttpStatus.NOT_FOUND, "단계를 찾을 수 없어요."),
    SESSION_ALREADY_ENDED(HttpStatus.CONFLICT, "이미 종료된 세션입니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
