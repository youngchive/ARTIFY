package com.elice.artBoard.post.entity;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {
    private Long id; // 게시글키
    private String title; // 게시글 제목
    private String content; // 게시글 내용
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime editedAt; // 수정 시간
    private int memberId; // 회원키(외래키)
    private int boardId; // 게시판키(외래키)
    private Long imageId;

}
