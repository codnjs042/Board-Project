package com.example.demo.domain.user.service;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.dto.UserInfoRequestDto;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade {
    private final UserService userService;
    private final PostService postService;
    private final SecurityUtil securityUtil;

    @Transactional
    public void updateNickname(UserInfoRequestDto dto, Long userId){
        User user = userService.findById(userId);

        if(user.isSocialUser())
            throw new BusinessException(ErrorCode.POLICY_VIOLATION);

        user.updateNickname(dto.getNickname());

        securityUtil.updateSecurityContext(user);
    }

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        return postService.getUserPosts(userId, state, page);
    }
}
