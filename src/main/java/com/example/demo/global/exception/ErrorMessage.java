package com.example.demo.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
public class ErrorMessage {
    private int status;
    private String error;
    private String message;
}