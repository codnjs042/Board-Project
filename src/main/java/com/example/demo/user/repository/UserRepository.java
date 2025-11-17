package com.example.demo.user.repository;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Page<User> findByUsernameContainingOrNicknameContaining(String username, String nickname, Pageable pageable);

    List<User> findByStatus(UserStatus status);
}
