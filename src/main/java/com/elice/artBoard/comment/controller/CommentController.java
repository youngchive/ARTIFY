package com.elice.artBoard.comment.controller;

import com.elice.artBoard.comment.dto.RequestCommentForm;
import com.elice.artBoard.comment.service.CommentService;
import com.elice.artBoard.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create")
    public String createComment(RequestCommentForm form, RedirectAttributes redirectAttributes,
                                @SessionAttribute(name = "member", required = false) Member member) {

        commentService.addComment(form, member);
        redirectAttributes.addAttribute("postId", form.getPostId());

        return "redirect:/posts/{postId}";
    }

    @PutMapping("/edit/{commentId}")
    public String updateComment(@PathVariable Long commentId, RequestCommentForm form, RedirectAttributes redirectAttributes,
                                @SessionAttribute(name = "member", required = false) Member member) {

        commentService.updateComment(commentId, form, member);
        redirectAttributes.addAttribute("postId", form.getPostId());

        return "redirect:/posts/{postId}";
    }

    @DeleteMapping("/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes,
                                @SessionAttribute(name = "member", required = false) Member member) {

        Long postId = commentService.removeComment(commentId, member);
        redirectAttributes.addAttribute("postId", postId);
        return "redirect:/posts/{postId}";
    }
}
