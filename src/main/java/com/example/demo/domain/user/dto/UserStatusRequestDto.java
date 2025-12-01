package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequestDto {
    public UserStatus status;

}
