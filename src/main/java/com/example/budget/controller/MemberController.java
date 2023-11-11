package com.example.budget.controller;

import com.example.budget.dto.req.MemberLoginDto;
import com.example.budget.dto.req.MemberSignUpDto;
import com.example.budget.dto.req.MemberUpdateReqDto;
import com.example.budget.dto.res.MemberDetailResDto;
import com.example.budget.dto.res.TokenDto;
import com.example.budget.entity.Member;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

  private final MemberService memberService;

  @PostMapping
  public ResponseEntity<Long> signup(
      @Valid @RequestBody MemberSignUpDto request
  ) {
    return ResponseEntity.ok().body(memberService.signUp(request));
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(
      @Valid @RequestBody MemberLoginDto request
  ) {
    String response = memberService.login(request);
    return ResponseEntity.ok().body(new TokenDto(response));
  }

  @GetMapping
  public ResponseEntity<MemberDetailResDto> getMemberDetail(
      @AuthenticationPrincipal Member member
  ) {
    MemberDetailResDto memberDetailResDto = MemberDetailResDto.builder()
        .account(member.getAccount())
        .nickname(member.getNickname())
        .notification(member.getNotification())
        .build();

    return ResponseEntity.ok().body(memberDetailResDto);
  }

  @PutMapping
  public ResponseEntity<MemberDetailResDto> updateMemberInfo(
      @AuthenticationPrincipal Member member,
      @Valid @RequestBody MemberUpdateReqDto request
  ) {
    return ResponseEntity.ok().body(memberService.updateMemberInfo(member, request));
  }

}
