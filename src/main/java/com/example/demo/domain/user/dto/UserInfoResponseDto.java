package com.example.demo.domain.user.dto;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private Long id;
    private String username;
    private String nickname;
    private UserRole role;
    private Boolean isAdmin;

    public static UserInfoResponseDto from(User user){
        return UserInfoResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .role(user.getRole())
                .isAdmin(user.isAdmin())
                .build();
    }
}
