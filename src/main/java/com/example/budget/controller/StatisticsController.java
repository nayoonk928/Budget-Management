package com.example.budget.controller;

import com.example.budget.dto.res.StatisticsDto;
import com.example.budget.entity.Member;
import com.example.budget.security.AuthenticationPrincipal;
import com.example.budget.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping("/month")
  public ResponseEntity<StatisticsDto> compareWithLastMonth(
      @AuthenticationPrincipal Member member
  ) {
    return ResponseEntity.ok().body(statisticsService.compareWithLastMonth(member));
  }

  @GetMapping("/weekday")
  public ResponseEntity<StatisticsDto> compareWithLastWeek(
      @AuthenticationPrincipal Member member
  ) {
    return ResponseEntity.ok().body(statisticsService.compareWithLastWeek(member));
  }

}
