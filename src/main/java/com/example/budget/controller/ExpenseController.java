package com.example.budget.controller;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.entity.Member;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  @PostMapping
  public ResponseEntity<Void> createExpense(
      @AuthenticationPrincipal Member member,
      @Valid @RequestBody ExpenseCreateReqDto request
  ) {
    expenseService.createExpense(member, request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/{expenseId}")
  public ResponseEntity<ExpenseDetailResDto> getExpenseDetail(
      @AuthenticationPrincipal Member member,
      @PathVariable Long expenseId
  ) {
    return ResponseEntity.ok().body(expenseService.getExpenseDetail(expenseId));
  }

}
