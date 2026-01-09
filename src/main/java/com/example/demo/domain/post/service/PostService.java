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
        if(dto.getState()==PostState.PUBLISHED)
            post.upload();
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

    public Page<PostResponseDto> searchPosts(int page, String type, String keyword){
        Pageable pageable = PageRequest.of(page, pageSize,
                Sort.by(Sort.Order.desc("type"), Sort.Order.desc("publishedAt")));

        return postRepository.searchPosts(PostState.PUBLISHED, PostStatus.ACTIVE, type, keyword, pageable)
                .map(PostResponseDto::from);
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

    public Page<PostResponseDto> getUserPosts(Long userId, PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(state==PostState.PUBLISHED ? "publishedAt" : "updatedAt").descending());
        return postRepository.findAllByUserId(userId, state, PostStatus.ACTIVE, pageable)
                .map(PostResponseDto::from);
    }

    public Page<PostAdminResponseDto> searchPostsForAdmin(PostState state, PostStatus status, int page, String type, String keyword){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.searchPostsForAdmin(state, status, type, keyword, pageable)
                .map(PostAdminResponseDto::from);
    }
}
