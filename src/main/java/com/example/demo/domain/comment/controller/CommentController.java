package com.example.demo.domain.comment.controller;

import com.example.demo.domain.comment.dto.CommentRequestDto;
import com.example.demo.domain.comment.service.CommentFacade;
import com.example.demo.domain.comment.service.CommentService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/{postId}")
public class CommentController {
    private final CommentService commentService;
    private final CommentFacade commentFacade;

    @PostMapping("/comment")
    public String write(@PathVariable Long postId,
                        @RequestParam(required = false) Long parentId,
                        @ModelAttribute CommentRequestDto dto,
                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentFacade.create(parentId, postId, dto, userDetails.getUser());
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{commentId}")
    public String modify(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @ModelAttribute CommentRequestDto dto,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.modify(commentId, dto, userDetails.getUser());
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{commentId}/delete")
    public String delete(@PathVariable Long postId,
                         @PathVariable Long commentId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        commentService.delete(commentId, userDetails.getUser());
        return "redirect:/post/" + postId;
    }
}
