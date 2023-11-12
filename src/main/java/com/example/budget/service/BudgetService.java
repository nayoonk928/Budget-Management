package com.example.budget.service;

import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.res.BudgetsResDto;
import com.example.budget.entity.Member;
import java.math.BigDecimal;

public interface BudgetService {

  BudgetsResDto createBudget(Member member, BudgetCreateReqDto request);

  BudgetsResDto getBudgets(Member member);

  BudgetsResDto recommendBudget(BigDecimal totalAmount);

}
