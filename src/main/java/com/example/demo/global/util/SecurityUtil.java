package com.example.demo.global.util;

import com.example.demo.domain.user.domain.User;
import com.example.demo.global.security.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

}
