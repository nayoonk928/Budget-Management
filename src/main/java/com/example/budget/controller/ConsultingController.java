package com.example.budget.controller;

import com.example.budget.dto.res.ExpenseRecommendDto;
import com.example.budget.entity.Member;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.ConsultingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses/today")
public class ConsultingController {

  private final ConsultingService consultingService;

  @GetMapping("/recommend")
  public ResponseEntity<ExpenseRecommendDto> recommendDailyExpense(
      @AuthenticationPrincipal Member member
  ) {
    return ResponseEntity.ok().body(consultingService.recommendDailyExpense(member));
  }

}
