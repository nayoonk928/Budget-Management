package com.example.budget.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MemberSignUpDto(
    @NotBlank(message = "계정은 필수 항목 입니다.")
    String account,
    @NotBlank(message = "비밀번호는 필수 항목 입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$",
        message = "비밀번호는 알파벳, 숫자, 특수문자를 각각 하나 이상 포함하여 6자 이상으로 설정해주세요.")
    String password,
    @NotBlank(message = "닉네임은 필수 항목 입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해 주세요.")
    String nickname,
    Boolean notification
){

}
