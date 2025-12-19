package com.example.demo.global.advice;

import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {
    private final UserService userService;
    @ModelAttribute("user")
    public UserResponseDto addUserToModel(@AuthenticationPrincipal CustomUserDetails userDetails){
        if(userDetails!=null){
            return userService.userInfo(userDetails);
        }
        return null;
    }
}
