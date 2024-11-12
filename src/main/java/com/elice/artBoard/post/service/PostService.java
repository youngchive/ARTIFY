package com.elice.artBoard.post.service;

import com.elice.artBoard.board.domain.Board;
import com.elice.artBoard.board.repository.BoardRepository;
import com.elice.artBoard.comment.repository.CommentRepository;
import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.entity.PostPostDto;
import com.elice.artBoard.post.repository.PostRepository;
import com.elice.artBoard.post.service.PostImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostImageService postImageService;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 모든 게시글 조회
    public List<Post> findPostsByBoardId(Long boardId) {
        return postRepository.findByBoardId(boardId);  // Board와 연결된 게시글 조회
    }

    // 특정 게시글 조회
    public Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    //board와 연관된 게시글 조회
    public Post getPostByBoardId(Long boardId) {
        return postRepository.findOneByBoardId(boardId);
    }

    // 게시글 저장
    public Post save(PostPostDto postPostDto, Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        Post post = Post.create(postPostDto.getTitle(), postPostDto.getContent(), board);
        return postRepository.save(post);
    }

    public Post update(Long postId, PostPostDto postPostDto) {
        Post findPost = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        return findPost.update(postPostDto.getTitle(), postPostDto.getContent());
    }

    // 특정 게시글 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

        //댓글에 게시글 fk가 있음으로 댓글 먼저 삭제
        commentRepository.deleteByPostId(postId);

        // 관련 이미지 삭제
        postImageService.delete(postId);

        postRepository.delete(post);
    }

    public PostPostDto getPostPostDto(Long postId) {
        Post post = getPost(postId);  // 게시글 조회
        Long boardId = post.getBoard().getId();  // 게시판 ID 추출
        return new PostPostDto(post.getTitle(), post.getContent(), boardId);
    }



}
