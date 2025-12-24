package com.example.demo.domain.like.controller;

import com.example.demo.domain.like.service.LikeService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public String postLike(@PathVariable("postId") Long postId,
                           @AuthenticationPrincipal CustomUserDetails userDetails){
        likeService.toggleLike(postId, userDetails.getId());
        return "redirect:/post/{postId}";
    }
}
