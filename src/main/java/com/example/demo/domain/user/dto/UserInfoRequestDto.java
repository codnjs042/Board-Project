package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoRequestDto {
    @NotBlank(message="닉네임을 입력하세요.")
    @Pattern(regexp="^[가-힣A-Za-z\\d]{2,12}$",
            message="닉네임은 2~12자 이내로 한글, 영문, 숫자만 가능합니다.")
    private String nickname;
}
