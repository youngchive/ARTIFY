package com.elice.artBoard.member.controller;

import com.elice.artBoard.member.entity.MemberCheck;
import com.elice.artBoard.member.exception.MemberNotFoundException;
import com.elice.artBoard.member.mapper.MemberMapper;
import com.elice.artBoard.member.service.MemberService;
import com.elice.artBoard.member.entity.Member;
import com.elice.artBoard.member.entity.MemberPostDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public String login(MemberPostDto memberPostDto, Model model) {
        Member member = memberMapper.MemberPostDtoToMember(memberPostDto);

        try {
            Member result = memberService.checkMember(member);

            model.addAttribute("loginMember", result);
        } catch (MemberNotFoundException e) {
            model.addAttribute("msg", e.getMessage());

            return "member/login";
        }

        return "redirect:/boards";
    }

    // 회원 가입 화면
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("memberCreate", new MemberPostDto());
        return "member/create";
    }

    // 회원 가입 처리
    @PostMapping("/create")
    public String signUp(@Validated @ModelAttribute("memberCreate") MemberPostDto memberPostDto, BindingResult result, Model model) {
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

        return "member/update";
    }

    @PutMapping("/edit/{memberId}")
    public String update(@PathVariable Integer memberId, MemberPostDto memberPostDto) {
        Member member = memberMapper.MemberPostDtoToMember(memberPostDto);
        member.setMemberId(memberId);

        memberService.updateMember(member);

        return "redirect:/" + memberId;
    }

    @DeleteMapping("/{memberId}")
    public String deleteMember(@PathVariable Integer memberId) {
        memberService.deleteMember(memberId);

        return "redirect:/";
    }
}
