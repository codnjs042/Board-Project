package com.example.demo.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length=30)
    private String username;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false, length=30)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime pendingAt;

    public void updatePassword(String password){
        this.password=password;
    }
    public void updateNickname(String nickname){
        this.nickname=nickname;
    }
    public void updateStatus(UserStatus status){
        this.status=status;
        if(status==UserStatus.PENDING)
            this.pendingAt=LocalDateTime.now();
    }
}
