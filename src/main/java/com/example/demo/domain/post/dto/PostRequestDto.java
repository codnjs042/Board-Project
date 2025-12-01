package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostType;
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
