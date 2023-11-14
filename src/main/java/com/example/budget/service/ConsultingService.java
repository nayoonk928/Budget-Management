package com.example.budget.service;

import com.example.budget.dto.res.ExpenseRecommendDto;
import com.example.budget.entity.Member;

public interface ConsultingService {

  ExpenseRecommendDto recommendDailyExpense(Member member);

}
