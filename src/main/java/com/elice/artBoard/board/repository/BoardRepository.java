package com.elice.artBoard.board.repository;

import com.elice.artBoard.board.entity.Board;
import com.elice.artBoard.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b join fetch b.member where b.id = :boardId")
    Optional<Board> findById(Long boardId);

    List<Board> findByMember(Member member);
}
