package com.example.demo.domain.user.domain;

import com.example.demo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@NoArgsConstructor(access= AccessLevel.PROTECTED)
public class User extends BaseEntity {
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

    @Builder
    public User(String username, String password, String nickname, UserRole role, UserStatus status){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = (role==null) ? UserRole.USER : role;
        this.status = (status==null) ? UserStatus.ACTIVE : status;
    }

    public LocalDateTime getDeadLine(){
        return getUpdatedAt().plusDays(30).with(LocalDateTime.MAX);
    }

    public boolean isAdmin(){
        return (role == UserRole.ADMIN) || (role == UserRole.SUPER_ADMIN);
    }

    public boolean isSuperAdmin(){
        return role == UserRole.SUPER_ADMIN;
    }

    public boolean isSocialUser(){
        return role == UserRole.KAKAO_USER;
    }

    //matches(문자열, 암호화된 문자열)
    public boolean mismatchRawPw(PasswordEncoder encoder, String rawPassword){
        return encoder.matches(rawPassword, this.password);
    }

    public void updatePw(String password){
        this.password = password;
    }

    public void updateNickname(String nickname){
        this.nickname = nickname;
    }

    public void toggleStatusForUser() {
        if(status == UserStatus.ACTIVE)
            status = UserStatus.PENDING;
        else if(status == UserStatus.PENDING)
            status = UserStatus.ACTIVE;
    }

    public void updateStatusForce(UserStatus status){
        this.status = status;
    }
}
