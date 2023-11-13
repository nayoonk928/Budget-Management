package com.example.budget.controller;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.dto.res.ExpenseDetailResDto;
import com.example.budget.dto.res.ExpensesResDto;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.ExpenseService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  @PostMapping
  public ResponseEntity<ExpenseDetailResDto> createExpense(
      @AuthenticationPrincipal Member member,
      @Valid @RequestBody ExpenseCreateReqDto request
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(expenseService.createExpense(member, request));
  }

  @PutMapping("/{expenseId}")
  public ResponseEntity<ExpenseDetailResDto> edieExpense(
      @AuthenticationPrincipal Member member,
      @PathVariable Long expenseId,
      @Valid @RequestBody ExpenseCreateReqDto request
  ) {
    return ResponseEntity.ok().body(expenseService.updateExpense(member, expenseId, request));
  }

  @GetMapping("/{expenseId}")
  public ResponseEntity<ExpenseDetailResDto> getExpenseDetail(
      @AuthenticationPrincipal Member member,
      @PathVariable Long expenseId
  ) {
    return ResponseEntity.ok().body(expenseService.getExpenseDetail(member, expenseId));
  }

  @GetMapping
  public ResponseEntity<ExpensesResDto> getExpenses(
      @AuthenticationPrincipal Member member,
      @RequestParam(value = "start_date", required = false) LocalDate startDate,
      @RequestParam(value = "end_date", required = false) LocalDate endDate,
      @RequestParam(value = "min_amount", required = false, defaultValue = "0") Integer minAmount,
      @RequestParam(value = "max_amount", required = false, defaultValue = ""
          + Integer.MAX_VALUE) Integer maxAmount,
      @RequestParam(value = "category", required = false) String category,
      @RequestParam(value = "order_by", required = false, defaultValue = "expendedAt") String orderBy,
      @RequestParam(value = "sort_by", required = false, defaultValue = "desc") String sortBy
  ) {
    if (startDate == null) {
      startDate = LocalDate.now().withDayOfMonth(1);
    }

    if (endDate == null) {
      endDate = LocalDate.now();
    }

    if (startDate.isAfter(endDate)) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    if (maxAmount < minAmount) {
      throw new CustomException(ErrorCode.INVALID_REQUEST);
    }

    Sort.Direction direction =
        sortBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
    Sort sort = Sort.by(direction, orderBy);
    int size = 5; // 스크롤 1회에 조회할 데이터 개수
    PageRequest pageRequest = PageRequest.of(0, size + 1, sort);

    ExpensesResDto expenses = expenseService.getExpenses(member, startDate, endDate,
        minAmount, maxAmount, category, pageRequest);
    return ResponseEntity.ok().body(expenses);
  }

  @DeleteMapping("/{expenseId}")
  public ResponseEntity<Void> deleteExpense(
      @AuthenticationPrincipal Member member,
      @PathVariable Long expenseId
  ) {
    expenseService.deleteExpense(member, expenseId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

}
