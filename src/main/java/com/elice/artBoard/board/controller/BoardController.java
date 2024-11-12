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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 게시글 목록 페이지
    @GetMapping("/{boardId}")
    public String getBoard(@PathVariable Long boardId,
                           @RequestParam(defaultValue = "1") int page,  // 페이지 번호 파라미터 추가
                           Model model,
                           @SessionAttribute(name = "memberId", required = false) Integer memberId) {

        Member member = memberService.findMember(memberId);
        model.addAttribute("member", member);

        Pageable pageable = PageRequest.of(page - 1, 10);  // 한 페이지에 10개의 게시글을 표시

        Page<Post> posts = postService.findPostsByBoardId(boardId, pageable);
        log.info("###########Posts: {}", posts);

        model.addAttribute("posts", posts);
        model.addAttribute("boardId", boardId);
        model.addAttribute("totalPages", posts);
        model.addAttribute("currentPage", page);

        return "post/list";
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
