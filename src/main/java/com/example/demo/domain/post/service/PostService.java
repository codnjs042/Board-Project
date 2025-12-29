package com.example.demo.domain.post.service;

import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private static final int pageSize = 10;

    @Transactional
    public void save(PostRequestDto dto, User user){
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .authorName(user.getNickname())
                .type(dto.getType())
                .state(dto.getState())
                .build();

        postRepository.save(post);
    }

    public Post getPostId(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
    }

    public List<Post> getPostsIds(List<Long> postId){
        return postRepository.findAllById(postId);
    }

    public Page<PostResponseDto> getPosts(int page, String type, String keyword){
        Pageable pageable = PageRequest.of(page, pageSize,
                Sort.by(Sort.Order.desc("type"), Sort.Order.desc("publishedAt")));

        return postRepository.findByPosts(PostState.PUBLISHED, PostStatus.ACTIVE, type, keyword, pageable)
                .map(PostResponseDto::from);
    }

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("publishedAt").descending());
        return postRepository.findByUserPosts(userId, state, PostStatus.ACTIVE, pageable)
                .map(PostResponseDto::from);
    }

    public Page<PostAdminResponseDto> getAdminPosts(PostState state, PostStatus status, int page, String type, String keyword){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.findByAdminPosts(state, status, type, keyword, pageable)
                .map(PostAdminResponseDto::from);
    }
}
