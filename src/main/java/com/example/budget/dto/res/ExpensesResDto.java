package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ExpensesResDto(
    Integer totalAmount,
    List<CategoryResDto> categoryAmounts,
    List<ExpenseResDto> expenses
) {

  public record CategoryResDto(
      String category,
      Integer amount
  ) {
  }

  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record ExpenseResDto(
      Long expenseId,
      String expendedAt,
      Integer amount,
      String category,
      String description
  ) {
  }

}
