package com.example.budget.dto.res;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record DailyReportDto(
    Integer recommendExpense,
    Integer spentExpense,
    Integer risk,
    List<CategoryResDto> categories
) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record CategoryResDto(
      String category,
      Integer expense,
      Integer risk
  ) {

  }

}
