package com.example.demo.domain.post.domain;

import com.example.demo.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String authorName;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column()
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int view;

    @ManyToMany
    private List<User> likes;

    @Enumerated(EnumType.STRING)
    private PostState state;

    @Enumerated(EnumType.STRING)
    private PostType type;

    @Enumerated(EnumType.STRING)
    private PostStatus status;

    public void update(String title, String content, PostType type){
        this.title=title;
        this.content=content;
        this.type=type;
    }

    public void updateState(PostState state){
        this.updatedAt=LocalDateTime.now();
        this.state=state;
    }

    public void updateStatus(PostStatus status){
        this.status=status;
    }

    public void toggleLike(User user){
        if(this.getLikes().contains(user))
            this.likes.remove(user);
        else this.likes.add(user);
    }
}

