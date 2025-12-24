package com.example.demo.domain.like.repository;

import com.example.demo.domain.like.domain.Like;
import com.example.demo.domain.like.domain.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);

    Boolean existsByUserIdAndPostIdAndStatus(Long userId, Long postId, LikeStatus status);
}
