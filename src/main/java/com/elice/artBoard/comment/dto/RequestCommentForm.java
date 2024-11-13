package com.elice.artBoard.comment.dto;

import lombok.Data;

@Data
public class RequestCommentForm {
    private Long postId;
    private String content;
}
