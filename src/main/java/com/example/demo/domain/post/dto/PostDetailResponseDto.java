package com.example.demo.domain.post.dto;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import lombok.Getter;
import java.util.List;

@Getter
public class PostDetailResponseDto {
    private PostResponseDto postDto;
    private Boolean isLiked;
    private List<CommentResponseDto> commentDto;

    public PostDetailResponseDto(PostResponseDto postDto, Boolean isLiked, List<CommentResponseDto> commentDto){
        this.postDto=postDto;
        this.isLiked=isLiked;
        this.commentDto=commentDto;
    }

    public static PostDetailResponseDto from(PostResponseDto postDto, Boolean isLiked, List<CommentResponseDto> commentDto){
        return new PostDetailResponseDto(postDto, isLiked, commentDto);
    }
}
