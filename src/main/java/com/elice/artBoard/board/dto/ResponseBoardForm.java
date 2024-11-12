package com.elice.artBoard.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.relational.core.sql.In;

@Data
@AllArgsConstructor
public class ResponseBoardForm {

    private Long boardId;
    private String title;
    private String description;
    private Long imageId;
    private Integer memberId;
}
