package com.example.demo.domain.like.service;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeFacade {
    private final UserService userService;
    private final PostService postService;
    private final LikeService likeService;

    @Transactional
    public void toggleLike(Long postId, Long userId){
        User user = userService.getUserId(userId);

        Post post = postService.getPostId(postId);

        if(user.getStatus() == UserStatus.DISABLED)
            throw new IllegalArgumentException("탈퇴한 회원은 서비스를 이용할 수 없습니다.");

        if (post.getStatus()== PostStatus.DISABLED)
            throw new IllegalArgumentException("삭제된 게시글입니다.");

        if (post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("본인이 작성한 게시글은 좋아요를 누를 수 없습니다.");

        likeService.updateLike(user, post);
    }
}
