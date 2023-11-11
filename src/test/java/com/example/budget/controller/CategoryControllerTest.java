package com.example.budget.controller;

import com.example.budget.controller.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CategoryControllerTest extends ControllerTest {

  @Test
  @DisplayName("카테고리 목록 조회 - 성공")
  void get_categories() throws Exception {
    //when & then
    mockMvc.perform(get("/api/categories"))
        .andExpect(status().isOk())
        .andDo(print());
  }

}