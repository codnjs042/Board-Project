package com.example.demo.domain.comment.service;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.domain.CommentStatus;
import com.example.demo.domain.comment.dto.CommentRequestDto;
import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.repository.CommentRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
import com.example.demo.global.exception.BusinessException;
import com.example.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    public final CommentRepository commentRepository;

    @Transactional
    public Comment save(User user, Post post, CommentRequestDto dto){
        Comment comment = Comment.builder()
                .comment(dto.getComment())
                .author(user)
                .authorName(user.getNickname())
                .post(post)
                .build();

        commentRepository.save(comment);

        post.updateCommentCount(1L);
        return comment;
    }

    @Transactional
    public void connect(Long parentId, Long commentId){
        Comment parent = findById(parentId);

        Comment comment = findById(commentId);

        comment.updateParent(parent);
        parent.updateChild(comment);
    }

    public Comment findById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public List<CommentResponseDto> getRootComments(Long postId){
        return commentRepository.findAllByRootComments(postId)
                .stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public void modify(Long commentId, CommentRequestDto dto, User user){
        Comment comment = findById(commentId);

        if(!comment.isAuthor(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        comment.modify(dto.getComment());
    }

    @Transactional
    public void delete(Long commentId, User user){
        Comment comment = findById(commentId);

        if(!comment.isAuthor(user.getId()))
            throw new BusinessException(ErrorCode.INVALID_PERMISSION);

        comment.updateStatus(CommentStatus.DISABLED);
        comment.getPost().updateCommentCount(1L);
    }
}
