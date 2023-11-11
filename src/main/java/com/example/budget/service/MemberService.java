package com.example.budget.service;

import com.example.budget.dto.req.MemberLoginDto;
import com.example.budget.dto.req.MemberSignUpDto;
import com.example.budget.dto.req.MemberUpdateReqDto;
import com.example.budget.dto.res.MemberDetailResDto;
import com.example.budget.entity.Member;

public interface MemberService {

  Long signUp(MemberSignUpDto request);

  String login(MemberLoginDto request);

  MemberDetailResDto updateMemberInfo(Member member, MemberUpdateReqDto request);

}
