package com.example.demo.domain.post.controller;

import com.example.demo.domain.post.dto.PostDetailDto;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostFacade;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
public class PostController {
    public final PostService postService;
    public final PostFacade postFacade;

    @GetMapping
    public String list(@RequestParam(defaultValue="0") int page,
                       @RequestParam(required=false) String type,
                       @RequestParam(required=false) String keyword,
                       Model model){
        Page<PostResponseDto> postPage
                = postService.getPosts(page, type, keyword);
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
        postFacade.create(dto, userDetails.getId());
        return "redirect:/post";
    }

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model){
        PostDetailDto postDetail = postFacade.getPostDetail(postId, userDetails);
        model.addAttribute("postDetail", postDetail);
        return "post/detail";
    }

    @GetMapping("/{postId}/edit")
    public String editForm(@PathVariable Long postId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model){
        PostResponseDto post = postFacade.getPostForEdit(postId, userDetails.getId());
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId,
                       @ModelAttribute PostRequestDto dto,
                       @AuthenticationPrincipal CustomUserDetails userDetails){
        postFacade.modify(postId, dto, userDetails.getId());
        return "redirect:/post/{postId}";
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postFacade.delete(postId, userDetails.getId());
        return "redirect:/post";
    }
}
