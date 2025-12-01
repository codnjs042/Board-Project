package com.example.demo.post.controller;

import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.service.CommentService;
import com.example.demo.post.domain.PostState;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.service.PostService;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final UserService userService;

    @GetMapping("/write")
    public String write(@RequestParam(required = false) Long id, Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        if (id != null) {
            PostResponseDto post = postService.findById(id); // 여기서 id가 null이면 에러
            model.addAttribute("post", post);
        }
        return "post/write";
    }

    @PostMapping("/write")
    public String write(@RequestParam(required = false) Long id,
                        @ModelAttribute PostRequestDto dto,
                        Principal principal){
        if(principal==null) return "redirect:/user/login";
        if(id!=null) postService.upload(id, dto, principal.getName());
        else postService.create(dto, principal.getName());
        return "redirect:/post";
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String type,
            Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        Page<PostResponseDto> postPage
                = (keyword!=null && !keyword.isBlank())
                ? postService.searchPosts(PostState.PUBLISHED, keyword, type, page)
                : postService.findAll(PostState.PUBLISHED, page);
        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        return "post/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        PostResponseDto post = postService.findById(id);
        model.addAttribute("post", post);
        List<CommentResponseDto> comments = commentService.findAll(id);
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

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        else return "redirect:/user/login";
        PostResponseDto post = postService.findById(id);
        model.addAttribute("post", post);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute PostRequestDto dto, Principal principal){
        if(principal==null) return "redirect:/user/login";
        postService.update(id, dto, principal.getName());
        return "redirect:/post/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/user/login";
        postService.delete(id, principal.getName());
        return "redirect:/post";
    }
}
