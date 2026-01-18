package com.example.demo.domain.post.domain;

import com.example.demo.domain.user.domain.User;
import com.example.demo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30)
    private String title;

    @Lob
    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private int view;

    @Column(nullable = false)
    private int likeCount;

    @Column(nullable = false)
    private int commentCount;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Enumerated(EnumType.STRING)
    private PostState state;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column
    private LocalDateTime publishedAt;

    @Builder
    public Post(String title, String content, User author, String authorName, PostType type, PostState state, PostStatus status){
        this.title=title;
        this.content=content;
        this.author=author;
        this.authorName=authorName;
        this.view=0;
        this.likeCount=0;
        this.commentCount=0;
        this.type=type;
        this.state=state;
        this.status=(status==null)?PostStatus.ACTIVE:status;
        this.publishedAt=(state==PostState.PUBLISHED)?LocalDateTime.now():null;
    }

    public void modify(String title, String content, PostType type){
        this.title=title;
        this.content=content;
        this.type=type;
    }

    public boolean isAuthor(Long userId){
        return this.author.getId().equals(userId);
    }

    public boolean isPublished(){
        return state == PostState.PUBLISHED;
    }

    public boolean isActive(){
        return status == PostStatus.ACTIVE;
    }
    public void updateLikeCount(int amount){
        likeCount+=1;
    }

    public void updateCommentCount(int amount) {commentCount+=1;}

    public void updateStatus(PostStatus status){
        this.status=status;
    }
}

