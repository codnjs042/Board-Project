package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private UserRole role;
    private Boolean isAdmin;

    public static UserResponseDto from(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .isAdmin(user.isAdmin())
                .build();
    }
}
