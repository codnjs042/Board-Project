package com.example.demo.global.infra.kakao.service;

import com.example.demo.global.infra.kakao.component.KakaoComponent;
import com.example.demo.global.security.CustomUserDetails;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoService {
    public final KakaoComponent kakaoComponent;
    public final RestTemplate restTemplate;
    public final ObjectMapper objectMapper;

    public String requestToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoComponent.getClientId());
        body.add("redirect_uri", kakaoComponent.getRedirectUri());
        body.add("code", code);

        RequestEntity<MultiValueMap<String, String>> request = RequestEntity
                .post("https://kauth.kakao.com/oauth/token")
                .headers(headers)
                .body(body);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        String responseBody = response.getBody();
        log.info("응답 바디 : {}", responseBody);

        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String accessToken = jsonNode.get("access_token").asText();
        return accessToken;
    }

    public Map<String, Object> userInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("property_keys", "[\"kakao_account.profile.nickname\"]");

        RequestEntity<MultiValueMap<String, String>> request = RequestEntity
                .post("https://kapi.kakao.com/v2/user/me")
                .headers(headers)
                .body(body);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        String responseBody = response.getBody();
        log.info("응답 바디 : {}", responseBody);

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
    }

    public void logout(String accessToken){
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RequestEntity<Void> request = RequestEntity
                .post("https://kapi.kakao.com/v1/user/logout")
                .headers(headers)
                .build();
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        String responseBody = response.getBody();
        log.info("응답 바디 : {}", responseBody);
    }
}
