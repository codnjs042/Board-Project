package com.example.demo.domain.notice.repository;

import com.example.demo.domain.notice.domain.Notice;
import com.example.demo.domain.notice.domain.NoticeState;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByReceiveUser_Id(Long userId);

    Long countByReceiveUser_IdAndState(Long userId, NoticeState state);
}
