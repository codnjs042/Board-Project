package com.example.demo.domain.post.dto;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import lombok.Getter;
import java.util.List;

@Getter
public class PostDetailDto {
    private PostResponseDto postDto;
    private Boolean isLiked;
    private List<CommentResponseDto> commentDto;

    public PostDetailDto(PostResponseDto postDto, Boolean isLiked, List<CommentResponseDto> commentDto){
        this.postDto=postDto;
        this.isLiked=isLiked;
        this.commentDto=commentDto;
    }

    public static PostDetailDto from(PostResponseDto postDto, Boolean isLiked, List<CommentResponseDto> commentDto){
        return new PostDetailDto(postDto, isLiked, commentDto);
    }
}
