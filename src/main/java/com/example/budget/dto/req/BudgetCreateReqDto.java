package com.example.budget.dto.req;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record BudgetCreateReqDto(
    @NotEmpty
    List<BudgetDto> budgets,
    Integer totalAmount
) {

  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record BudgetDto(
      Long categoryId,
      Integer amount
  ) {

  }

}
