package com.example.demo.global.infra.kakao.service;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.exception.ForceLogoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoFacade {
    public final UserService userService;
    public final RestTemplate restTemplate;

    @Transactional
    public User signupOrGet(Map<String, Object> info){
        String username = "k" + info.get("id");
        String nickname = (String) info.get("nickname");

        User user = userService.saveKakaoUser(username, nickname);
        return user;
    }

    @Transactional(noRollbackFor = ForceLogoutException.class)
    public void disconnect(String accessToken, User user){
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);

            RequestEntity<Void> request = RequestEntity
                    .post("https://kapi.kakao.com/v1/user/unlink")
                    .headers(headers)
                    .build();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            String responseBody = response.getBody();
            log.info("응답 바디 : {}", responseBody);
        }catch(RestClientException e){
            log.warn("카카오 api 로그아웃 호출 실패");
        }
        userService.findByUsername(user.getUsername())
                .ifPresent(u -> {
                    u.updateStatusForce(UserStatus.DISABLED);
                    throw new ForceLogoutException(ErrorCode.FORCE_LOGOUT);
                });
    }
}
