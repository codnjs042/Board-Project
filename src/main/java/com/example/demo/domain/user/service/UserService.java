package com.example.demo.domain.user.service;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.dto.*;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.ForceLogoutException;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final PostService postService;

    @Transactional
    public void signup(UserSignupRequestDto dto){
        Optional<User> existing = userRepository.findByUsername(dto.getUsername());

        if(existing.isPresent()){
            throw new IllegalArgumentException("이미 존재하는 사용자입니다");
        }
        String password = dto.getPassword().trim();
        if(!dto.getUsername().matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{8,12}$")){
            throw new IllegalArgumentException("아이디는 알파벳, 숫자가 포함된 8~12자리만 가능합니다");
        }
        if(!dto.getPassword().matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]{8,12}$")){
            throw new IllegalArgumentException("비밀번호는 알파벳, 숫자, 특수문자(!@#$%^&*)가 포함된 8~12자리만 가능합니다");
        }

        String encodePw = passwordEncoder.encode(password);

        User user = User.builder()
                .username(dto.getUsername())
                .password(encodePw)
                .nickname(dto.getNickname())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);
    }

    @Transactional
    public UserResponseDto userInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResponseDto.from(user);
    }

    @Transactional
    public void updateNickname(String nickname, CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        if(user.getRole()==UserRole.KAKAO_USER){
            throw new IllegalArgumentException("소셜 로그인 사용자는 닉네임을 변경할 수 없습니다.");
        }

        user.updateNickname(nickname);
    }

    @Transactional(noRollbackFor = ForceLogoutException.class)
    public void updatePassword(UserPasswordRequestDto pwDto, CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        if(user.getRole()==UserRole.KAKAO_USER){
            throw new IllegalArgumentException("소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");
        }

        if(!passwordEncoder.matches(pwDto.getCurrentPw(), user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if(pwDto.getNewPw().equals(pwDto.getCurrentPw())){
            throw new IllegalArgumentException("현재 비밀번호와 같은 비밀번호로 변경할 수 없습니다.");
        }
        if(!pwDto.getNewPw().equals(pwDto.getConfirmPw())){
            throw new IllegalArgumentException("변경 비밀번호가 일치하지 않습니다.");
        }

        String encodePw = passwordEncoder.encode(pwDto.getNewPw());
        user.updatePassword(encodePw);
        throw new ForceLogoutException("비밀번호가 변경되어 로그아웃되었습니다. 다시 로그인 해주세요.");
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAllMyPost(String username, PostState status, int page) {
        return postService.findAllMyPost(username, status, page);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchMyPost(String username, PostState status, String keyword, String type, int page) {
        return postService.searchMyPost(username, status, keyword, type, page);
    }

    @Transactional
    public void deleteToggle(CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        if(user.getRole()==UserRole.SUPER_ADMIN)
            throw new IllegalArgumentException("슈퍼 관리자는 본인 계정을 삭제할 수 없습니다");

        if(user.getStatus()==UserStatus.ACTIVE)
            user.updateStatus(UserStatus.PENDING);

        else if(user.getStatus()==UserStatus.PENDING)
            user.updateStatus(UserStatus.ACTIVE);

        else throw new ForceLogoutException("계정이 비활성화되어 삭제/복구 요청을 처리할 수 없습니다. 관리자에게 문의하세요.");
    }

    @Transactional
    public UserStatusResponseDto userStatus(CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        return new UserStatusResponseDto(user);
    }
}