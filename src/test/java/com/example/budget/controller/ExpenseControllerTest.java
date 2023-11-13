package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.exception.ErrorCode;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

class ExpenseControllerTest extends ControllerTest {

  @Nested
  @DisplayName("지출 추가")
  class create_expense {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto request = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
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
          .expendedAt(LocalDate.of(2023, 11, 13))
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
          .expendedAt(LocalDate.of(2023, 11, 13))
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

  @Nested
  @DisplayName("지출 수정")
  class update_expense {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto createDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(10000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      MvcResult result = mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(createDto)))
          .andExpect(status().isCreated())
          .andDo(print())
          .andReturn();

      String resultContent = result.getResponse().getContentAsString();
      JsonNode resultJson = objectMapper.readTree(resultContent);
      long expenseId = resultJson.get("expense_id").asLong();

      ExpenseCreateReqDto updateDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(11000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      //when & then
      mockMvc.perform(put("/api/expenses/{expenseId}", expenseId)
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.expended_at").value("2023-11-13"))
          .andExpect(jsonPath("$.amount").value(11000))
          .andExpect(jsonPath("$.description").value("서브웨이 먹음"))
          .andExpect(jsonPath("$.category").value("식비"))
          .andExpect(jsonPath("$.is_excluded_sum").value(false))
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 지출 존재하지 않음")
    void fail_invalid_expense_id() throws Exception {
      //given
      ExpenseCreateReqDto updateDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(11000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      //when & then
      mockMvc.perform(put("/api/expenses/{expenseId}", 1000)
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.EXPENSE_NOT_FOUND.name()))
          .andDo(print());
    }

  }

  @Nested
  @DisplayName("지출 상세 조회")
  class get_expense_detail {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto createDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(10000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      MvcResult result = mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(createDto)))
          .andExpect(status().isCreated())
          .andDo(print())
          .andReturn();

      String resultContent = result.getResponse().getContentAsString();
      JsonNode resultJson = objectMapper.readTree(resultContent);
      long expenseId = resultJson.get("expense_id").asLong();

      //when & then
      mockMvc.perform(get("/api/expenses/{expenseId}", expenseId)
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.expended_at").value("2023-11-13"))
          .andExpect(jsonPath("$.amount").value(10000))
          .andExpect(jsonPath("$.description").value("서브웨이 먹음"))
          .andExpect(jsonPath("$.category").value("식비"))
          .andExpect(jsonPath("$.is_excluded_sum").value(false))
          .andDo(print());
    }

  }

  @Nested
  @DisplayName("지출 목록 조회")
  class get_expenses {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto createDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(10000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(createDto)))
          .andExpect(status().isCreated())
          .andDo(print())
          .andReturn();

      //when & then
      mockMvc.perform(get("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andDo(print())
          .andReturn();
    }

  }

  @Nested
  @DisplayName("지출 삭제")
  class delete_expense {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      ExpenseCreateReqDto createDto = ExpenseCreateReqDto.builder()
          .expendedAt(LocalDate.of(2023, 11, 13))
          .amount(10000)
          .category("식비")
          .isExcludedSum(false)
          .description("서브웨이 먹음")
          .build();

      MvcResult result = mockMvc.perform(post("/api/expenses")
              .header("Authorization", "Bearer " + accessToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(createDto)))
          .andExpect(status().isCreated())
          .andDo(print())
          .andReturn();

      String resultContent = result.getResponse().getContentAsString();
      JsonNode resultJson = objectMapper.readTree(resultContent);
      long expenseId = resultJson.get("expense_id").asLong();

      //when & then
      mockMvc.perform(delete("/api/expenses/{expenseId}", expenseId)
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isNoContent())
          .andDo(print());
    }

  }

}