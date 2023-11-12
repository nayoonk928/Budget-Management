package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BudgetsResDto(
    List<BudgetDto> budgets,
    BigDecimal totalAmount
) {

  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record BudgetDto(
      Long categoryId,
      String categoryName,
      BigDecimal amount
  ) {

  }

}
