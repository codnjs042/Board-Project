package com.example.demo.domain.post.service;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.service.CommentService;
import com.example.demo.domain.like.service.LikeService;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.dto.PostDetailDto;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.service.UserService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostFacade {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Transactional
    public void create(PostRequestDto dto, Long userId){
        User user = userService.getUserId(userId);

        postService.save(dto, user);
    }

    public PostDetailDto getPostDetail(Long postId, CustomUserDetails userDetails){
        Post post = postService.getPostId(postId);

        if(post.getStatus().equals(PostStatus.DISABLED))
            throw new IllegalArgumentException("현재 삭제된 게시글입니다.");

        List<CommentResponseDto> comment = commentService.getRootComments(post.getId());

        Boolean isLiked = (userDetails!=null) && likeService.isLiked(userDetails.getId(), post.getId());

        return new PostDetailDto(PostResponseDto.from(post), isLiked, comment);
    }

    public PostResponseDto getPostForEdit(Long postId, Long userId){
        User user = userService.getUserId(userId);

        Post post = postService.getPostId(postId);

        if(!post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 수정 권한이 없습니다.");

        return PostResponseDto.from(post);
    }

    @Transactional
    public void modify(Long postId, PostRequestDto dto, Long userId){
        User user = userService.getUserId(userId);

        Post post = postService.getPostId(postId);

        if(!post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 수정 권한이 없습니다.");

        if (!user.isAdmin() && dto.getType().equals(PostType.NOTICE))
            throw new IllegalArgumentException("공지사항 설정 권한이 없습니다.");

        post.modify(dto.getTitle(), dto.getContent(), dto.getType());
    }

    @Transactional
    public void delete(Long postId, Long userId){
        User user = userService.getUserId(userId);

        Post post = postService.getPostId(postId);

        if(post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 삭제 권한이 없습니다.");

        post.updateStatus(PostStatus.DISABLED);
    }
}
