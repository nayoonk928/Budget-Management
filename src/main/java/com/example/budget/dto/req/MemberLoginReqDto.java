package com.example.budget.dto.req;

import jakarta.validation.constraints.NotBlank;

public record MemberLoginReqDto(
    @NotBlank(message = "계정은 필수값입니다.")
    String account,
    @NotBlank(message = "비밀번호는 필수값입니다.")
   String password
) {

}
