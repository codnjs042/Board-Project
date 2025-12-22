package com.example.demo.domain.notice.controller;

import com.example.demo.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Controller
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    @GetMapping(value = "/notice/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter notice(@PathVariable("userId") Long userId){
        return noticeService.connect(userId);
    }
}
