package com.example.demo.domain.admin.controller;

import com.example.demo.domain.admin.dto.PostAdminRequestDto;
import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.admin.dto.UserAdminRequestDto;
import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.admin.service.AdminService;
import com.example.demo.domain.user.dto.UserResponseDto;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {
    public final AdminService adminService;
    public final UserService userService;

    @GetMapping
    public String userList(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(required=false) String keyword,
            Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        Page<UserAdminResponseDto> userPage
                = (keyword!=null && !keyword.isBlank())
                ? adminService.searchUsers(keyword, page)
                : adminService.findAllUsers(page);
        model.addAttribute("userPage", userPage);
        model.addAttribute("keyword", keyword);
        return "admin/user";
    }

    @PostMapping("user/delete")
    public String userDelete(@ModelAttribute UserAdminRequestDto dto, Principal principal, Model model){
        try {
            adminService.deleteUsers(dto, principal.getName());
            return "redirect:/admin";
        }
        catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin";
        }
    }

    @GetMapping("post")
    public String postList(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(required=false) String keyword,
            @RequestParam(required=false) String type,
            Model model, Principal principal){
        if(principal!=null) {
            UserResponseDto user = userService.userInfo(principal.getName());
            model.addAttribute("user", user);
        }
        Page<PostAdminResponseDto> postPage
                = (keyword!=null && !keyword.isBlank())
                ? adminService.searchPosts(keyword, type, page)
                : adminService.findAllPosts(page);
        model.addAttribute("postPage", postPage);
        model.addAttribute("keyword", keyword);
        return "admin/post";
    }

    @PostMapping("post/delete")
    public String postDelete(@ModelAttribute PostAdminRequestDto dto, Principal principal, Model model){
        try {
            adminService.deletePosts(dto, principal.getName());
            return "redirect:/admin/post";
        }
        catch(IllegalArgumentException e){
            model.addAttribute("error", e.getMessage());
            return "redirect:/admin/post";
        }
    }
}
