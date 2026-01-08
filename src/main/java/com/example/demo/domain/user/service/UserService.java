package com.example.demo.domain.user.service;

import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.dto.*;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.USER_ALREADY_EXIST);
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
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(user.isSocialUser()){
            throw new BusinessException(ErrorCode.POLICY_VIOLATION);
        }

        if(!user.matchRawPw(passwordEncoder, dto.getRawPw())){
            throw new BusinessException(ErrorCode.MISMATCH);
        }
        if(dto.matchNewPw()){
            throw new BusinessException(ErrorCode.POLICY_VIOLATION);
        }
        if(!dto.matchConfirmPw()){
            throw new BusinessException(ErrorCode.MISMATCH);
        }

        String encodePw = passwordEncoder.encode(dto.getNewPw());
        user.updatePw(encodePw);
        throw new ForceLogoutException(ErrorCode.FORCE_LOGOUT);
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> findAllById(List<Long> userId){
        return userRepository.findAllById(userId);
    }

    public UserProfileResponseDto getUserProfile(Long userId){
        User user = findById(userId);
        return UserProfileResponseDto.from(user);
    }

    public Page<UserAdminResponseDto> searchUsersForAdmin(UserRole role, UserStatus status, String keyword, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return userRepository.searchUsersForAdmin(role, status, keyword, pageable)
                .map(UserAdminResponseDto::from);
    }

    @Transactional
    public void deleteToggle(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if(user.isSuperAdmin())
            throw new BusinessException(ErrorCode.POLICY_VIOLATION);

        user.toggleStatusForUser();
    }
}