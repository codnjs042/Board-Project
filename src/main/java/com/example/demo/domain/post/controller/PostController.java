package com.example.demo.domain.post.controller;

import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.service.CommentService;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
public class PostController {
    public final PostService postService;
    public final CommentService commentService;

    @GetMapping
    public String list(@RequestParam(defaultValue="0") int page,
                       @RequestParam(required=false) String keyword,
                       @RequestParam(required=false) String type,
                       Model model){
        Page<PostResponseDto> postPage
                = (keyword!=null && !keyword.isBlank())
                ? postService.searchPost(PostState.PUBLISHED, keyword, type, page)
                : postService.findAllPost(PostState.PUBLISHED, page);
        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        return "post/list";
    }

    @GetMapping("/write")
    public String writeForm(){
        return "post/write";
    }

    @PostMapping("/write")
    public String write(@ModelAttribute PostRequestDto dto,
                        @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.create(dto, userDetails.getId());
        return "redirect:/post";
    }

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId,
                         Model model){
        PostResponseDto post = postService.getPostDetail(postId);
        model.addAttribute("post", post);
        List<CommentResponseDto> comments = commentService.findAll(postId);
        model.addAttribute("comments", comments);
        return "post/detail";
    }

    @PostMapping("/{id}")
    public String postLike(@PathVariable Long id, Principal principal, Model model){
        postService.likeToggle(id, principal.getName());

        boolean myLike = postService.myLike(id, principal.getName());
        model.addAttribute("myLike", myLike);

        return "redirect:/post/{id}";
    }

    @GetMapping("/{postId}/edit")
    public String editForm(@PathVariable Long postId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model){
        PostResponseDto post = postService.getPostForEdit(postId, userDetails.getId());
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId,
                       @ModelAttribute PostRequestDto dto,
                       @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.modify(postId, dto, userDetails.getId());
        return "redirect:/post/{postId}";
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(postId, userDetails.getId());
        return "redirect:/post";
    }
}
