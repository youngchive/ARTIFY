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
        return boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("일치하는 게시판이 없습니다"));
    }

    public List<Board> findBoards() {
        return boardRepository.findAll();
    }

    public List<ResponseBoardForm> findBoardsAndImages() {
        List<Board> boards = boardRepository.findAll();
        List<BoardImage> images = boardImageService.findImagesByBoardId(boards);
        return getResponseFormList(boards, images);
    }

    private List<ResponseBoardForm> getResponseFormList(List<Board> boards, List<BoardImage> images) {

        List<ResponseBoardForm> formList = new ArrayList<>();
        int bound = boards.size();

        IntStream.range(0, bound).forEach(i -> {
            Board board = boards.get(i);
            BoardImage boardImage = images.get(i);
            formList.add(new ResponseBoardForm(
                    board.getId(), board.getTitle(), board.getDescription(), boardImage.getId(), board.getMember().getMemberId()
            ));
        });

        return formList;
    }
}
