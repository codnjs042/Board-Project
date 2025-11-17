package com.example.demo.admin.dto;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.domain.UserStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAdminResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public static UserAdminResponseDto from(User user){
        return UserAdminResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}