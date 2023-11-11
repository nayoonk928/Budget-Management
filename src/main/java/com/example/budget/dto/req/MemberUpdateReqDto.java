package com.example.budget.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberUpdateReqDto(
    @NotBlank(message = "닉네임은 필수 항목 입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해 주세요.")
    String nickname,
    Boolean notification
) {

}
