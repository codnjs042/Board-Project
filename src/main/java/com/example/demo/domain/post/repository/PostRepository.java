package com.example.demo.domain.post.repository;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 사용자 게시글 조회
    @Query("select p from Post p " +
            "where p.state=:state " +
            "and p.status=:status " +
            "and (:keyword is null or " +
                "(:type='title' and p.title like %:keyword%) or" +
                "(:type='content' and p.content like %:keyword%) or " +
                "(:type='author' and p.author.nickname like %:keyword%))")
    Page<Post> findPosts(@Param("state") PostState state,
                         @Param("status") PostStatus status,
                         @Param("type") String type,
                         @Param("keyword") String keyword,
                         Pageable pageable);

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
//    @Query("select p from posts " +
//            "where p.status = 'ACTIVE' and " +
//            "p.author.username = :username and" +
//            "p.postState = 'DRAFT' " +
//            "order by p.updatedAt desc")
//    List<Post> findByMyDraftPost(@Param("username") String username);
    List<Post> findByStatusAndAuthor_UsernameAndStateOrderByUpdatedAtDesc(
            PostStatus status, String username, PostState state
    );

    Page<Post> findByAuthor_UsernameContainingOrAuthorNameContaining(String username, String authorName, Pageable pageable);

    Page<Post> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
