package com.example.demo.comment.controller;

import com.example.demo.comment.dto.CommentRequestDto;
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.service.CommentService;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post/{postId}")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

//    @GetMapping("/comment")
//    @ResponseBody
//    public List<CommentResponseDto> list(@PathVariable Long postId){
//        return commentService.findAllByPost_Id(postId);
//    }

    @PostMapping("/comment")
    public String write(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false) Long editId,
            @PathVariable Long postId,
            @ModelAttribute CommentRequestDto dto, Principal principal) {
        if(editId==null)
            commentService.create(parentId, postId, dto, principal.getName());
        else
            return edit(postId, editId, dto);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{id}/edit")
    public String edit(@PathVariable Long postId, Long id, @ModelAttribute CommentRequestDto dto){
        commentService.edit(id, dto);
        return "redirect:/post/" + postId;
    }

    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable Long postId, @PathVariable Long id){
        commentService.delete(id);
        return "redirect:/post/" + postId;
    }

    @GetMapping("/comment/{id}/reply")
    public String reply(@PathVariable Long postId, @PathVariable Long parentId, Model model){
        CommentResponseDto comment = commentService.findById(parentId);
        model.addAttribute("comment", comment);
        return "redirect:/post/" + postId;
    }
}
