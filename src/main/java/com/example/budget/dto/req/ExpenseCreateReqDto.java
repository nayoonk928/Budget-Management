package com.example.budget.dto.req;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExpenseCreateReqDto(
    @NotNull(message = "지출일을 입력해주세요")
    Date expendedAt,
    @NotNull(message = "금액을 입력해주세요")
    @Min(value = 0, message = "지출 금액은 0원 이상으로 입력해주세요.")
    Integer amount,
    @NotNull(message = "카테고리를 입력해주세요")
    String category,
    Boolean isExcludedSum,
    String description
) {

}
