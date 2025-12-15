package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.*;
import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.global.infra.kakao.service.KakaoService;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class UserController {
    private final UserService userService;
    private final PostService postService;
    private final KakaoComponent kakaoComponent;
    private final KakaoService kakaoService;

    @GetMapping
    public String main(){
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
    public String myHistory(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String type,
            Principal principal,
            Model model){
        Page<PostResponseDto> PostPage
                = (keyword!=null && !keyword.isBlank())
                ? userService.searchMyPost(principal.getName(), PostState.PUBLISHED, keyword, type, page)
                : userService.findAllMyPost(principal.getName(), PostState.PUBLISHED, page);
        model.addAttribute("postPage", PostPage);
        model.addAttribute("keyword", keyword);
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
    public String draft(@AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model){
        List<PostResponseDto> draft = postService.findDraft(userDetails, PostState.DRAFT);
        model.addAttribute("draft", draft);
        return "user/draft";
    }

//    @PostMapping("/user/login")
//    public String login(@ModelAttribute UserLoginRequestDto dto, HttpSession session, Model model){
//        try {
//            UserResponseDto user = userService.login(dto);
//            if(session.getAttribute("loginUser")!=null)
//                session.invalidate();
//            session.setAttribute("loginUser", user);
//            return "redirect:/";
//        } catch(IllegalArgumentException e){
//            model.addAttribute("error", e.getMessage());
//            return "redirect:/user/login";
//        }
//    }
//
//    @GetMapping("/user/logout")
//    public String logout(HttpSession session){
//        session.invalidate();
//        return "redirect:/";
//    }
}
