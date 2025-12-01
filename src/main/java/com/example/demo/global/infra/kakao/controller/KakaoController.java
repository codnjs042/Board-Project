package com.example.demo.global.infra.kakao.controller;
import com.example.demo.global.infra.kakao.service.KakaoService;
import com.example.demo.domain.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@RequiredArgsConstructor
@Controller
public class KakaoController {
    public final KakaoService kakaoService;

    @GetMapping("/kakao/login")
    public String kakaoLogin(@RequestParam(required = true) String code, HttpServletRequest request){
        String token = kakaoService.tokenRequest(code);
        Map<String, Object> info = kakaoService.userInfo(token);
        User user = kakaoService.signupOrGet(info);
        kakaoService.login(token, user, request);
        HttpSession session = request.getSession(true);
        session.setAttribute("KAKAO_ACCESS_TOKEN", token);
        return "redirect:/";
    }
}
