package com.example.demo.domain.notice.dto;

import com.example.demo.domain.notice.domain.Notice;
import com.example.demo.domain.notice.domain.NoticeState;
import com.example.demo.domain.notice.domain.NoticeStatus;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NoticeResponseDto {
    private User sendUser;
    private User receiveUser;
    private Post post;
    private LocalDateTime createdAt;
    private NoticeState state;
    private NoticeStatus status;

    public NoticeResponseDto(Notice notice){
        this.sendUser = notice.getSendUser();
        this.receiveUser = notice.getReceiveUser();
        this.post = notice.getPost();
        this.createdAt = notice.getCreatedAt();
        this.state = notice.getState();
        this.status = notice.getStatus();
    }

    public static NoticeResponseDto from(Notice notice){ return new NoticeResponseDto(notice);}
}
