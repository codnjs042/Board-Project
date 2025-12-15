package com.example.demo.global.config;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.ForceLogoutException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SchedulerConfig {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정
    @Transactional
    public void disablePendingUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findByStatus(UserStatus.PENDING);

        for(User user : users){
            if(user.getPendingAt().toLocalDate().plusDays(30).atTime(23, 59, 59).isBefore(now)){
                user.updateStatus(UserStatus.DISABLED);
                throw new ForceLogoutException("계정이 비활성화되어 로그아웃되었습니다.");
            }
        }
    }
}
