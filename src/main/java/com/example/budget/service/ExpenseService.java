package com.example.budget.service;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.entity.Member;

public interface ExpenseService {

  void createExpense(Member member, ExpenseCreateReqDto request);

  ExpenseDetailResDto getExpenseDetail(Long expenseId);

}
