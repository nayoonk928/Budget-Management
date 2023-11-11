package com.example.budget.dto.res;

import lombok.Builder;

@Builder
public record MemberDetailResDto(
    String account,
    String nickname,
    Boolean notification
) {

}
