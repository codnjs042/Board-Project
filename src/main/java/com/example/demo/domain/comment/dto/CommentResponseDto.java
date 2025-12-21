package com.example.demo.domain.comment.dto;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.domain.CommentStatus;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private User author;
    private String authorName;
    private Post post;
    private Comment parent;
    private List<Comment> child;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommentStatus status;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.comment = comment.getComment();
        this.author = comment.getAuthor();
        this.authorName = comment.getAuthorName();
        this.post = comment.getPost();
        this.parent = comment.getParent();
        this.child = comment.getChild();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.status = comment.getStatus();
    }

    public static CommentResponseDto from(Comment comment){
        return new CommentResponseDto(comment);
    }
}
