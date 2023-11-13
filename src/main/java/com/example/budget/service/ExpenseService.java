package com.example.budget.service;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.dto.res.ExpensesResDto;
import com.example.budget.entity.Member;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {

  ExpenseDetailResDto createExpense(Member member, ExpenseCreateReqDto request);

  ExpenseDetailResDto updateExpense(Member member, Long expenseId, ExpenseCreateReqDto request);

  ExpenseDetailResDto getExpenseDetail(Member member, Long expenseId);

  ExpensesResDto getExpenses(Member member, LocalDate startDate, LocalDate endDate,
      Integer minAmount, Integer maxAmount, String categoryType, Pageable pageable);

}
