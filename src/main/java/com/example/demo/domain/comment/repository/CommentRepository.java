package com.example.demo.domain.comment.repository;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.domain.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_IdAndStatusAndParentIsNull(Long postId, CommentStatus status);
}
