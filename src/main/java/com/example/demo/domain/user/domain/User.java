package com.example.demo.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length=12)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, length=12)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime pendingAt;

    @Builder
    public User(String username, String password, String nickname, UserRole role, UserStatus status){
        this.username=username;
        this.password=password;
        this.nickname=nickname;
        this.role=(role==null)?UserRole.USER:role;
        this.status=(status==null)?UserStatus.ACTIVE:status;
    }

    public LocalDateTime  getDeadLine(){
        return pendingAt.plusDays(30).with(LocalDateTime.MAX);
    }

    public boolean isAdmin(){
        return (role == UserRole.ADMIN) || (role == UserRole.SUPER_ADMIN);
    }

    public void updatePassword(String password){
        this.password = password;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void updateStatus(UserStatus status) {
        this.status = status;
        if (status == UserStatus.PENDING) this.pendingAt = LocalDateTime.now();
    }
}
