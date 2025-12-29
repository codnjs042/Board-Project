package com.example.demo.domain.user.service;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacade {
    private final PostService postService;

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        return postService.getUserPosts(userId, state, page);
    }
}
