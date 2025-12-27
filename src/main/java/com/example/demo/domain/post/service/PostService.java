package com.example.demo.domain.post.service;

import com.example.demo.domain.comment.domain.CommentStatus;
import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.repository.CommentRepository;
import com.example.demo.domain.like.domain.LikeStatus;
import com.example.demo.domain.like.repository.LikeRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.dto.PostDetailDto;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.repository.UserRepository;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final int pageSize = 10;

    @Transactional
    public void create(PostRequestDto dto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .authorName(user.getNickname())
                .state(dto.getState())
                .type(dto.getType())
                .build();

        postRepository.save(post);
    }

    public Page<PostResponseDto> getPosts(int page, String type, String keyword){
        Pageable pageable = PageRequest.of(page, pageSize,
                Sort.by(Sort.Order.desc("type"), Sort.Order.desc("createdAt")));

        return postRepository.findPosts(PostState.PUBLISHED, PostStatus.ACTIVE, type, keyword, pageable)
                .map(PostResponseDto::from);
    }

    public PostDetailDto getPostDetail(Long postId, CustomUserDetails userDetails){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(post.getStatus().equals(PostStatus.DISABLED))
            throw new IllegalArgumentException("현재 삭제된 게시글입니다.");

        List<CommentResponseDto> comment = commentRepository.findAllByPost_IdAndStatusAndParentIsNull(post.getId(), CommentStatus.ACTIVE)
                .stream()
                .map(CommentResponseDto::from)
                .toList();

        Boolean isLiked = false;
        if(userDetails!=null)
            isLiked = likeRepository.existsByUserIdAndPostIdAndStatus(userDetails.getId(), post.getId(), LikeStatus.ACTIVE);

        return new PostDetailDto(PostResponseDto.from(post), isLiked, comment);
    }

    public PostResponseDto getPostForEdit(Long postId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(!post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 수정 권한이 없습니다.");

        return PostResponseDto.from(post);
    }

    @Transactional
    public void modify(Long postId, PostRequestDto dto, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(!post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 수정 권한이 없습니다.");

        if (!user.isAdmin() && dto.getType().equals(PostType.NOTICE))
            throw new IllegalArgumentException("공지사항 설정 권한이 없습니다.");

        post.modify(dto.getTitle(), dto.getContent(), dto.getType());
    }

    @Transactional
    public void delete(Long postId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("인증된 사용자 정보를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("해당 게시글의 삭제 권한이 없습니다.");

        post.updateStatus(PostStatus.DISABLED);
    }

    public Page<PostResponseDto> findAllMyPost(String username, PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        if(state!=PostState.PUBLISHED){
            throw new IllegalStateException("정상 업로드된 게시글만 열람 가능합니다");
        }
        return postRepository.findByStatusAndAuthor_UsernameAndState(PostStatus.ACTIVE, username, state, pageable).map(PostResponseDto::from);
    }

    public List<PostResponseDto> findDraft(CustomUserDetails userDetails, PostState state){
        return postRepository.findByStatusAndAuthor_UsernameAndStateOrderByUpdatedAtDesc(PostStatus.ACTIVE, userDetails.getUsername(), state)
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    public Page<PostResponseDto> searchMyPost(String username, PostState state, String keyword, String type, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        if(state!=PostState.PUBLISHED){
            throw new IllegalStateException("정상 업로드된 게시글만 열람 가능합니다");
        }

        Page<Post> result = switch (type) {
            case "title" -> postRepository.findByStatusAndAuthor_UsernameAndStateAndTitleContaining(PostStatus.ACTIVE, username, state, keyword, pageable);
            case "content" -> postRepository.findByStatusAndAuthor_UsernameAndStateAndContentContaining(PostStatus.ACTIVE, username, state, keyword, pageable);
            default -> Page.empty(pageable);
        };

        return result.map(PostResponseDto::from);
    }
}
