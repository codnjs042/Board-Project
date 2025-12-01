package com.example.demo.domain.user.controller;

import com.example.demo.domain.user.dto.*;
import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.global.infra.kakao.service.KakaoService;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
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
    public String main(Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/user/signup")
    public String signup(Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        return "user/signup";
    }

    @PostMapping("/user/signup")
    public String signup(@ModelAttribute UserSignupRequestDto dto, Model model, Principal principal){
        try {
            if(principal!=null) {
                UserResponseDto user = userService.userInfo(principal.getName());
                model.addAttribute("user", user);
            }
            userService.signup(dto);
            return "redirect:/user/login";
        } catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "user/signup";
        }
    }

    @GetMapping("/user/login")
    public String login(@RequestParam(required=false) String error, Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        if(error!=null) model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다");
        model.addAttribute("kakao", kakaoComponent);
        return "user/login";
    }

    @GetMapping("/user/myPage")
    public String myPage(Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        return "user/myPage";
    }

    @PostMapping("/user/myPage")
    public String myPage(@RequestParam String nickname, Principal principal, Model model){
        try{
            userService.updateNickname(principal.getName(), nickname);
            return "redirect:/user/myPage";
        }catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "user/myPage";
        }
    }

    @GetMapping("/user/myHistory")
    public String myHistory(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String type,
            Principal principal,
            Model model){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }

        Page<PostResponseDto> PostPage
                = (keyword!=null && !keyword.isBlank())
                ? userService.searchPosts(principal.getName(), PostState.PUBLISHED, keyword, type, page)
                : userService.findAll(principal.getName(), PostState.PUBLISHED, page);
        model.addAttribute("postPage", PostPage);
        model.addAttribute("keyword", keyword);
        return "user/myHistory";
    }

    @GetMapping("/user/pwPage")
    public String pwPage(Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
            if (user.getRole() == UserRole.KAKAO_USER) {
                return "redirect:/";
            }
        }
        return "user/pwPage";
    }

    @PostMapping("/user/pwPage")
    public String pwPage(@ModelAttribute UserPasswordRequestDto dto, Principal principal, Model model){
        try{
            userService.updatePassword(dto, principal.getName());
            return "redirect:/user/myPage";
        }catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "user/pwPage";
        }
    }

    @GetMapping("/user/delete")
    public String delete(Principal principal, Model model){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
            UserStatusResponseDto userStatus = userService.userStatus(principal.getName());
            model.addAttribute("userStatus", userStatus);
            if (userStatus.pendingAt != null) {
                LocalDateTime deadLine = userStatus.getPendingAt().plusDays(30);
                model.addAttribute("deadLine", deadLine);
            }
        }
        return "user/delete";
    }

    @PostMapping("/user/delete")
    public String delete(@ModelAttribute UserStatusRequestDto dto, Principal principal, HttpServletRequest request, HttpServletResponse response,
                         Authentication authentication){
        UserResponseDto user = userService.userInfo(principal.getName());
        if (user.getRole() == UserRole.KAKAO_USER) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String token = (String) session.getAttribute("KAKAO_ACCESS_TOKEN");
                if (token != null) {
                    kakaoService.disconnect(user.getUsername(), token);
                    new SecurityContextLogoutHandler().logout(request, response, authentication);
                    return "redirect:/";
                }
            }
        }
        userService.deleteToggle(dto, principal.getName());
        return "redirect:/user/delete";
    }

    @GetMapping("/user/draft")
    public String draft(Principal principal, Model model){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        List<PostResponseDto> draft
                = postService.findDraft(principal.getName(), PostState.DRAFT);
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

//    @GetMapping("/user/logout")
//    public String logout(HttpSession session){
//        session.invalidate();
//        return "redirect:/";
//    }
}
