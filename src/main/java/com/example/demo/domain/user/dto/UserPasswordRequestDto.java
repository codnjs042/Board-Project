package com.example.demo.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordRequestDto {
    private String currentPw;
    private String newPw;
    private String confirmPw;
}
