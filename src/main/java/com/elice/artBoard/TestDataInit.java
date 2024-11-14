package com.elice.artBoard;

import com.elice.artBoard.board.entity.Board;
import com.elice.artBoard.board.entity.BoardImage;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.post.entity.Post;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final InitService initService;


    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {

            Member member = createMember("testA@n.com", "이름A", "1234", "testerA");
            em.persist(member);

            IntStream.range(1, 6)
                    .forEach(i -> {
                        Board board = Board.create("testBoard"+ i, "testBoard" + i, member);
                        BoardImage boardImage = BoardImage.createDefaultImage(board);

                        IntStream.range(1, 16)
                                .forEach(j -> {
                                    Post post = Post.create("testPost" + j, "tesPost" + j, board, member);
                                    em.persist(post);
                                });
                        em.persist(boardImage);
                        em.persist(board);
                    });
        }

        public void dbInit2() {
            Member member = createMember("testB@n.com", "이름B", "1234", "testerB");
            em.persist(member);

            IntStream.range(6, 11)
                    .forEach(i -> {
                        Board board = Board.create("testBoard"+ i, "testBoard" + i, member);
                        BoardImage boardImage = BoardImage.createDefaultImage(board);

                        IntStream.range(1, 16)
                                .forEach(j -> {
                                    Post post = Post.create("testPost" + j, "tesPost" +j, board, member);
                                    em.persist(post);
                                });

                        em.persist(boardImage);
                        em.persist(board);
                    });
        }

        private Member createMember(String mail, String name, String password, String nickname) {
            Member memberA = new Member();
            memberA.setName(name);
            memberA.setEmail(mail);
            memberA.setPassword(password);
            memberA.setNickname(nickname);

            return memberA;
        }

    }
}
