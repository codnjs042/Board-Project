package com.example.demo.global.infra.kakao.controller;
import com.example.demo.domain.user.dto.UserInfoResponseDto;
import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.global.infra.kakao.service.KakaoFacade;
import com.example.demo.global.infra.kakao.service.KakaoService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.security.CustomUserDetails;
import com.example.demo.global.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@RequiredArgsConstructor
@Controller
@RequestMapping("/kakao")
public class KakaoController {
    public final KakaoService kakaoService;
    public final KakaoFacade kakaoFacade;
    public final KakaoComponent kakaoComponent;
    public final SecurityUtil securityUtil;

    @GetMapping("/auth")
    public String kakaoAuth(){
        String uri = "https://kauth.kakao.com/oauth/authorize?response_type=code" +
                "&client_id=" + kakaoComponent.getClientId() +
                "&redirect_uri=" + kakaoComponent.getRedirectUri();
        return "redirect:" + uri;
    }

    @GetMapping("/login")
    public String kakaoLogin(@RequestParam String code,
                             HttpServletRequest request) throws JsonProcessingException {
        String token = kakaoService.requestToken(code);
        Map<String, Object> info = kakaoService.userInfo(token);
        User user = kakaoFacade.signupOrGet(info);
        securityUtil.kakaoLogin(user, token, request);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String kakaoDelete(HttpServletRequest request,
                              @AuthenticationPrincipal CustomUserDetails userDetails) throws JsonProcessingException{
        String token = securityUtil.getKakaoToken(request);
        if (token != null)
            kakaoFacade.disconnect(token, userDetails.getUser());
        return "redirect:/";
    }
}
