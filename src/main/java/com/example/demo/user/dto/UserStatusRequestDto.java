package com.example.demo.user.dto;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusRequestDto {
    public UserStatus status;

}
