package com.elice.artBoard.comment.service;

import com.elice.artBoard.comment.domain.Comment;
import com.elice.artBoard.comment.dto.RequestCommentForm;
import com.elice.artBoard.comment.dto.ResponseCommentDto;
import com.elice.artBoard.comment.repository.CommentRepository;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public Comment addComment(RequestCommentForm form, Member member) {

        Post post = postRepository.findById(form.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 게시물이 없습니다"));

        Comment comment = Comment.create(post, form.getContent(), member);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, RequestCommentForm form, Member member) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 댓글 없습니다"));

        if (!comment.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다");
        }

        comment.update(form.getContent());
        return comment;
    }

    public Long removeComment(Long commentId, Member member) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 댓글 없습니다"));

        if (!comment.getMember().getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다");
        }

        Long postId = comment.getPost().getId();
        commentRepository.delete(comment);
        return postId;
    }

    public List<ResponseCommentDto> findComments(Long postId) {
        List<Comment> comments = commentRepository.findAll(postId);
        return comments.stream()
                .map(c -> new ResponseCommentDto(
                        c.getId(), c.getMember().getMemberId(), c.getMember().getNickname(),
                        c.getContent(), c.getCreateDate(), updateTime(c.getCreateDate(), c.getEditDate())))
                .toList();
    }

    private String updateTime(LocalDateTime createDate, LocalDateTime editDate) {

        final int ONE_MINUTE = 1;
        final int ONE_HOUR = 1;
        final int ONE_DAY = 1;
        final int ONE_YEAR = 365;

        LocalDateTime currentTime = LocalDateTime.now();
        Duration duration = Duration.between(editDate, currentTime);

        if (createDate.equals(editDate)) {
            return "수정사항 없음";
        }
        if (duration.toMinutes() < ONE_MINUTE) {
            return "방금 전 수정";
        }
        if (duration.toHours() < ONE_HOUR) {
            return duration.toMinutes() + "분전 수정";
        }
        if (duration.toDays() < ONE_DAY) {
            return duration.toHours() + "시간전 수정";
        }
        if (duration.toDays() < ONE_YEAR) {
            return duration.toDays() + "일전 수정";
        }
        return (duration.toDays() / ONE_YEAR) + "년전 수정";
    }
}
