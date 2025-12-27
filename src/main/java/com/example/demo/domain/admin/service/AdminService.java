package com.example.demo.domain.admin.service;

import com.example.demo.domain.admin.dto.PostAdminRequestDto;
import com.example.demo.domain.admin.dto.PostAdminResponseDto;
import com.example.demo.domain.admin.dto.UserAdminRequestDto;
import com.example.demo.domain.admin.dto.UserAdminResponseDto;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostStatus;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.domain.UserRole;
import com.example.demo.domain.user.domain.UserStatus;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    private final int pageSize = 10;

    public Page<UserAdminResponseDto> findAllUsers(int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable).map(UserAdminResponseDto::from);
    }

    public Page<UserAdminResponseDto> searchUsers(String keyword, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<User> result = userRepository.findByUsernameContainingOrNicknameContaining(keyword, keyword, pageable);
        return result.map(UserAdminResponseDto::from);
    }

    public Page<PostAdminResponseDto> findAllPosts(int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return postRepository.findAll(pageable).map(PostAdminResponseDto::from);
    }

    public Page<PostAdminResponseDto> searchPosts(String keyword, String type, int page){
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        Page<Post> result = switch(type){
            case "user" -> postRepository.findByAuthor_UsernameContainingOrAuthorNameContaining(keyword, keyword, pageable);
            case "post" -> postRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
            default -> Page.empty(pageable);
        };
        return result.map(PostAdminResponseDto::from);
    }

    @Transactional
    public void deleteUsers(UserAdminRequestDto dto, String username){
        User user = userRepository.findByUsername(username)
                        .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지 않습니다"));

        if(user.getRole()!=UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new IllegalArgumentException("계정 삭제 권한이 없습니다");

        if(dto.getId()==null)
            throw new IllegalArgumentException("삭제할 항목을 먼저 선택하세요");

        List<User> users = userRepository.findAllById(dto.getId());

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

    @Transactional
    public void deletePosts(PostAdminRequestDto dto, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()->new IllegalArgumentException("사용자가 존재하지 않습니다"));

        if(user.getRole()!=UserRole.SUPER_ADMIN && user.getRole()!=UserRole.ADMIN)
            throw new IllegalArgumentException("계정 삭제 권한이 없습니다");

        if(dto.getId()==null)
            throw new IllegalArgumentException("삭제할 항목을 먼저 선택하세요");

        List<Post> posts = postRepository.findAllById(dto.getId());

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
