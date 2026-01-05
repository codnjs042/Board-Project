package com.example.demo.domain.comment.repository;

import com.example.demo.domain.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c " +
            "where c.post.id=:postId " +
            "and c.parent is null")
    List<Comment> findAllByRootComments(@Param("postId") Long postId);
}
