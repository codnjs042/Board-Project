package com.example.demo.domain.comment.domain;

import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name="comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
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

    @Enumerated(EnumType.STRING)
    private CommentStatus status;

    @Builder
    public Comment(String comment, User author, String authorName, Post post, CommentStatus status){
        this.comment=comment;
        this.author=author;
        this.authorName=authorName;
        this.post=post;
        this.status=(status==null)?CommentStatus.ACTIVE:status;
    }

    public boolean isAuthor(Long userId){
        return this.author.getId().equals(userId);
    }

    public void modify(String comment){
        this.comment=comment;
    }

    public void updateParent(Comment parent){
        this.parent=parent;
    }

    public void updateChild(Comment child){
        this.child.add(child);
    }

    public void updateStatus(CommentStatus status){this.status=status;}
}
