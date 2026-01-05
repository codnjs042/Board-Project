package com.example.demo.global.exception;

import lombok.Getter;

@Getter
public class ForceLogoutException extends RuntimeException{
    private final ErrorCode errorCode;

    public ForceLogoutException(ErrorCode errorCode){
        super(ErrorCode.FORCE_LOGOUT.getMessage());
        this.errorCode=errorCode;
    }
}
