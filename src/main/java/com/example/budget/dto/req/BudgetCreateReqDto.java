package com.example.budget.dto.req;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BudgetCreateReqDto(
    @NotEmpty
    List<BudgetDto> budgets,
    BigDecimal totalAmount
) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record BudgetDto(
      Long categoryId,
      BigDecimal amount
  ) {

  }

}
