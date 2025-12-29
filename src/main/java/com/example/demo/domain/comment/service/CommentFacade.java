package com.example.demo.domain.comment.service;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.dto.CommentRequestDto;
import com.example.demo.domain.notice.service.NoticeService;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.service.PostService;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFacade {
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final NoticeService noticeService;

    @Transactional
    public void create(Long parentId, Long postId, CommentRequestDto dto, Long userId){
        User user = userService.getUserId(userId);

        Post post = postService.getPostId(postId);

        if (post.getType()== PostType.NOTICE){
            throw new IllegalArgumentException("공지글에는 댓글을 작성할 수 없습니다");
        }

        Comment comment = commentService.save(user, post, dto);

        if(parentId!=null){
            Comment parent = commentService.connect(parentId, comment.getId());
            noticeService.send(user, parent.getAuthor(), post);
        }
        noticeService.send(user, post.getAuthor(), post);
    }

}
