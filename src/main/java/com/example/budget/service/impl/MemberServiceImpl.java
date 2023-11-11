package com.example.budget.service.impl;

import com.example.budget.dto.req.MemberLoginDto;
import com.example.budget.dto.req.MemberSignUpDto;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.MemberRepository;
import com.example.budget.security.JwtUtil;
import com.example.budget.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Override
  public Long signUp(MemberSignUpDto request) {
    String account = request.account();
    String nickname = request.nickname();

    if (memberRepository.existsByAccount(account)) {
      throw new CustomException(ErrorCode.ALREADY_EXISTS_ACCOUNT);
    }

    if (memberRepository.existsByNickname(nickname)) {
      throw new CustomException(ErrorCode.ALREADY_EXISTS_NICKNAME);
    }

    String encryptPassword = passwordEncoder.encode(request.password());

    Member member = Member.builder()
        .account(account)
        .nickname(nickname)
        .password(encryptPassword)
        .notification(request.notification())
        .build();

    memberRepository.save(member);

    return member.getId();
  }

  @Override
  public String login(MemberLoginDto request) {
    Member member = memberRepository.findByAccount(request.account())
        .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_ACCOUNT_OR_PASSWORD));

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new CustomException(ErrorCode.INCORRECT_ACCOUNT_OR_PASSWORD);
    }

    return jwtUtil.createToken(member.getId());
  }

}
