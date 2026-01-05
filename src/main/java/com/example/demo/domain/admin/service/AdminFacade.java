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
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
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

    public Page<UserAdminResponseDto> getUsers(UserRole role, UserStatus status, String keyword, int page) {
        return userService.searchUsersForAdmin(role, status, keyword, page);
    }

    @Transactional
    public void deleteUsers(UserAdminRequestDto dto, User user){
        if(user.getRole()!=UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        if(dto.getId()==null)
            throw new BusinessException(ErrorCode.INVALID_INPUT);

        List<User> users = userService.findAllById(dto.getId());

        for (User u : users) {
            if(u.getRole()==UserRole.SUPER_ADMIN)
                throw new BusinessException(ErrorCode.POLICY_VIOLATION);
            if(user.getRole()==UserRole.ADMIN
                    && u.getUsername().equals(user.getUsername())
                    && u.getRole()==UserRole.ADMIN)
                throw new BusinessException(ErrorCode.INVALID_PERMISSION);
            u.updateStatusForce(UserStatus.DISABLED);
        }
    }


    public Page<PostAdminResponseDto> getPosts(PostState state, PostStatus status, int page, String type, String keyword){
        return postService.searchPostsForAdmin(state,status, page, type, keyword);
    }

    @Transactional
    public void deletePosts(PostAdminRequestDto dto, User user){
        if(user.getRole()!= UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        if(dto.getId()==null)
            throw new BusinessException(ErrorCode.INVALID_INPUT);

        List<Post> posts = postService.findAllById(dto.getId());

        for (Post p : posts) {
            if(p.getAuthor().getRole()==UserRole.SUPER_ADMIN)
                throw new BusinessException(ErrorCode.POLICY_VIOLATION);
            if(user.getRole()==UserRole.ADMIN
                    && p.getAuthor().getUsername().equals(user.getUsername())
                    && p.getAuthor().getRole()==UserRole.ADMIN)
                throw new BusinessException(ErrorCode.INVALID_PERMISSION);
            p.updateStatus(PostStatus.DISABLED);
        }
    }
}
