package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExpenseDetailResDto(
    Long expenseId,
    String expendedAt,
    Integer amount,
    String category,
    Boolean isExcludedSum,
    String description
) {

}
