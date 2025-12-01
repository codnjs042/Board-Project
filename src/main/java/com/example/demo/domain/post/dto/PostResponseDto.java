package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private User author;
    private String authorName;
    private List<User> likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PostState state;
    private PostType type;

    public PostResponseDto(Post post){
        this.id=post.getId();
        this.title=post.getTitle();
        this.content=post.getContent();
        this.author=post.getAuthor();
        this.authorName=post.getAuthorName();
        this.likes=post.getLikes();
        this.createdAt=post.getCreatedAt();
        this.updatedAt=post.getUpdatedAt();
        this.state=post.getState();
        this.type=post.getType();
    }

    public static PostResponseDto from(Post post){
        return new PostResponseDto(post);
    }
}
