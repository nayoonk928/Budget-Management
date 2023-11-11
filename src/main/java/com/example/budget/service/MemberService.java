package com.example.budget.service;

import com.example.budget.dto.req.MemberLoginReqDto;
import com.example.budget.dto.req.MemberSignUpReqDto;
import com.example.budget.dto.req.MemberUpdateReqDto;
import com.example.budget.dto.res.MemberDetailResDto;
import com.example.budget.entity.Member;

public interface MemberService {

  Long signUp(MemberSignUpReqDto request);

  String login(MemberLoginReqDto request);

  MemberDetailResDto updateMemberInfo(Member member, MemberUpdateReqDto request);

}
