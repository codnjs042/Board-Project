package com.example.demo.domain.like.service;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeFacade {
    private final PostService postService;
    private final LikeService likeService;

    @Transactional
    public void likeToggle(Long postId, User user){
        Post post = postService.findById(postId);

        if (post.getStatus()== PostStatus.DISABLED)
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);

        if (post.getAuthor().getId().equals(user.getId()))
            throw new BusinessException(ErrorCode.POLICY_VIOLATION);

        likeService.updateLike(user, post);
    }
}
