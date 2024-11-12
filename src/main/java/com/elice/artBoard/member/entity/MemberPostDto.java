package com.elice.artBoard.member.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class MemberPostDto {
    @NotBlank(message = "이름을 입력해 주세요")
    private String name;

    @NotBlank(message = "이메일을 입력해 주세요")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$",
            message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "닉네임을 입력해 주세요")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해 주세요")
    @Size(min = 8, max = 15, message = "8자 이상 15자 이하로 작성해 주세요")
    private String password;

    // 회원 생성, 수정 시간 ?

    // 로그인 데이터를 받는 객체
    public MemberPostDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
