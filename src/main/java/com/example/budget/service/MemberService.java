package com.example.budget.service;

import com.example.budget.dto.req.MemberLoginDto;
import com.example.budget.dto.req.MemberSignUpDto;
import com.example.budget.dto.res.TokenDto;

public interface MemberService {

  Long signUp(MemberSignUpDto request);

  String login(MemberLoginDto request);

}
