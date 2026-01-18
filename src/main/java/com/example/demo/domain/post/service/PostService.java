package com.example.demo.domain.post.service;

import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
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
    public void create(Long postId, PostRequestDto dto, User user){
        Post post;
        if(postId==null) {
            post = Post.builder()
                    .title(dto.getTitle())
                    .content(dto.getContent())
                    .author(user)
                    .authorName(user.getNickname())
                    .type(dto.getType())
                    .state(dto.getState())
                    .build();

            postRepository.save(post);
        }else {
            post = findById(postId);
            post.modify(dto.getTitle(), dto.getContent(), dto.getType());
        }
    }

    public Post findById(Long postId){
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
    }

    public List<Post> findAllById(List<Long> postId){
        return postRepository.findAllById(postId);
    }

    @Transactional
    public void incrementView(Long postId){
        postRepository.updateView(postId);
    }

    public PostResponseDto getPostForEdit(Long postId, User user){
        Post post = findById(postId);

        if(!post.isAuthor(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        return PostResponseDto.from(post);
    }

    @Transactional
    public void modify(Long postId, PostRequestDto dto, User user){
        Post post = findById(postId);

        if(!post.isAuthor(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        if (!user.isAdmin() && dto.getType().equals(PostType.NOTICE))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        post.modify(dto.getTitle(), dto.getContent(), dto.getType());
    }

    @Transactional
    public void delete(Long postId, User user){
        Post post = findById(postId);

        if(!post.isAuthor(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        post.updateStatus(PostStatus.DISABLED);
    }

    public Page<PostResponseDto> searchPosts(int page, String type, String keyword){
        Sort sort = Sort.by(Sort.Order.desc("type"), Sort.Order.desc("publishedAt"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<Long> ids = postRepository.searchPosts(PostState.PUBLISHED, PostStatus.ACTIVE, type, keyword, pageable);
        if(ids.isEmpty())
            return Page.empty(pageable);

        List<Post> posts = postRepository.findAllByIdIn(ids, sort);
        long total = postRepository.countSearchPosts(PostState.PUBLISHED, PostStatus.ACTIVE, type, keyword);

        return new PageImpl<>(posts, pageable, total).map(PostResponseDto::from);
    }

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        Sort sort = Sort.by(Sort.Order.desc(state==PostState.PUBLISHED ? "publishedAt" : "updatedAt"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<Long> ids = postRepository.findAllByUserId(userId, state, PostStatus.ACTIVE, pageable);
        if(ids.isEmpty())
            return Page.empty(pageable);

        List<Post> posts = postRepository.findAllByIdIn(ids, sort);
        long total = postRepository.countByAuthorId(userId, state, PostStatus.ACTIVE);

        return new PageImpl<>(posts, pageable, total).map(PostResponseDto::from);
    }

    public Page<PostAdminResponseDto> searchPostsForAdmin(PostState state, PostStatus status, int page, String type, String keyword){
        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        List<Long> ids = postRepository.searchPostsForAdmin(state, status, type, keyword, pageable);
        if(ids.isEmpty())
            return Page.empty(pageable);

        List<Post> posts = postRepository.findAllByIdIn(ids, sort);
        long total = postRepository.countSearchPostsForAdmin(state, status, type, keyword);

        return new PageImpl<>(posts, pageable, total).map(PostAdminResponseDto::from);
    }
}