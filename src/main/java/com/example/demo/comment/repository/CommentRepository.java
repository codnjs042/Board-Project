package com.example.demo.comment.repository;

import com.example.demo.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost_IdAndParentIsNull(Long id);
}
