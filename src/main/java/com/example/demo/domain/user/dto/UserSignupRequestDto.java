package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserSignupRequestDto {
    @NotBlank(message="아이디를 입력하세요.")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$",
            message="아이디는 8~12자 이내로 영문, 숫자를 혼합하여 입력해주세요.")
    private String username;

    @NotBlank(message="비밀번호를 입력하세요.")
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$",
            message="비밀번호는 8~12자 이내로 영문, 숫자, 특수문자(!@#$%^&*)를 혼합하여 입력해주세요.")
    private String password;

    @NotBlank(message="닉네임을 입력하세요.")
    @Pattern(regexp="^[가-힣A-Za-z\\d]{2,12}$",
            message="닉네임은 2~12자 이내로 한글, 영문, 숫자만 가능합니다.")
    private String nickname;
}
