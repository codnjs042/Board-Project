package com.example.demo.domain.post.controller;

import com.example.demo.domain.post.dto.PostDetailResponseDto;
import com.example.demo.domain.post.dto.PostRequestDto;
import com.example.demo.domain.post.dto.PostResponseDto;
import com.example.demo.domain.post.service.PostFacade;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
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
        Page<PostResponseDto> postPage = postService.searchPosts(page, type, keyword);
        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        return "post/list";
    }

    @GetMapping("/write")
    public String writeForm(@RequestParam(value="id", required = false) Long postId,
                            Model model){
        if(postId!=null) {
            PostResponseDto post = PostResponseDto.from(postService.findById(postId));
            model.addAttribute("post", post);
        }return "post/write";
    }

    @PostMapping("/write")
    public String write(@RequestParam(value="id", required = false) Long postId,
                        @Valid @ModelAttribute PostRequestDto dto,
                        @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.create(postId, dto, userDetails.getUser());
        return "redirect:/post";
    }

    @GetMapping("/{postId}")
    public String detail(@PathVariable Long postId,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model){
        PostDetailResponseDto postDetail = postFacade.getPostDetail(postId, userDetails);
        model.addAttribute("postDetail", postDetail);
        return "post/detail";
    }

    @GetMapping("/{postId}/edit")
    public String editForm(@PathVariable Long postId,
                           @AuthenticationPrincipal CustomUserDetails userDetails,
                           Model model){
        PostResponseDto post = postService.getPostForEdit(postId, userDetails.getUser());
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{postId}/edit")
    public String edit(@PathVariable Long postId,
                       @Valid @ModelAttribute PostRequestDto dto,
                       @AuthenticationPrincipal CustomUserDetails userDetails){
        postService.modify(postId, dto, userDetails.getUser());
        return "redirect:/post/{postId}";
    }

    @PostMapping("/{postId}/delete")
    public String delete(@PathVariable Long postId,
                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        postService.delete(postId, userDetails.getUser());
        return "redirect:/post";
    }
}
