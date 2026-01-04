package com.example.demo.domain.user.service;

import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.dto.*;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.ForceLogoutException;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private static final int pageSize = 10;

    @Transactional
    public void signup(UserSignupRequestDto dto){
        Optional<User> existing = userRepository.findByUsername(dto.getUsername());

        if(existing.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 사용자입니다");
        }

        String encodePw = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .username(dto.getUsername())
                .password(encodePw)
                .nickname(dto.getNickname())
                .build();
        userRepository.save(user);
    }

    @Transactional(noRollbackFor = ForceLogoutException.class)
    public void updatePassword(UserPasswordRequestDto dto, CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        if(user.isSocialUser()){
            throw new IllegalArgumentException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        if(user.mismatchRawPw(passwordEncoder, dto.getRawPw())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if(dto.matchNewPw()){
            throw new IllegalArgumentException("현재 비밀번호와 같은 비밀번호로 변경할 수 없습니다.");
        }
        if(dto.mismatchConfirmPw()){
            throw new IllegalArgumentException("변경 비밀번호가 일치하지 않습니다.");
        }

        String encodePw = passwordEncoder.encode(dto.getNewPw());
        user.updatePw(encodePw);
        throw new ForceLogoutException("비밀번호가 변경되어 로그아웃되었습니다. 다시 로그인 해주세요.");
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));
    }

    public List<User> findAllById(List<Long> userId){
        return userRepository.findAllById(userId);
    }

    public Page<UserAdminResponseDto> searchUsersForAdmin(UserRole role, UserStatus status, String keyword, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return userRepository.searchUsersForAdmin(role, status, keyword, pageable)
                .map(UserAdminResponseDto::from);
    }

    @Transactional
    public void deleteToggle(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        if(user.isSuperAdmin())
            throw new IllegalArgumentException("슈퍼 관리자는 본인 계정을 삭제할 수 없습니다");

        user.toggleStatusForUser();
    }
}