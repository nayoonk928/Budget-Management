package com.example.budget.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.budget.controller.common.ControllerTest;
import com.example.budget.dto.req.MemberSignUpReqDto;
import com.example.budget.dto.req.MemberUpdateReqDto;
import com.example.budget.entity.Member;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class MemberControllerTest extends ControllerTest {

  @Autowired
  private MemberRepository memberRepository;

  @Nested
  @DisplayName("회원가입")
  class signUp {

    @Test
    @DisplayName("성공")
    void success() throws Exception {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account2", "password1!", "member2", true);

      //when & then
      mockMvc.perform(post("/api/members")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 중복되는 계정")
    void fail_duplicated_account() throws Exception {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account1", "password1!", "member1", true);

      //when & then
      mockMvc.perform(post("/api/members")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.ALREADY_EXISTS_ACCOUNT.name()))
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 중복되는 닉네임")
    void fail_duplicated_nickname() throws Exception {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account", "password1!", "member1", true);

      //when & then
      mockMvc.perform(post("/api/members")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.ALREADY_EXISTS_NICKNAME.name()))
          .andDo(print());
    }

    @Test
    @DisplayName("실패: 비밀번호 형식 오류")
    void fail_invalid_password() throws Exception {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account1", "password1", "member1", true);

      //when & then
      mockMvc.perform(post("/api/members")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.error_code").value(ErrorCode.INVALID_REQUEST.name()))
          .andDo(print());
    }

    @Test
    @DisplayName("사용자 정보 조회")
    void success_get_member_detail() throws Exception {
      //when & then
      mockMvc.perform(get("/api/members")
              .header("Authorization", "Bearer " + accessToken))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.account").value(memberAccount))
          .andExpect(jsonPath("$.nickname").value(memberNickname))
          .andExpect(jsonPath("$.notification").value(memberNotification))
          .andDo(print());
    }

    @Nested
    @DisplayName("사용자 정보 수정")
    class update_member_detail {

      @Test
      @DisplayName("성공")
      void success() throws Exception {
        //given
        MemberUpdateReqDto request = new MemberUpdateReqDto("수정닉네임", null);

        //when & then
        mockMvc.perform(put("/api/members")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.account").value(memberAccount))
            .andExpect(jsonPath("$.nickname").value("수정닉네임"))
            .andExpect(jsonPath("$.notification").value(false))
            .andDo(print());
      }

      @Test
      @DisplayName("실패: 닉네임 입력 값 없음")
      void fail_no_nickname() throws Exception {
        //given
        MemberUpdateReqDto request = new MemberUpdateReqDto("", false);

        //when & then
        mockMvc.perform(put("/api/members")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error_code").value(ErrorCode.INVALID_REQUEST.name()))
            .andDo(print());
      }

    }

  }

}