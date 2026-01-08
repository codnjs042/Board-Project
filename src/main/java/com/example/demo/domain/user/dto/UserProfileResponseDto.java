package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class UserProfileResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private UserRole role;
    private LocalDateTime createdAt;


    public static UserProfileResponseDto from(User user){
        return UserProfileResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
