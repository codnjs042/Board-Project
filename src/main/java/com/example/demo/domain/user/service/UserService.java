package com.example.demo.domain.user.service;

import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.repository.PostRepository;
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
import org.springframework.data.domain.*;
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
    private final PostRepository postRepository;

    @Transactional
    public void signup(UserSignupRequestDto dto){
        Optional<User> existing = findByUsername(dto.getUsername());
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

    @Transactional
    public User saveKakaoUser(String username, String nickname){
        User user = findByUsername(username)
                .map(u -> {
                    u.updateStatusForce(UserStatus.ACTIVE);
                    return u;
                })
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(username)
                            .nickname(nickname)
                            .role(UserRole.KAKAO_USER)
                            .build();
                    return userRepository.save(newUser);
                });
        return userRepository.save(user);
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

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
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
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<Long> ids = userRepository.searchUsersForAdmin(role, status, keyword, pageable);
        if(ids.isEmpty())
            return Page.empty(pageable);

        List<User> users = userRepository.findAllByIdIn(ids, sort);
        long total = userRepository.countSearchUsersForAdmin(role, status, keyword);

        return new PageImpl<>(users, pageable, total).map(UserAdminResponseDto::from);
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