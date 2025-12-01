package com.example.demo.domain.post.repository;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByIdAndLikes(Long id, User user);

    // 공개된 모든 유저 목록
    Page<Post> findByStatusAndStateOrderByTypeDesc(PostStatus status, PostState state, Pageable pageable);

    // 공개된 모든유저 검색
    Page<Post> findByStatusAndStateAndTitleContaining(
            PostStatus status, PostState state, String keyword, Pageable pageable
    );
    Page<Post> findByStatusAndStateAndContentContaining(
            PostStatus status, PostState state, String keyword, Pageable pageable
    );
    Page<Post> findByStatusAndStateAndAuthorNameContaining(
            PostStatus status, PostState state, String keyword, Pageable pageable
    );

    // 공개/비공개된 단독 유저 목록
    Page<Post> findByStatusAndAuthor_UsernameAndState(PostStatus status, String username, PostState state, Pageable pageable);

    // 공개된 단독 유저 검색
    Page<Post> findByStatusAndAuthor_UsernameAndStateAndTitleContaining(
            PostStatus status, String username, PostState state, String keyword, Pageable pageable
    );
    Page<Post> findByStatusAndAuthor_UsernameAndStateAndContentContaining(
            PostStatus status, String username, PostState state, String keyword, Pageable pageable
    );

    // 임시 저장
    List<Post> findByStatusAndAuthor_UsernameAndStateOrderByUpdatedAtDesc(
            PostStatus status, String username, PostState state
    );

    Page<Post> findByAuthor_UsernameContainingOrAuthorNameContaining(String username, String authorName, Pageable pageable);

    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
