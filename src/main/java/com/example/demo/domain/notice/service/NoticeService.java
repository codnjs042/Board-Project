package com.example.demo.domain.notice.service;

import com.example.demo.domain.notice.domain.Notice;
import com.example.demo.domain.notice.dto.NoticeResponseDto;
import com.example.demo.domain.notice.repository.NoticeRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final NoticeRepository noticeRepository;

    public SseEmitter connect(Long userId){
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitters.put(userId, emitter);

        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));

        return emitter;
    }

    @Transactional
    public void send(User sendUser, User receiveUser, Post post){
        SseEmitter emitter = emitters.get(receiveUser.getId());

        Notice notice = Notice.builder()
                .sendUser(sendUser)
                .receiveUser(receiveUser)
                .post(post)
                .build();
        noticeRepository.save(notice);

        if(emitter!=null){
            try{
                emitter.send(SseEmitter.event()
                        .data(NoticeResponseDto.from(notice)));
            }
            catch(IOException e){
                emitters.remove(receiveUser.getId());
            }
        }
    }
}
