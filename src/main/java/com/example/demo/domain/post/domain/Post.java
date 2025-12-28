package com.example.demo.domain.post.domain;

import com.example.demo.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name="posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String authorName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private Long view;

    @Column(nullable = false)
    private Long likeCount;

    @Column(nullable = false)
    private Long commentCount;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Enumerated(EnumType.STRING)
    private PostState state;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Builder
    public Post(String title, String content, User author, String authorName, PostType type, PostState state, PostStatus status){
        this.title=title;
        this.content=content;
        this.author=author;
        this.authorName=authorName;
        this.view=0L;
        this.likeCount=0L;
        this.commentCount=0L;
        this.type=type;
        this.state=state;
        this.status=(status==null)?PostStatus.ACTIVE:status;
    }

    public void modify(String title, String content, PostType type){
        this.title=title;
        this.content=content;
        this.type=type;
        if(state == PostState.PUBLISHED)
            updatedAt = LocalDateTime.now();
    }

    public void updateLikeCount(Long amount){
        likeCount+=1;
    }

    public void updateCommentCount(Long amount) {commentCount+=1;}

    public void updateStatus(PostStatus status){
        this.status=status;
    }
}

