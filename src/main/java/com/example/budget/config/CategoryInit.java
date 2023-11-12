package com.example.budget.config;

import com.example.budget.entity.Category;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.type.CategoryType;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("test-data")
@Component
@RequiredArgsConstructor
public class CategoryInit {

  private final CategoryRepository categoryRepository;

  @Transactional
  @PostConstruct
  public void init() {
    List<Category> categories = new ArrayList<>();

    for (CategoryType type : CategoryType.values()) {
      categories.add(Category.builder()
          .name(type.getName())
          .build());
    }

    categoryRepository.saveAll(categories);
  }

}
