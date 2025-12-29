package com.example.demo.domain.admin.service;

import com.example.demo.domain.admin.dto.PostAdminRequestDto;
import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.admin.dto.UserAdminRequestDto;
import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostState;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminFacade {
    private final UserService userService;
    private final PostService postService;

    public Page<UserAdminResponseDto> getAdminUsers(UserRole role, UserStatus status, String keyword, int page) {
        return userService.getAdminUsers(role, status, keyword, page);
    }

    @Transactional
    public void deleteUsers(UserAdminRequestDto dto, Long userId){
        User user = userService.getUserId(userId);

        if(user.getRole()!=UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new IllegalArgumentException("계정 삭제 권한이 없습니다");

        if(dto.getId()==null)
            throw new IllegalArgumentException("삭제할 항목을 먼저 선택하세요");

        List<User> users = userService.getUsersIds(dto.getId());

        for (User u : users) {
            if(u.getRole()==UserRole.SUPER_ADMIN)
                throw new IllegalArgumentException("슈퍼 관리자 계정은 삭제할 수 없습니다");
            if(user.getRole()==UserRole.ADMIN
                    && u.getUsername()!=user.getUsername()
                    && u.getRole()==UserRole.ADMIN)
                throw new IllegalArgumentException("일반 관리자는 다른 관리자 계정을 삭제할 수 없습니다");
            u.updateStatus(UserStatus.DISABLED);
        }
    }


    public Page<PostAdminResponseDto> getAdminPosts(PostState state, PostStatus status, int page, String type, String keyword){
        return postService.getAdminPosts(state,status, page, type, keyword);
    }

    @Transactional
    public void deletePosts(PostAdminRequestDto dto, Long userId){
        User user = userService.getUserId(userId);

        if(user.getRole()!= UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new IllegalArgumentException("계정 삭제 권한이 없습니다");

        if(dto.getId()==null)
            throw new IllegalArgumentException("삭제할 항목을 먼저 선택하세요");

        List<Post> posts = postService.getPostsIds(dto.getId());

        for (Post p : posts) {
            if(p.getAuthor().getRole()==UserRole.SUPER_ADMIN)
                throw new IllegalArgumentException("슈퍼 관리자 게시글은 삭제할 수 없습니다");
            if(user.getRole()==UserRole.ADMIN
                    && p.getAuthor().getUsername()!=user.getUsername()
                    && p.getAuthor().getRole()==UserRole.ADMIN)
                throw new IllegalArgumentException("일반 관리자는 다른 관리자 게시글을 삭제할 수 없습니다");
            p.updateStatus(PostStatus.DISABLED);
        }
    }
}
