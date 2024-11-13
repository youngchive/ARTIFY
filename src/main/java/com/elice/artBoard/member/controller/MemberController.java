package com.elice.artBoard.member.controller;

import com.elice.artBoard.member.entity.MemberCheck;
import com.elice.artBoard.member.mapper.MemberMapper;
import com.elice.artBoard.member.service.MemberService;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.member.entity.MemberPostDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper memberMapper;

    @Autowired
    public MemberController(MemberService memberService, MemberMapper memberMapper) {
        this.memberService = memberService;
        this.memberMapper = memberMapper;
    }

    // 초기 화면 주소
    @GetMapping("/")
    public String home() {
        return "home";
    }

    // 로그인 화면
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(MemberPostDto memberPostDto, Model model, HttpServletRequest httpServletRequest) {
        Member member = memberMapper.MemberPostDtoToMember(memberPostDto);

        try {
            Member result = memberService.checkMember(member);

            // 로그인 성공 시
            // 사용자 정보 세션에 저장
            httpServletRequest.getSession().invalidate(); // 세션을 생성하기 전 기존 세션 파기
            HttpSession session = httpServletRequest.getSession(true); // 세션이 없으면 새로 생성
            // 세션에 로그인 회원 저장
            session.setAttribute("member", result);
            session.setMaxInactiveInterval(60 * 30); // 세션 30분동안 유지

        } catch (RuntimeException e) { // 로그인 실패 시
            model.addAttribute("msg", e.getMessage());

            return "member/login";
        }

        return "redirect:/boards";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 세션이 없으면 null 리턴

        if(session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }

    // 회원 가입 화면
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("memberCreate", new MemberPostDto());
        return "member/create";
    }

    // 회원 가입 처리
    @PostMapping("/create")
    public String signUp(@Validated @ModelAttribute("memberCreate") MemberPostDto memberPostDto, BindingResult result) {
        memberService.checkDuplicate(new MemberCheck(memberPostDto), result);

        if (result.hasErrors()) {
            return "member/create";
        }

        Member member = memberMapper.MemberPostDtoToMember(memberPostDto);
        memberService.createMember(member);

        return "redirect:/login";
    }

    @GetMapping("/{memberId}")
    public String detail(Model model, @PathVariable Integer memberId) {
        Member member = memberService.findMember(memberId);

        model.addAttribute("loginMember", member);

        return "member/profile";
    }

    @GetMapping("/edit/{memberId}")
    public String update(Model model, @PathVariable Integer memberId) {
        Member member = memberService.findMember(memberId);

        model.addAttribute("findMember", member);
        model.addAttribute("memberId", memberId);

        return "member/update";
    }

    @PutMapping("/edit/{memberId}")
    public String update(@PathVariable Integer memberId,
                         @ModelAttribute("findMember") @Validated MemberPostDto memberPostDto, BindingResult result, Model model,
                         HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            model.addAttribute("memberId", memberId);

            return "member/update";
        }

        Member member = memberMapper.MemberPostDtoToMember(memberPostDto);
        member.setMemberId(memberId);

        memberService.updateMember(member);

        httpServletRequest.getSession().invalidate(); // 수정 성공 시 기존 세션 폐기
        HttpSession session = httpServletRequest.getSession(true); // 세션이 없으면 새로 생성
        // 세션에 수정된 회원 저장
        session.setAttribute("member", member);
        session.setMaxInactiveInterval(60 * 30); // 세션 30분동안 유지

        return "redirect:/" + memberId;
    }

    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Integer memberId) {
        memberService.deleteMember(memberId);

        return "redirect:/logout";
    }
}
