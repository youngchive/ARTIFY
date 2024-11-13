package com.elice.artBoard.post.entity;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPostDto {
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private Long boardId;  // boardId 필드 추가
    private MultipartFile image;
    private Integer memberId;

    public PostPostDto(String title, String content, Long boardId, Integer memberId) {
        this.title = title;
        this.content = content;
        this.boardId = boardId;
        this.memberId = memberId;
    }

}
