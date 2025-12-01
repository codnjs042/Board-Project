package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserStatusResponseDto {
    public UserStatus status;
    public LocalDateTime pendingAt;

    public UserStatusResponseDto(User user){
        this.status=user.getStatus();
        this.pendingAt=user.getPendingAt();
    }

    public static UserStatusResponseDto from(User user){
        return new UserStatusResponseDto(user);
    }
}
