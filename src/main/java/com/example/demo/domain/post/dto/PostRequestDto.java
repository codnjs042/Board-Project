package com.example.demo.domain.post.dto;

import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    @NotBlank(message="제목을 입력하세요.")
    @Size(min=1, max=30)
    private String title;

    @NotBlank(message="내용을 입력하세요.")
    @Size(min=1)
    private String content;

    @NotNull(message="게시글 유형을 지정해주세요.")
    private PostState state;

    @NotNull(message="공지글 여부를 선택해주세요.")
    private PostType type;
}
