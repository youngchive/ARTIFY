package com.elice.artBoard.board.controller;

import com.elice.artBoard.board.domain.Board;
import com.elice.artBoard.board.domain.BoardImage;
import com.elice.artBoard.board.dto.RequestBoardForm;
import com.elice.artBoard.board.service.BoardImageService;
import com.elice.artBoard.board.service.BoardService;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.member.service.MemberService;
import com.elice.artBoard.post.service.PostService;
import com.elice.artBoard.post.entity.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.List;

import static com.elice.artBoard.board.constants.DefaultImgConst.DEFAULT_IMG_PATH;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final MemberService memberService;
    private final BoardService boardService;
    private final BoardImageService boardImageService;
    private final PostService postService;

    @GetMapping
    public String boardList(Model model, @SessionAttribute(name = "memberId", required = false) Integer memberId) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        model.addAttribute("list", boardService.findBoardsAndImages());
        return "/board/boards";
    }

    @GetMapping("/create")
    public String createForm(Model model, @SessionAttribute(name = "memberId", required = false) Integer memberId) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        model.addAttribute("form", new RequestBoardForm());
        return "board/create-form";
    }

    @PostMapping("/create")
    public String createBoard(@Validated @ModelAttribute("form") RequestBoardForm form, BindingResult bindingResult,
                              @SessionAttribute(name = "memberId", required = false) Integer memberId, Model model) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        if (bindingResult.hasErrors()) {
            return "board/create-form";
        }

        Board board = boardService.save(form);
        boardImageService.save(form, board);
        return "redirect:/boards";
    }

    @GetMapping("/put/{boardId}")
    public String updateForm(@PathVariable Long boardId, Model model, @SessionAttribute(name = "memberId", required = false) Integer memberId) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        Board board = boardService.findBoard(boardId);
        model.addAttribute("form", new RequestBoardForm(board.getTitle(), board.getDescription(), null));
        return "board/edit-form";
    }

    @PutMapping("/put/{boardId}")
    public String updateBoard(@PathVariable Long boardId, @Validated @ModelAttribute("form") RequestBoardForm form, BindingResult bindingResult,
                              @SessionAttribute(name = "memberId", required = false) Integer memberId, Model model) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        if (bindingResult.hasErrors()) {
            return "board/edit-form";
        }

        Board board = boardService.update(boardId, form);
        boardImageService.update(board, form);
        return "redirect:/boards";
    }

    @DeleteMapping("/delete/{boardId}")
    public String deleteBoard(@PathVariable Long boardId) {
        boardImageService.delete(boardId);
        boardService.delete(boardId);
        return "redirect:/boards";
    }

    @ResponseBody
    @GetMapping("image/{imageId}")
    public ResponseEntity downloadImg(@PathVariable Long imageId) throws MalformedURLException {
        BoardImage image = boardImageService.findByImgId(imageId);

        return getResponse(image);
    }

    // 게시글 연결
    @GetMapping("/{boardId}")
    public String getBoard(@PathVariable Long boardId, Model model, @SessionAttribute(name = "memberId", required = false) Integer memberId) {
        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        // boardId를 사용하여 해당 게시판에 연결된 게시글 목록을 가져옴
        List<Post> posts = postService.findPostsByBoardId(boardId);  // 게시판에 해당하는 게시글 목록을 가져오는 메서드

        // 게시글 목록을 모델에 추가하여 전달
        model.addAttribute("posts", posts);

        // 게시판 상세 페이지 대신 게시글 목록 페이지로 이동
        return "post/list";  // 게시판에 속한 게시글 목록을 보여주는 뷰로 이동
    }

    private ResponseEntity getResponse(BoardImage image) throws MalformedURLException {
        Resource resource;

        if (image.getImagePath().equals(DEFAULT_IMG_PATH)) {
            resource = new ClassPathResource(DEFAULT_IMG_PATH);

        } else {
            resource = new UrlResource("file:" + image.getImagePath());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .contentType(MediaType.IMAGE_PNG)
                .body(resource);
    }
}
