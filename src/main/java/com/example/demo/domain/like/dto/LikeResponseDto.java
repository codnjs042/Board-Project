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
    private LocalDateTime likeAt;
    private LikeStatus status;

    public LikeResponseDto(Like like){
        this.user=like.getUser();
        this.post=like.getPost();
        this.likeAt=like.getLikeAt();
        this.status=like.getStatus();
    }

    public static LikeResponseDto from(Like like) { return new LikeResponseDto(like);}
}
