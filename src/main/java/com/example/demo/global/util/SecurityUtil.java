package com.example.demo.global.util;

import com.example.demo.domain.user.domain.User;
import com.example.demo.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {
    public void updateSecurityContext(User user){
        CustomUserDetails newUserDetails = new CustomUserDetails(user);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null,
                newUserDetails.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public void kakaoLogin(User user, String token, HttpServletRequest request){
        updateSecurityContext(user);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
        session.setAttribute("KAKAO_ACCESS_TOKEN", token);
    }

    public String getKakaoToken(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null) {
            String token = (String) session.getAttribute("KAKAO_ACCESS_TOKEN");
            if(token != null)
                return token;
        }return null;
    }
}
