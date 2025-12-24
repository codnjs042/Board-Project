package com.example.demo.domain.comment.service;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.domain.CommentStatus;
import com.example.demo.domain.comment.dto.CommentRequestDto;
import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.repository.CommentRepository;
import com.example.demo.domain.notice.service.NoticeService;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.post.domain.PostType;
import com.example.demo.domain.post.repository.PostRepository;
import com.example.demo.domain.user.domain.User;
import com.example.demo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {
    public final CommentRepository commentRepository;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final NoticeService noticeService;

    @Transactional
    public void create(Long parentId, Long postId, CommentRequestDto dto, String username){
        User author = userRepository.findByUsername(username)
                .orElseThrow(()-> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

        if (post.getType()== PostType.NOTICE){
            throw new IllegalArgumentException("공지글에는 댓글을 작성할 수 없습니다");
        }

        Comment comment = Comment.builder()
                .comment(dto.getComment())
                .author(author)
                .authorName(author.getNickname())
                .post(post)
                .status(CommentStatus.ACTIVE)
                .build();
        commentRepository.save(comment);

        post.updateCommentCount(1L);

        if(parentId!=null){
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글을 찾을 수 없습니다"));
            comment.updateParent(parent);
            parent.updateChild(comment);
            noticeService.send(author, parent.getAuthor(), post);
        }
        noticeService.send(author, post.getAuthor(), post);
    }

    @Transactional(readOnly = true)
    public CommentResponseDto findById(Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));
        return new CommentResponseDto(comment);
    }

    @Transactional
    public void modify(Long id, CommentRequestDto dto){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));
        comment.modify(dto.getComment());
    }

    @Transactional
    public void delete(Long id){
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));
        comment.updateStatus(CommentStatus.DISABLED);
        comment.getPost().updateCommentCount(1L);
    }
}
