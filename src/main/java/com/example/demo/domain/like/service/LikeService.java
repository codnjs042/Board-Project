package com.example.demo.domain.like.service;

import com.example.demo.domain.like.domain.Like;
import com.example.demo.domain.like.domain.LikeStatus;
import com.example.demo.domain.like.repository.LikeRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeService {
    private final LikeRepository likeRepository;

    public boolean isLiked(Long userId, Long postId){
        return likeRepository.existsByUserIdAndPostIdAndStatus(userId, postId, LikeStatus.ACTIVE);
    }

    @Transactional
    public void updateLike(User user, Post post){
        Optional<Like> existing = likeRepository.findByUserIdAndPostId(user.getId(), post.getId());

        if(existing.isPresent()){
            Like like = existing.get();
            if(like.getStatus()== LikeStatus.ACTIVE) {
                like.updateStatus(LikeStatus.DISABLED);
                post.updateLikeCount(-1);
            }
            else{
                like.updateStatus(LikeStatus.ACTIVE);
                post.updateLikeCount(1);
            }
        }
        else{
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(newLike);
            post.updateLikeCount(1);
        }
    }
}
