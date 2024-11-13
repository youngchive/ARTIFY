package com.elice.artBoard.board.service;

import com.elice.artBoard.board.domain.Board;
import com.elice.artBoard.board.domain.BoardImage;
import com.elice.artBoard.board.dto.RequestBoardForm;
import com.elice.artBoard.board.dto.ResponseBoardForm;
import com.elice.artBoard.board.repository.BoardRepository;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final PostService postService;
    private final BoardImageService boardImageService;


    @Transactional
    public Board save(RequestBoardForm form, Member member) {
        Board board = Board.create(form.getTitle(), form.getDescription(), member);
        return boardRepository.save(board);
    }

    @Transactional
    public Board update(Long boardId, RequestBoardForm form, Member member) {
        Board findBoard = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("일치하는 게시판이 없습니다"));

        if (!member.getMemberId().equals(findBoard.getMember().getMemberId())) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다");
        }

        return findBoard.update(form.getTitle(), form.getDescription());
    }

    @Transactional
    public void delete(Long boardId, Member member) {

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("일치하는 게시판이 없습니다"));

        if (!member.getMemberId().equals(board.getMember().getMemberId())) {
            throw new IllegalArgumentException("회원 정보가 일치하지 않습니다");
        }

        Post post = postService.getPostByBoardId(boardId);

        if (post != null) {
            postService.deletePost(post.getId());
        }

        boardImageService.delete(board.getId());
        boardRepository.delete(board);
    }

    public Board findBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 게시판이 없습니다"));
    }

    public Page<ResponseBoardForm> findBoardsAndImages(int page) {

        final int OFFSET = page - 1;
        final int LIMIT = 6;

        PageRequest pageRequest = PageRequest.of(OFFSET, LIMIT, Sort.Direction.DESC, "id");

        Page<Board> boards = boardRepository.findAll(pageRequest);

        return boards.map(b -> {
            BoardImage bi = boardImageService.findByBordId(b.getId());
            return new ResponseBoardForm(b.getId(), b.getTitle(), b.getDescription(), bi.getId(), b.getMember().getMemberId());
        });
    }
}
