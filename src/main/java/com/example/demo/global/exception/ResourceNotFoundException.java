package com.example.demo.global.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(Exception e){
        super(e.getMessage());
    }
}