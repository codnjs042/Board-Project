package com.example.demo.domain.like.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name="likes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Like extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @ManyToOne
    @JoinColumn(name="postId")
    private Post post;

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
