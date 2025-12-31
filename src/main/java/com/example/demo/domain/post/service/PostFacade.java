package com.example.demo.domain.post.service;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.service.CommentService;
import com.example.demo.domain.like.service.LikeService;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.dto.PostDetailDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostFacade {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    public PostDetailDto getPostDetail(Long postId, CustomUserDetails userDetails){
        Post post = postService.findById(postId);

        if(post.getStatus().equals(PostStatus.DISABLED))
            throw new IllegalArgumentException("현재 삭제된 게시글입니다.");

        List<CommentResponseDto> comment = commentService.getRootComments(post.getId());

        Boolean isLiked = (userDetails!=null) && likeService.isLiked(userDetails.getId(), post.getId());

        return new PostDetailDto(PostResponseDto.from(post), isLiked, comment);
    }
}
