package com.example.demo.domain.admin.controller;

import com.example.demo.domain.admin.dto.PostAdminRequestDto;
import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.admin.dto.UserAdminRequestDto;
import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.admin.service.AdminFacade;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    public final AdminFacade adminFacade;

    @GetMapping
    public String userList(@RequestParam(defaultValue="0") int page,
                           @RequestParam(required=false) String keyword,
                           Model model){
        Page<UserAdminResponseDto> userPage = adminFacade.getUsers(null, null, keyword, page);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        return "admin/user";
    }

    @PostMapping("user/delete")
    public String userDelete(@ModelAttribute UserAdminRequestDto dto,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        adminFacade.deleteUsers(dto, userDetails.getUser());
        return "redirect:/admin";
    }

    @GetMapping("post")
    public String postList(@RequestParam(defaultValue="0") int page,
                           @RequestParam(required=false) String keyword,
                           @RequestParam(required=false) String type,
                           Model model){
        Page<PostAdminResponseDto> postPage = adminFacade.getPosts(PostState.PUBLISHED, null, page, type, keyword);
        model.addAttribute("postPage", postPage);
        model.addAttribute("type", type);
        model.addAttribute("keyword", keyword);
        return "admin/post";
    }

    @PostMapping("post/delete")
    public String postDelete(@ModelAttribute PostAdminRequestDto dto,
                             @AuthenticationPrincipal CustomUserDetails userDetails){
        adminFacade.deletePosts(dto, userDetails.getUser());
        return "redirect:/admin/post";
    }
}
