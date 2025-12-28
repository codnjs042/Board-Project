package com.example.demo.domain.like.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="postId")
    private Post post;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime likeAt;

    @Enumerated(EnumType.STRING)
    private LikeStatus status;

    @Builder
    public Like(User user, Post post, LikeStatus status){
        this.user=user;
        this.post=post;
        this.status=(status==null)?LikeStatus.ACTIVE:status;
    }

    public void updateStatus(LikeStatus status){
        this.status = status;
    }
}
