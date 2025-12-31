package com.example.demo.domain.user.repository;

import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByStatus(UserStatus status);

    // 관리자용 사용자 정보 조회
    @Query("select u from User u " +
            "where u.role=:role " +
            "and u.status=:status " +
            "and (:keyword is null or " +
                "(u.username like %:keyword%) or " +
                "(u.nickname like %:keyword%))")
    Page<User> searchUsersForAdmin(@Param("role") UserRole role,
                                   @Param("status") UserStatus status,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);
}
