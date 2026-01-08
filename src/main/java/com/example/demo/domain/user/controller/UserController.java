package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.*;
import com.example.demo.domain.user.service.UserFacade;
import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final UserFacade userFacade;
    private final KakaoComponent kakaoComponent;

    @GetMapping
    public String home(){
        return "index";
    }

    @GetMapping("/user/signup")
    public String signup(){
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String signup(@Valid @ModelAttribute UserSignupRequestDto dto){
        userService.signup(dto);
        return "redirect:/user/login";
    }

    @GetMapping("/user/login")
    public String login(Model model){
        model.addAttribute("kakao", kakaoComponent);
        return "user/login";
    }

    @GetMapping({"/user", "/user/myPage"})
    public String myPage(){
        return "user/myPage";
    }

    @PostMapping("/user/myPage")
    public String updateNickname(@Valid @ModelAttribute UserInfoRequestDto dto,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        userFacade.updateNickname(dto, userDetails.getId());
        return "redirect:/user/myPage";
    }

    @GetMapping("/user/pwPage")
    public String pwPage(){
        return "user/pwPage";
    }

    @PostMapping("/user/pwPage")
    public String pwPage(@Valid @ModelAttribute UserPasswordRequestDto dto,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.updatePassword(dto, userDetails);
        return "redirect:/user/login";
    }

    @GetMapping("/user/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model){
        UserStatusResponseDto userStatus = UserStatusResponseDto.from(userDetails.getUser());
        model.addAttribute("userStatus", userStatus);
        return "user/delete";
    }

    @PostMapping("/user/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteToggle(userDetails.getId());
        return "redirect:/user/delete";
    }

    @GetMapping("/user/draft")
    public String draft(@RequestParam(defaultValue="0") int page,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model){
        Page<PostResponseDto> draftPage = userFacade.getUserPosts(userDetails.getId(), PostState.DRAFT, page);
        model.addAttribute("draftPage", draftPage);
        return "user/draft";
    }

    @GetMapping("/user/{userId}/profile")
    public String profile(@RequestParam(defaultValue="0") int page,
                          @PathVariable Long userId,
                          Model model){
        UserProfileResponseDto profileUser = userService.getUserProfile(userId);
        model.addAttribute("profileUser", profileUser);
        Page<PostResponseDto> postPage = userFacade.getUserPosts(userId, PostState.PUBLISHED, page);
        model.addAttribute("postPage", postPage);
        return "user/profile";
    }
}