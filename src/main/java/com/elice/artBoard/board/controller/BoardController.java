package com.elice.artBoard.board.controller;

import com.elice.artBoard.board.entity.Board;
import com.elice.artBoard.board.entity.BoardImage;
import com.elice.artBoard.board.dto.RequestBoardForm;
import com.elice.artBoard.board.service.BoardImageService;
import com.elice.artBoard.board.service.BoardService;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.service.PostService;
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

import static com.elice.artBoard.board.constants.DefaultImgConst.DEFAULT_IMG_PATH;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;
    private final BoardImageService boardImageService;
    private final PostService postService;

    @GetMapping
    public String boardList(@RequestParam(defaultValue = "1") int page,
            @SessionAttribute(name = "member", required = false) Member member, Model model) {

        model.addAttribute("member", member);
        model.addAttribute("list", boardService.findBoardsAndImages(page));
        return "/board/boards";
    }

    @GetMapping("/create")
    public String createForm(Model model, @SessionAttribute(name = "member", required = false) Member member) {

        model.addAttribute("member", member);
        model.addAttribute("form", new RequestBoardForm());

        return "board/create-form";
    }

    @PostMapping("/create")
    public String createBoard(@Validated @ModelAttribute("form") RequestBoardForm form, BindingResult bindingResult,
                              @SessionAttribute(name = "member", required = false) Member member, Model model) {

        model.addAttribute("member", member);

        if (bindingResult.hasErrors()) {
            return "board/create-form";
        }

        Board board = boardService.save(form, member);
        boardImageService.save(form, board);

        return "redirect:/boards";
    }

    @GetMapping("/edit/{boardId}")
    public String updateForm(@PathVariable Long boardId, Model model, @SessionAttribute(name = "member", required = false) Member member) {
        model.addAttribute("member", member);

        Board board = boardService.findBoard(boardId);
        model.addAttribute("form", new RequestBoardForm(board.getTitle(), board.getDescription(), null));
        return "board/edit-form";
    }

    @PutMapping("/edit/{boardId}")
    public String updateBoard(@PathVariable Long boardId, @Validated @ModelAttribute("form") RequestBoardForm form, BindingResult bindingResult,
                              @SessionAttribute(name = "member", required = false) Member member, Model model) {

        model.addAttribute("member", member);

        if (bindingResult.hasErrors()) {
            return "board/edit-form";
        }

        Board board = boardService.update(boardId, form, member);
        boardImageService.update(board, form);

        return "redirect:/boards";
    }

    @DeleteMapping("/delete/{boardId}")
    public String deleteBoard(@PathVariable Long boardId, @SessionAttribute(name = "member", required = false) Member member) {

        boardService.delete(boardId, member);
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
                           @SessionAttribute(name = "member", required = false) Member member) {


        Pageable pageable = PageRequest.of(page - 1, 10);  // 한 페이지에 10개의 게시글을 표시

        Page<Post> posts = postService.findPostsByBoardId(boardId, pageable);
        log.info("###########Posts: {}", posts);

        model.addAttribute("member", member);
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
