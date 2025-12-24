package com.example.demo.domain.like.service;

import com.example.demo.domain.like.domain.Like;
import com.example.demo.domain.like.domain.LikeStatus;
import com.example.demo.domain.like.repository.LikeRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public void toggleLike(Long postId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(user.getStatus() == UserStatus.DISABLED)
            throw new IllegalArgumentException("탈퇴한 회원은 서비스를 이용할 수 없습니다.");

        if (post.getStatus()== PostStatus.DISABLED)
            throw new IllegalArgumentException("삭제된 게시글입니다.");

        if (post.getAuthor().getId().equals(user.getId()))
            throw new IllegalArgumentException("본인이 작성한 게시글은 좋아요를 누를 수 없습니다.");

        Optional<Like> existing = likeRepository.findByUserIdAndPostId(userId, postId);

        if(existing.isPresent()){
            Like like = existing.get();
            like.updateStatus(LikeStatus.DISABLED);
            post.updatelikeCount(-1L);
        }
        else{
            Like newLike = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(newLike);
            post.updatelikeCount(1L);
        }
    }
}
