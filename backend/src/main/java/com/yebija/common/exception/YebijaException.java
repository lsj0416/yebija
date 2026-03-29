package com.yebija.common.exception;

import lombok.Getter;

@Getter
public class YebijaException extends RuntimeException {

    private final ErrorCode errorCode;

    public YebijaException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
