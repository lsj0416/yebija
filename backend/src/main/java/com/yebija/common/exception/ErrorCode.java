package com.yebija.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
    INVALID_INPUT(400, "INVALID_INPUT", "입력값이 올바르지 않습니다."),

    // 인증
    UNAUTHORIZED(401, "UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", "만료된 토큰입니다."),

    // 교회/계정
    DUPLICATE_EMAIL(409, "DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
    CHURCH_NOT_FOUND(404, "CHURCH_NOT_FOUND", "존재하지 않는 교회입니다."),
    INVALID_PASSWORD(401, "INVALID_PASSWORD", "비밀번호가 올바르지 않습니다."),

    // 성경
    BIBLE_SCRAPING_FAILED(502, "BIBLE_SCRAPING_FAILED", "성경 데이터를 가져오지 못했습니다."),
    BIBLE_NOT_FOUND(404, "BIBLE_NOT_FOUND", "해당 성경 구절을 찾을 수 없습니다.");

    private final int httpStatus;
    private final String code;
    private final String message;
}
