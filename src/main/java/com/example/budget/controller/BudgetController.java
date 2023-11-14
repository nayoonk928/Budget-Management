package com.example.budget.controller;

import com.example.budget.dto.req.BudgetCreateReqDto;
import com.example.budget.dto.res.BudgetsResDto;
import com.example.budget.entity.Member;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/budgets")
public class BudgetController {

  private final BudgetService budgetService;

  @PostMapping
  public ResponseEntity<BudgetsResDto> createBudget(
      @AuthenticationPrincipal Member member,
      @Valid @RequestBody BudgetCreateReqDto request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED).body(budgetService.createBudget(member, request));
  }

  @GetMapping
  public ResponseEntity<BudgetsResDto> getBudget(
      @AuthenticationPrincipal Member member
  ) {
    return ResponseEntity.ok().body(budgetService.getBudgets(member));
  }

  @GetMapping("/recommend")
  public ResponseEntity<BudgetsResDto> recommendBudget(
      @AuthenticationPrincipal Member member,
      @RequestParam(value = "total_amount")Integer totalAmount
  ) {
    return ResponseEntity.ok().body(budgetService.recommendBudget(totalAmount));
  }

}
