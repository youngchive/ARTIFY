package com.elice.artBoard.member.service;

import com.elice.artBoard.board.entity.Board;
import com.elice.artBoard.board.repository.BoardRepository;
import com.elice.artBoard.board.service.BoardService;
import com.elice.artBoard.member.entity.MemberCheck;
import com.elice.artBoard.member.exception.MemberNotFoundException;
import com.elice.artBoard.member.repository.MemberRepository;
import com.elice.artBoard.member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    @Autowired
    public MemberService(MemberRepository memberRepository, BoardRepository boardRepository, BoardService boardService) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.boardService = boardService;
    }

    // 전체 회원 목록
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    // 특정 회원 조회
    public Member findMember(Integer memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("해당 회원이 없습니다."));
    }

    // DB에 로그인 할 회원이 있는지 조회
    public Member checkMember(Member member) throws MemberNotFoundException {
        if (memberRepository.check(member).isEmpty()) {
            throw new MemberNotFoundException("로그인 정보가 틀립니다.");
        } else {
            return memberRepository.check(member).orElseThrow(() ->  new RuntimeException("서버 내부 오류로 나중에 다시 로그인해주세요."));
        }
    }

    // 중복 회원 있는지
    public void checkDuplicate(MemberCheck memberCheck, BindingResult result) {
        // 이메일이 존재 하면
        if (memberRepository.findByEmail(memberCheck.getEmail()).isPresent()) {
            result.addError(new FieldError(memberCheck.getObjectName(), "email", "이미 존재하는 회원입니다."));
        }
    }

    // 새로운 회원 저장
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    // 기존 회원 프로필 수정
    public Member updateMember(Member member) {
        Member findMember = memberRepository.findById(member.getMemberId())
                .orElseThrow(() -> new RuntimeException("수정할 회원이 없습니다."));

        Optional.ofNullable(member.getName())
                .ifPresent(name -> findMember.setName(name));
        Optional.ofNullable(member.getEmail())
                .ifPresent(email -> findMember.setEmail(email));
        Optional.ofNullable(member.getPassword())
                .ifPresent(password -> findMember.setPassword(password));
        Optional.ofNullable(member.getNickname())
                .ifPresent(nickname -> findMember.setNickname(nickname));

        return memberRepository.save(findMember);
    }

    // 회원 탈퇴 -> 정말 탈퇴할 것인지 다시 묻기
    public void deleteMember(Integer memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("삭제할 회원이 없습니다."));

        memberRepository.delete(findMember);
    }

    // 회원 탈퇴 전 작성한 게시판 모두 지우기
    public void deleteAllBoards(Integer memberId) {
        Member member = findMember(memberId);
        List<Board> boards = boardRepository.findByMember(member);

        if(!boards.isEmpty()) {
            for (Board board : boards) {
                boardService.delete(board.getId(), member);
            }
        }
    }
}
