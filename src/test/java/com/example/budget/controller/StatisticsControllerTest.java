package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.config.ExpenseDataInit;
import com.example.budget.controller.common.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class StatisticsControllerTest extends ControllerTest {

  @Autowired
  private ExpenseDataInit expenseDataInit;

  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
  }

  @Nested
  @DisplayName("지출 통계")
  class statistics {

    @Test
    @DisplayName("성공: 월 대비 통계")
    void compare_with_last_month() throws Exception {
      //given
      expenseDataInit.generateTestData(member, 1000);

      //when & then
      mockMvc.perform(get("/api/statistics/month")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.last_expense").isNumber())
          .andExpect(jsonPath("$.current_expense").isNumber())
          .andExpect(jsonPath("$.increase_rate").isNumber())
          .andExpect(jsonPath("$.categories").isArray())
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("성공: 요일 대비 통계")
    void compare_with_last_weekday() throws Exception {
      //given
      expenseDataInit.generateTestData(member, 1000);

      //when & then
      mockMvc.perform(get("/api/statistics/weekday")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.last_expense").isNumber())
          .andExpect(jsonPath("$.current_expense").isNumber())
          .andExpect(jsonPath("$.increase_rate").isNumber())
          .andExpect(jsonPath("$.categories").isArray())
          .andDo(print())
          .andReturn();
    }

  }

}