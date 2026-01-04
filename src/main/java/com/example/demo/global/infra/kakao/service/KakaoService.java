package com.example.demo.global.infra.kakao.service;

import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.global.security.CustomUserDetails;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoService {
    public final KakaoComponent kakaoComponent;
    public final UserRepository userRepository;

    public String tokenRequest(String code){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoComponent.getClientId());
        body.add("redirect_uri", kakaoComponent.getRedirectUri());
        body.add("code", code);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String responseBody = response.getBody();
        System.out.println(responseBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String accessToken = jsonNode.get("access_token").asText();
            return accessToken;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Object> userInfo(String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.profile.nickname\"]");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String responseBody = response.getBody();
        System.out.println(responseBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            Long id = jsonNode.get("id").asLong();
            String nickname = jsonNode
                    .path("kakao_account")
                    .path("profile")
                    .path("nickname")
                    .asText();

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", id);
            userInfo.put("nickname", nickname);

            return userInfo;


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    public User signupOrGet(Map<String, Object> info){
        String id = "k" + String.valueOf(info.get("id"));
        String nickname = (String) info.get("nickname");

        User user = userRepository.findByUsername(id)
                .map(u -> {
                    u.updateStatusForce(UserStatus.ACTIVE);
                    return u;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(id)
                            .nickname(nickname)
                            .role(UserRole.KAKAO_USER)
                            .status(UserStatus.ACTIVE)
                            .build();
                    return userRepository.save(newUser);
                });
        return userRepository.save(user);
    }

    public void login(String accessToken, User user, HttpServletRequest request){
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
        );
    }

    @Transactional
    public void disconnect(String target_id, String accessToken){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", Long.parseLong(target_id.substring(1)));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v1/user/unlink",
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        String responseBody = response.getBody();
        System.out.println(responseBody);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            Long id = jsonNode.get("id").asLong();
            User user = userRepository.findByUsername(target_id)
                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
            user.updateStatusForce(UserStatus.DISABLED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
