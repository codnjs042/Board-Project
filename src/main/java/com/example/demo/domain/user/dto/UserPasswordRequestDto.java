package com.example.demo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordRequestDto {
    @NotBlank(message="현재 비밀번호를 입력해주세요.")
    private String rawPw;

    @NotBlank(message="새 비밀번호를 입력해주세요.")
    @Size(min=8, max=12)
    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$",
            message="비밀번호는 8~12자 이내로 영문, 숫자, 특수문자(!@#$%^&*)를 혼합하여 입력해주세요.")
    private String newPw;

    @NotBlank(message="새 비밀번호를 다시 입력해주세요.")
    private String confirmPw;

    public boolean matchNewPw(){
        return newPw.equals(rawPw);
    }

    public boolean mismatchConfirmPw(){
        return !confirmPw.equals(newPw);
    }

}
