package com.elice.artBoard.board.entity;

import com.elice.artBoard.common.entity.BaseEntity;
import com.elice.artBoard.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "board_id")
    private Long id;

    private String title;
    private String description;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY)
    private Member member;

    public Board(String title, String description, Member member) {
        this.title = title;
        this.description = description;
        this.member = member;
    }

    public static Board create(String title, String description, Member member) {
        return new Board(title, description, member);
    }

    public Board update(String title, String description) {
        this.title = title;
        this.description = description;
        return this;
    }
}
