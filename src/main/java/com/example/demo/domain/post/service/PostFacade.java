package com.example.demo.domain.post.service;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.service.CommentService;
import com.example.demo.domain.like.service.LikeService;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.dto.PostDetailResponseDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import com.example.demo.global.security.CustomUserDetails;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.tool.schema.TargetType;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
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

    public PostDetailResponseDto getPostDetail(Long postId, CustomUserDetails userDetails){
        Post post = postService.findById(postId);

        if(!post.isPublished() || !post.isActive())
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);

        List<CommentResponseDto> comment = commentService.getRootComments(post.getId());

        Boolean isLiked = (userDetails!=null) && likeService.isLiked(userDetails.getId(), post.getId());

        return new PostDetailResponseDto(PostResponseDto.from(post), isLiked, comment);
    }
}

