package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.service.impl.ExpenseServiceImpl;
import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class ExpenseControllerTest extends ControllerTest {

  @Autowired
  private ExpenseRepository expenseRepository;

  @Autowired
  private ExpenseServiceImpl budgetService;

  @Nested
  @DisplayName("지출 추가")
  class create_expense {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto request = ExpenseCreateReqDto.builder()
          .expendedAt(new Date(2023, 11, 13))
          .amount(10000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      //when & then
      mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("지출 추가 - 음수 금액")
    void fail_negative_amount() throws Exception {
      // given
      ExpenseCreateReqDto request = ExpenseCreateReqDto.builder()
          .expendedAt(new Date(2023, 11, 13))
          .amount(-10)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      // when & then
      MvcResult result = mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.INVALID_REQUEST.name()))
          .andDo(print())
          .andReturn();
    }

    @Test
    @DisplayName("지출 추가 - 잘못된 카테고리")
    void fail_invalid_category() throws Exception {
      // given
      ExpenseCreateReqDto request = ExpenseCreateReqDto.builder()
          .expendedAt(new Date(2023, 11, 13))
          .amount(10000)
          .category("자동차")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      // when & then
      mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.CATEGORY_NOT_FOUND.name()))
          .andDo(print());
    }

  }

}