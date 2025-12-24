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
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private LikeStatus status = LikeStatus.ACTIVE;

    public void updateStatus(LikeStatus status){
        this.status = status;
    }
}
