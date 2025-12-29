package com.example.demo.domain.like.dto;

import com.example.demo.domain.like.domain.Like;
import com.example.demo.domain.like.domain.LikeStatus;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class LikeResponseDto {
    private User user;
    private Post post;
    private LikeStatus status;
    private LocalDateTime createdAt;


    public LikeResponseDto(Like like){
        this.user=like.getUser();
        this.post=like.getPost();
        this.status=like.getStatus();
        this.createdAt=like.getCreatedAt();
    }

    public static LikeResponseDto from(Like like) { return new LikeResponseDto(like);}
}
