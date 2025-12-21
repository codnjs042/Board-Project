package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserStatusResponseDto {
    private Long id;
    private UserStatus status;
    private LocalDateTime pendingAt;
    private LocalDateTime deadLine;

    public UserStatusResponseDto(User user){
        this.id=user.getId();
        this.status=user.getStatus();
        this.pendingAt=user.getPendingAt();
        this.deadLine=user.getDeadLine();
    }

    public static UserStatusResponseDto from(User user){
        return new UserStatusResponseDto(user);
    }
}
