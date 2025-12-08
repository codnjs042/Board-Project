package com.example.demo.domain.user.service;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.dto.*;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    public void updateNickname(String username, String nickname){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
        if(!user.getUsername().equals(username)){
            throw new IllegalArgumentException("본인 닉네임만 수정할 수 있습니다");
        }
        if(user.getRole()==UserRole.KAKAO_USER){
            throw new IllegalArgumentException("카카오 유저는 닉네임만 수정할 수 없습니다");
        }

        user.updateNickname(nickname);
    }

    @Transactional
    public void updatePassword(UserPasswordRequestDto pwDto, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
        if(!user.getUsername().equals(username)){
            throw new IllegalArgumentException("본인 비밀번호만 수정할 수 있습니다");
        }
        if(user.getRole()==UserRole.KAKAO_USER){
            throw new IllegalArgumentException("카카오 유저는 닉네임만 수정할 수 없습니다");
        }
        if(!passwordEncoder.matches(pwDto.getCurrentPw(), user.getPassword())){
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if(!pwDto.getNewPw().equals(pwDto.getCurrentPw())){
            throw new IllegalArgumentException("현재 비밀번호와 같은 비밀번호로 수정할 수 없습니다");
        }
        if(!pwDto.getNewPw().equals(pwDto.getConfirmPw())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        String encodePw = passwordEncoder.encode(pwDto.getNewPw());
        user.updatePassword(encodePw);
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
    public void deleteToggle(UserStatusRequestDto dto, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
        if(user.getRole()==UserRole.SUPER_ADMIN)
            throw new IllegalArgumentException("슈퍼 관리자는 계정을 삭제할 수 없습니다");
        user.updateStatus(dto.getStatus());
    }

    @Transactional
    public UserStatusResponseDto userStatus(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다"));
        return new UserStatusResponseDto(user);
    }

//    public UserResponseDto login(UserLoginRequestDto dto){
//        User user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다"));
//        if(!user.getPassword().equals(dto.getPassword())){
//        if(passwordEncoder.matches(dto.getPassword(), user.getPassword())){
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
//        }
//        return UserResponseDto.from(user);
//    }
}
