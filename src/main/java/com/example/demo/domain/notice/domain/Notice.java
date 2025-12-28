package com.example.demo.domain.notice.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name="notices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="sendUserId")
    private User sendUser;

    @ManyToOne
    @JoinColumn(name="receiveUserId")
    private User receiveUser;

    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NoticeState state = NoticeState.UNCHECK;

    @Enumerated(EnumType.STRING)
    private NoticeStatus status = NoticeStatus.ACTIVE;

    @Builder
    public Notice(User sendUser, User receiveUser, Post post, NoticeState state, NoticeStatus status){
        this.sendUser=sendUser;
        this.receiveUser=receiveUser;
        this.post=post;
        this.state=(state==null)?NoticeState.UNCHECK:state;
        this.status=(status==null)?NoticeStatus.ACTIVE:status;
    }
}
