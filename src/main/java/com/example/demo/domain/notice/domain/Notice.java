package com.example.demo.domain.notice.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name="notices")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseEntity {
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

    @Enumerated(EnumType.STRING)
    private NoticeState state;

    @Enumerated(EnumType.STRING)
    private NoticeStatus status;

    @Builder
    public Notice(User sendUser, User receiveUser, Post post, NoticeState state, NoticeStatus status){
        this.sendUser=sendUser;
        this.receiveUser=receiveUser;
        this.post=post;
        this.state=(state==null)?NoticeState.UNCHECK:state;
        this.status=(status==null)?NoticeStatus.ACTIVE:status;
    }
}
