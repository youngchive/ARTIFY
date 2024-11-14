package com.elice.artBoard.board.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.elice.artBoard.board.constants.DefaultImgConst.DEFAULT_IMAGE_NAME;
import static com.elice.artBoard.board.constants.DefaultImgConst.DEFAULT_IMG_PATH;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class BoardImage {


    @Id
    @GeneratedValue
    @Column(name = "board_image_id")
    private Long id;

    @Column(name = "board_image_name", nullable = false)
    private String imageName;

    @Column(name = "board_image_path", nullable = false)
    private String imagePath;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    private BoardImage(String imageName, String imagePath, Board board) {
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.board = board;
    }

    public static BoardImage create(String imageName, String imagePath, Board board) {
        return new BoardImage(imageName, imagePath, board);
    }

    public static BoardImage createDefaultImage(Board board) {
        return new BoardImage(DEFAULT_IMAGE_NAME, DEFAULT_IMG_PATH, board);
    }
}
