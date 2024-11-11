package com.elice.artBoard.comment.service;

import com.elice.artBoard.comment.domain.Comment;
import com.elice.artBoard.comment.dto.RequestCommentForm;
import com.elice.artBoard.comment.dto.ResponseCommentDto;
import com.elice.artBoard.comment.repository.CommentRepository;
import com.elice.artBoard.member.repository.MemberRepository;
import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public Comment addComment(RequestCommentForm form) {
        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 게시물이 없습니다"));

        Comment comment = Comment.create(post, form.getContent());

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, RequestCommentForm form) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 댓글 없습니다"));

        comment.update(form.getContent());
        return comment;
    }

    public Long removeComment(Long commentId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 댓글 없습니다"));

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        return postId;
    }

    //TODO 회원 추가후 변경
    public List<ResponseCommentDto> findComments(Long postId) {
        List<Comment> comments = commentRepository.findAll(postId);
        return comments.stream()
                .map(c -> new ResponseCommentDto(c.getId(), 1, c.getContent(), c.getCreateDate(), c.getEditDate()))
                .toList();
    }
}
