package com.example.demo.domain.admin.dto;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostAdminResponseDto {
    private Long id;
    private String title;
    private String content;
    private User author;
    private String authorName;
    private int view;
    private int commentCount;
    private int likeCount;
    private PostState state;
    private PostType type;
    private PostStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public PostAdminResponseDto(Post post){
        this.id=post.getId();
        this.title=post.getTitle();
        this.content=post.getContent();
        this.author=post.getAuthor();
        this.authorName=post.getAuthorName();
        this.view=post.getView();
        this.commentCount=post.getCommentCount();
        this.likeCount=post.getLikeCount();
        this.state=post.getState();
        this.type=post.getType();
        this.status=post.getStatus();
        this.createdAt=post.getCreatedAt();
        this.publishedAt=post.getPublishedAt();
        this.updatedAt=post.getUpdatedAt();
    }

    public static PostAdminResponseDto from(Post post){
        return new PostAdminResponseDto(post);
    }
}
