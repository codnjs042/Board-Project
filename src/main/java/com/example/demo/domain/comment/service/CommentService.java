package com.example.demo.domain.comment.service;

import com.example.demo.domain.comment.domain.Comment;
import com.example.demo.domain.comment.domain.CommentStatus;
import com.example.demo.domain.comment.dto.CommentRequestDto;
import com.example.demo.domain.comment.dto.CommentResponseDto;
import com.example.demo.domain.comment.repository.CommentRepository;
import com.example.demo.domain.post.domain.Post;
import com.example.demo.domain.user.domain.User;
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
    public Comment connect(Long parentId, Long commentId){
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다"));

        comment.updateParent(parent);
        parent.updateChild(comment);

        return parent;
    }

    public List<CommentResponseDto> getRootComments(Long postId){
        return commentRepository.findAllByPost_IdAndStatusAndParentIsNull(postId, CommentStatus.ACTIVE)
                .stream()
                .map(CommentResponseDto::from)
                .toList();
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
