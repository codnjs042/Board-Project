package com.example.demo.domain.comment.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(nullable = false)
    private String authorName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> child;

    @CreationTimestamp
    private LocalDateTime CreatedAt;

    private LocalDateTime UpdatedAt;

    @Enumerated(EnumType.STRING)
    private CommentStatus status = CommentStatus.ACTIVE;

    @Builder
    public Comment(String comment, User author, String authorName, Post post, CommentStatus status){
        this.comment=comment;
        this.author=author;
        this.authorName=authorName;
        this.post=post;
        this.status=(status==null)?CommentStatus.ACTIVE:status;
    }

    public void modify(String comment){
        this.comment=comment;
        this.UpdatedAt=LocalDateTime.now();
    }

    public void updateParent(Comment parent){
        this.parent=parent;
    }

    public void updateChild(Comment child){
        this.child.add(child);
    }

    public void updateStatus(CommentStatus status){this.status=status;}
}
