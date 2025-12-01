package com.example.demo.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Builder
public class ErrorMessage {
    private HttpStatus status;
    private String message;
    private List<String> errors;


    /**
     * 단일 에러 메세지를 설정하는 편의 메서드(오버로딩)
     * 내부적으로 List로 변환되어 errors 필드에 저장
     */
    public static class ErrorMessageBuilder{
        public ErrorMessageBuilder errors(String error){
            this.errors = List.of(error);
            return this;
        }
        public ErrorMessageBuilder errors(List<String> errors){
            this.errors = errors;
            return this;
        }
    }
}

