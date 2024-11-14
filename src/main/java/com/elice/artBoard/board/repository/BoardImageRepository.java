package com.elice.artBoard.board.repository;

import com.elice.artBoard.board.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {

    @Query("select bi from BoardImage bi join fetch bi.board b where b.id = :boardId")
    Optional<BoardImage> findByBoardId(Long boardId);
}
