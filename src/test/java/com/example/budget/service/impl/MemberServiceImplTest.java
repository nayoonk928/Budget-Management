package com.example.budget.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.budget.dto.req.MemberLoginReqDto;
import com.example.budget.dto.req.MemberSignUpReqDto;
import com.example.budget.dto.req.MemberUpdateReqDto;
import com.example.budget.dto.res.MemberDetailResDto;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.MemberRepository;
import com.example.budget.security.JwtUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

  @InjectMocks
  private MemberServiceImpl memberService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtUtil jwtUtil;

  @Nested
  @DisplayName("회원가입")
  class signUp {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account1", "password1!", "nickname1", true);

      //when
      memberService.signUp(request);

      //then
      verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    @DisplayName("실패: 중복되는 계정")
    void fail_duplicated_account() {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account1", "password1!", "nickname1", true);

      when(memberRepository.existsByAccount(request.account())).thenReturn(true);

      //when
      CustomException exception = assertThrows(CustomException.class, () -> memberService.signUp(request));

      //then
      assertEquals(ErrorCode.ALREADY_EXISTS_ACCOUNT, exception.getErrorCode());
    }

    @Test
    @DisplayName("실패: 중복되는 닉네임")
    void fail_duplicated_nickname() {
      //given
      MemberSignUpReqDto request = new MemberSignUpReqDto("account1", "password1!", "nickname1", true);

      when(memberRepository.existsByNickname(request.nickname())).thenReturn(true);

      //when
      CustomException exception = assertThrows(CustomException.class, () -> memberService.signUp(request));

      //then
      assertEquals(ErrorCode.ALREADY_EXISTS_NICKNAME, exception.getErrorCode());
    }

  }

  @Nested
  @DisplayName("로그인")
  class login {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Member member = new Member(1L, "account1", passwordEncoder.encode("password1!"), "nickname1", true);

      MemberLoginReqDto request = new MemberLoginReqDto("account1", "password1!");

      when(memberRepository.findByAccount(request.account())).thenReturn(Optional.of(member));
      when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(true);

      //when
      memberService.login(request);

      //then
      verify(jwtUtil, times(1)).createToken(member.getId());
    }

    @Test
    @DisplayName("실패: 계정 또는 비밀번호 불일치")
    void fail_incorrect_account_or_password() {
      //given
      Member member = new Member(1L, "account1", passwordEncoder.encode("password1!"), "nickname1", true);

      MemberLoginReqDto request = new MemberLoginReqDto("account2", "password2!");

      when(memberRepository.findByAccount(request.account())).thenReturn(Optional.of(member));
      when(passwordEncoder.matches(request.password(), member.getPassword())).thenReturn(false);

      //when
      CustomException exception = assertThrows(CustomException.class,
          () -> memberService.login(request));

      //then
      assertEquals(ErrorCode.INCORRECT_ACCOUNT_OR_PASSWORD, exception.getErrorCode());
    }

  }

  @Nested
  @DisplayName("사용자 정보 수정")
  class updateMemberInfo {

    @Test
    @DisplayName("성공")
    void success() {
      //given
      Member member = new Member(1L, "account1", passwordEncoder.encode("password1!"), "nickname1", true);
      MemberUpdateReqDto request = new MemberUpdateReqDto("newNickname", false);

      //when
      MemberDetailResDto result = memberService.updateMemberInfo(member, request);

      //then
      assertEquals("account1", result.account());
      assertEquals("newNickname", result.nickname());
      assertThat(result.notification()).isFalse();

      assertEquals("newNickname", member.getNickname());
      assertFalse(member.getNotification());
    }

  }

}