package com.example.budget.service;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.entity.Member;

public interface ExpenseService {

  ExpenseDetailResDto createExpense(Member member, ExpenseCreateReqDto request);

  ExpenseDetailResDto updateExpense(Member member, Long expenseId, ExpenseCreateReqDto request);

  ExpenseDetailResDto getExpenseDetail(Member member, Long expenseId);

}
