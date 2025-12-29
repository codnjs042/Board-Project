package com.example.demo.domain.post.service;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    private final PostRepository postRepository;
    private static final int pageSize = 10;

    public Post getPostId(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("publishedAt").descending());
        return postRepository.findByUserPosts(userId, state, PostStatus.ACTIVE, pageable)
                .map(PostResponseDto::from);
    }
}
