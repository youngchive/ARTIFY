package com.elice.artBoard.post.entity;

import com.elice.artBoard.board.entity.Board;
import com.elice.artBoard.common.entity.BaseEntity;
import com.elice.artBoard.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 게시글키

    private String title; // 게시글 제목

    @Column(columnDefinition = "TEXT")
    private String content; // 게시글 내용

    /*@Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 생성 시간

    @Column(name = "edited_at")
    private LocalDateTime editedAt; // 수정 시간*/

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board; // 게시판 외래키로 단방향 관계 설정

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostImage> postImages;  // 해당 게시글에 속한 이미지 리스트


    /*// 엔티티가 처음 저장되기 전 호출 (생성 시간 설정)
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 엔티티가 업데이트되기 전 호출 (수정 시간 설정)
    @PreUpdate
    protected void onUpdate() {
        this.editedAt = LocalDateTime.now();
    }*/

    private Post(String title, String content, Board board, Member member) {
        this.title = title;
        this.content = content;
        this.board = board;
        this.member = member;
    }

    public static Post create(String title, String content, Board board, Member member) {
        return new Post(title, content, board, member);
    }

    public Post update(String title, String content) {
        this.title = title;
        this.content = content;

        return this;
    }

}
