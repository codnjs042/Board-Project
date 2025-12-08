package com.example.demo.global.advice;

import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttribute {
    private final UserService userService;
    @ModelAttribute("user")
    public UserResponseDto addUserToModel(Principal principal){
        if(principal!=null){
            return userService.userInfo(principal.getName());
        }
        return null;
    }
}
