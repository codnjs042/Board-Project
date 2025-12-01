package com.example.demo.domain.post.service;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.repository.UserRepository;
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
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final int pageSize = 10;

    @Transactional
    public void create(PostRequestDto dto, String username){
        User author = userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(author)
                .authorName(author.getNickname())
                .view(0)
                .state(dto.getState())
                .type(dto.getType())
                .status(PostStatus.ACTIVE)
                .build();
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public boolean myLike(Long id, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));
        return postRepository.findByIdAndLikes(id, user).isPresent();
    }

    @Transactional
    public void likeToggle(Long id, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        if(post.getAuthor().getUsername().equals(username))
            throw new IllegalArgumentException("본인 게시글의 좋아요를 누를 수 없습니다");
        else post.toggleLike(user);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAll(PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.findByStatusAndStateOrderByTypeDesc(PostStatus.ACTIVE, state, pageable).map(PostResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> findAll(String username, PostState state, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.findByStatusAndAuthor_UsernameAndState(PostStatus.ACTIVE, username, state, pageable).map(PostResponseDto::from);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> findDraft(String username, PostState state){
        return postRepository.findByStatusAndAuthor_UsernameAndStateOrderByUpdatedAtDesc(PostStatus.ACTIVE, username, state)
                .stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPosts(PostState state, String keyword, String type, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        Page<Post> result = switch (type) {
            case "title" -> postRepository.findByStatusAndStateAndTitleContaining(PostStatus.ACTIVE, state, keyword, pageable);
            case "content" -> postRepository.findByStatusAndStateAndContentContaining(PostStatus.ACTIVE, state, keyword, pageable);
            case "author" -> postRepository.findByStatusAndStateAndAuthorNameContaining(PostStatus.ACTIVE, state, keyword, pageable);
            default -> Page.empty(pageable);
        };

        return result.map(PostResponseDto::from);
    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> searchPosts(String username, PostState state, String keyword, String type, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());

        Page<Post> result = switch (type) {
            case "title" -> postRepository.findByStatusAndAuthor_UsernameAndStateAndTitleContaining(PostStatus.ACTIVE, username, state, keyword, pageable);
            case "content" -> postRepository.findByStatusAndAuthor_UsernameAndStateAndContentContaining(PostStatus.ACTIVE, username, state, keyword, pageable);
            default -> Page.empty(pageable);
        };

        return result.map(PostResponseDto::from);
    }

    @Transactional(readOnly = true)
    public PostResponseDto findById(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));
        return new PostResponseDto(post);
    }

    @Transactional
    public void update(Long id, PostRequestDto dto, String username){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if(!post.getAuthor().getUsername().equals(username)){
            throw new IllegalStateException("작성자만 수정할 수 있습니다");
        }
        post.update(dto.getTitle(),dto.getContent(), dto.getType());
    }

    @Transactional
    public void upload(Long id, PostRequestDto dto, String username){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        if(!post.getAuthor().getUsername().equals(username)){
            throw new IllegalStateException("작성자만 공개 게시할 수 있습니다");
        }
        post.update(dto.getTitle(),dto.getContent(), dto.getType());
        if(dto.getState()== PostState.PUBLISHED) post.updateState(dto.getState());
    }

    @Transactional
    public void delete(Long id, String username){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다"));
        if(!post.getAuthor().getUsername().equals(username)){
            throw new IllegalStateException("작성자만 삭제할 수 있습니다");
        }
        post.updateStatus(PostStatus.DISABLED);
    }
}
