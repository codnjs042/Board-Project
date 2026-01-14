package com.example.demo.domain.post.repository;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying(clearAutomatically = true)
    @Query("update Post p " +
            "set p.view = p.view + 1" +
            "where p.id = :id")
    void updateView(Long id);

    // 사용자 게시글 조회
    @Query("select p from Post p " +
            "where p.state=:state " +
            "and p.status=:status " +
            "and (:keyword is null or :keyword='' or " +
                "(:type='title' and p.title like %:keyword%) or" +
                "(:type='content' and p.content like %:keyword%) or " +
                "(:type='author' and p.author.nickname like %:keyword%))")
    Page<Post> searchPosts(@Param("state") PostState state,
                           @Param("status") PostStatus status,
                           @Param("type") String type,
                           @Param("keyword") String keyword,
                           Pageable pageable);

    // 특정 사용자 게시글 조회
    @Query("select p from Post p " +
            "where p.author.id=:userId " +
            "and p.state=:state " +
            "and p.status=:status")
    Page<Post> findAllByUserId(@Param("userId") Long userId,
                               @Param("state") PostState state,
                               @Param("status") PostStatus status,
                               Pageable pageable);

    // 관리자용 사용자 게시글 조회
    @Query("select p from Post p " +
            "where p.state=:state " +
            "and (:status is null or p.status=:status) " +
            "and (:keyword is null or :keyword='' or " +
                "(:type='title' and p.title like %:keyword%) or" +
                "(:type='content' and p.content like %:keyword%) or " +
                "(:type='id' and p.author.username like %:keyword%))")
    Page<Post> searchPostsForAdmin(@Param("state") PostState state,
                                @Param("status") PostStatus status,
                                @Param("type") String type,
                                @Param("keyword") String keyword,
                                Pageable pageable);
}
