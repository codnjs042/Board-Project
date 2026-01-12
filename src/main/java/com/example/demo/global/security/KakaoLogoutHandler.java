package com.example.demo.global.security;

import com.example.demo.global.infra.kakao.service.KakaoService;
import com.example.demo.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoLogoutHandler implements LogoutHandler {
    public final SecurityUtil securityUtil;
    public final KakaoService kakaoService;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication){
        String token = securityUtil.getKakaoToken(request);
        if (token != null)
            kakaoService.logout(token);
    }
}
