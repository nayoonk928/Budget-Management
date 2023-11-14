package com.example.budget.config;

import com.example.budget.dto.req.ExpenseCreateReqDto;
import com.example.budget.entity.Category;
import com.example.budget.entity.Member;
import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.repository.ExpenseRepository;
import com.example.budget.type.CategoryType;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test")
@Component
@RequiredArgsConstructor
public class ExpenseDataInit {

  private final List<String> CATEGORIES = new ArrayList<>(
      Arrays.stream(CategoryType.values())
          .map(CategoryType::getName)
          .collect(Collectors.toList())
  );

  private final ExpenseRepository expenseRepository;
  private final CategoryRepository categoryRepository;

  public void generateTestData(Member member, int count) {
    Random random = new Random();
    for (int i = 0; i < count; i++) {
      String categoryName = getRandomCategory(random);
      int amount = random.nextInt(50000) + 1000;
      boolean isExcludedSum = random.nextBoolean();
      String description = getRandomDescription(categoryName);

      // 오늘로부터 한 달 전 1일부터 오늘 사이의 랜덤한 날짜 생성
      LocalDate startDate = LocalDate.now().minusMonths(1).withDayOfMonth(1);
      LocalDate endDate = LocalDate.now();
      long randomDays = startDate.until(endDate, ChronoUnit.DAYS);
      LocalDate expendedAt = startDate.plusDays(random.nextInt((int) randomDays) + 1);


      ExpenseCreateReqDto request = ExpenseCreateReqDto.builder()
          .expendedAt(expendedAt)
          .amount(amount)
          .category(categoryName)
          .isExcludedSum(isExcludedSum)
          .description(description)
          .build();

      Category category = categoryRepository.findByName(categoryName)
          .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

      expenseRepository.save(request.toEntity(member, category, request));
    }
  }

  private String getRandomCategory(Random random) {
    return CATEGORIES.get(random.nextInt(CATEGORIES.size()));
  }

  private String getRandomDescription(String categoryName) {
    switch (categoryName) {
      case "식비":
        return "식사 비용";
      case "교통":
        return "대중교통 비용";
      case "쇼핑":
        return "쇼핑 비용";
      case "여가":
        return "영화나 책 구매 비용";
      case "의료/건강":
        return "약 비용";
      case "생활":
        return "생활용품 구매 비용";
      case "기타":
        return "기타 지출 비용";
      default:
        return "기타";
    }
  }

}
