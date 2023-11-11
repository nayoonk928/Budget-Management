package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTestNoAuth;
import com.example.budget.dto.req.MemberLoginReqDto;
import com.example.budget.entity.Member;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class LoginControllerTest extends ControllerTestNoAuth {

  @Autowired
  private MemberRepository memberRepository;

  @Nested
  @DisplayName("로그인")
  class login {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      Member member = Member.builder()
          .account("account1")
          .password("$2a$10$VjSR9HyMuosOneqcEysAweOITjRuppgrsG9nR6fdGm/jDKLKJ51zK")
          .nickname("member1")
          .build();
      memberRepository.save(member);

      MemberLoginReqDto request = new MemberLoginReqDto("account1", "password1!");

      //when & then
      mockMvc.perform(post("/api/members/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 올바르지 않은 비밀번호")
    void fail_login_invalid_password() throws Exception {
      //given
      Member member = Member.builder()
          .account("account1")
          .password("$2a$10$VjSR9HyMuosOneqcEysAweOITjRuppgrsG9nR6fdGm/jDKLKJ51zK")
          .nickname("member1")
          .build();
      memberRepository.save(member);

      MemberLoginReqDto request = new MemberLoginReqDto("account1", "wrongPassword");

      //when & then
      mockMvc.perform(post("/api/members/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.INCORRECT_ACCOUNT_OR_PASSWORD.name()))
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 계정")
    void fail_login_invalid_account() throws Exception {
      //given
      Member member = Member.builder()
          .account("account1")
          .password("$2a$10$VjSR9HyMuosOneqcEysAweOITjRuppgrsG9nR6fdGm/jDKLKJ51zK")
          .nickname("member1")
          .build();
      memberRepository.save(member);

      MemberLoginReqDto request = new MemberLoginReqDto("account2", "password1!");

      //when & then
      mockMvc.perform(post("/api/members/login")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.INCORRECT_ACCOUNT_OR_PASSWORD.name()))
          .andDo(print());
    }

  }

}
