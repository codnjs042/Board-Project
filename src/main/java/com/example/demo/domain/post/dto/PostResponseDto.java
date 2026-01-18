package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private User author;
    private String authorName;
    private int view;
    private int likeCount;
    private int commentCount;
    private PostState state;
    private PostType type;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public PostResponseDto(Post post){
        this.id=post.getId();
        this.title=post.getTitle();
        this.content=post.getContent();
        this.author=post.getAuthor();
        this.authorName=post.getAuthorName();
        this.view=post.getView();
        this.likeCount=post.getLikeCount();
        this.commentCount=post.getCommentCount();
        this.state=post.getState();
        this.type=post.getType();
        this.publishedAt=post.getPublishedAt();
        this.updatedAt=post.getUpdatedAt();
    }

    public static PostResponseDto from(Post post){
        return new PostResponseDto(post);
    }
}
