package com.example.demo.domain.user.service;

import com.example.demo.domain.admin.dto.UserAdminRequestDto;
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
                .build();
        userRepository.save(user);
    }

    public UserResponseDto userInfo(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

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

    public UserStatusResponseDto userStatus(CustomUserDetails userDetails){
        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        return new UserStatusResponseDto(user);
    }

    public User getUserId(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));
    }

    public List<User> getUsersIds(List<Long> userId){
        return userRepository.findAllById(userId);
    }

    public Page<UserAdminResponseDto> getAdminUsers(UserRole role, UserStatus status, String keyword, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return userRepository.findByAdminUsers(role, status, keyword, pageable)
                .map(UserAdminResponseDto::from);
    }

    @Transactional
    public void deleteUsers(UserAdminRequestDto dto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지 않습니다"));

        if(user.getRole()!=UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new IllegalArgumentException("계정 삭제 권한이 없습니다");

        if(dto.getId()==null)
            throw new IllegalArgumentException("삭제할 항목을 먼저 선택하세요");

        List<User> users = userRepository.findAllById(dto.getId());

        for (User u : users) {
            if(u.getRole()==UserRole.SUPER_ADMIN)
                throw new IllegalArgumentException("슈퍼 관리자 계정은 삭제할 수 없습니다");
            if(user.getRole()==UserRole.ADMIN
                    && u.getUsername()!=user.getUsername()
                    && u.getRole()==UserRole.ADMIN)
                throw new IllegalArgumentException("일반 관리자는 다른 관리자 계정을 삭제할 수 없습니다");
            u.updateStatus(UserStatus.DISABLED);
        }
    }
}