package com.example.demo.domain.user.controller;

import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.service.PostQueryService;
import com.example.demo.domain.user.dto.*;
import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final PostQueryService postQueryService;
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
    public String signup(@ModelAttribute UserSignupRequestDto dto){
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
    public String updateNickname(@RequestParam String nickname,
                                 @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.updateNickname(nickname, userDetails);
        return "redirect:/user/myPage";
    }

    @GetMapping("/user/myHistory")
    public String myHistory(@RequestParam(defaultValue="0") int page,
                            @AuthenticationPrincipal CustomUserDetails userDetails,
                            Model model){
        Page<PostResponseDto> PostPage = postQueryService.getUserPosts(userDetails.getId(), PostState.PUBLISHED, page);
        model.addAttribute("postPage", PostPage);
        return "user/myHistory";
    }

    @GetMapping("/user/pwPage")
    public String pwPage(){
        return "user/pwPage";
    }

    @PostMapping("/user/pwPage")
    public String pwPage(@ModelAttribute UserPasswordRequestDto dto,
                         @AuthenticationPrincipal CustomUserDetails userDetails){
        userService.updatePassword(dto, userDetails);
        return "redirect:/user/login";
    }

    @GetMapping("/user/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model){
        UserStatusResponseDto userStatus = userService.userStatus(userDetails);
        model.addAttribute("userStatus", userStatus);
        return "user/delete";
    }

    @PostMapping("/user/delete")
    public String delete(@AuthenticationPrincipal CustomUserDetails userDetails){
        userService.deleteToggle(userDetails);
        return "redirect:/user/delete";
    }

    @GetMapping("/user/draft")
    public String draft(@RequestParam(defaultValue="0") int page,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model){
        Page<PostResponseDto> draft = postQueryService.getUserPosts(userDetails.getId(), PostState.DRAFT, page);
        model.addAttribute("draft", draft);
        return "user/draft";
    }
}