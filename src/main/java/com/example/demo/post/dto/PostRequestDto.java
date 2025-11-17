package com.example.demo.post.dto;

import com.example.demo.post.domain.PostState;
import com.example.demo.post.domain.PostType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private String title;
    private String content;
    private PostState state;
    private PostType type;
}
