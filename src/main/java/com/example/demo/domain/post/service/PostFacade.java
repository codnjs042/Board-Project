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

    @Transactional
    public PostDetailResponseDto getPostDetail(Long postId, CustomUserDetails userDetails, HttpServletRequest request, HttpServletResponse response){
        if(isFirstView(postId, request)){
            postService.incrementView(postId);
            bakeCookie(postId, request, response);
        }

        Post post = postService.findById(postId);

        if(!post.isPublished() || !post.isActive())
            throw new BusinessException(ErrorCode.POST_NOT_FOUND);

        List<CommentResponseDto> comment = commentService.getRootComments(post.getId());

        Boolean isLiked = (userDetails!=null) && likeService.isLiked(userDetails.getId(), post.getId());

        return new PostDetailResponseDto(PostResponseDto.from(post), isLiked, comment);
    }

    private boolean isFirstView(Long postId, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie c : cookies){
                if(c.getName().equals("postView")
                        && c.getValue().contains("["+postId+"]"))
                    return false;
            }
        }
        return true;
    }

    private void bakeCookie(Long postId, HttpServletRequest request, HttpServletResponse response){
        String cookieName = "postView";
        String newValue = "["+postId+"]";
        Cookie[] cookies = request.getCookies();
        Cookie targetCookie = null;

        if(cookies!=null){
            for(Cookie c : cookies){
                if(c.getName().equals(cookieName)){
                    targetCookie = c;
                    targetCookie.setValue(c.getValue()+"_["+postId+"]");
                    break;
                }
            }
        }
        if(targetCookie==null){
            targetCookie = new Cookie(cookieName, "["+postId+"]");
        }
        targetCookie.setPath("/");
        targetCookie.setMaxAge(60*60*24);
        response.addCookie(targetCookie);
    }
}

