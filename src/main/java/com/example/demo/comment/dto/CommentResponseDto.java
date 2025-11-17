package com.example.demo.comment.dto;

import com.example.demo.comment.domain.Comment;
import com.example.demo.post.domain.Post;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.user.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    }

    public static CommentResponseDto from(Comment comment){
        return new CommentResponseDto(comment);
    }
}
