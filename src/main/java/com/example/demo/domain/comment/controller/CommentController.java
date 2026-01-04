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
    public String write(
            @PathVariable Long postId,
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long editId,
            @ModelAttribute CommentRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(editId==null)
            commentFacade.create(parentId, postId, dto, userDetails.getUser());
        else
            return modify(postId, editId, dto);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{id}/edit")
    public String modify(@PathVariable Long postId, Long id, @ModelAttribute CommentRequestDto dto){
        commentService.modify(id, dto);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable Long postId, @PathVariable Long id){
        commentService.delete(id);
        return "redirect:/post/" + postId;
    }
}
