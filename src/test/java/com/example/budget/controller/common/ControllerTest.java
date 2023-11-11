package com.example.budget.controller.common;

import com.example.budget.entity.Category;
import com.example.budget.repository.CategoryRepository;
import com.example.budget.type.CategoryType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class ControllerTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected CategoryRepository categoryRepository;

  @BeforeEach
  public void setUp() {
    saveCategories();
  }

  private void saveCategories() {
    List<Category> categories = new ArrayList<>();

    for (CategoryType type : CategoryType.values()) {
      categories.add(Category.builder()
          .name(type.getName())
          .build());
    }

    categoryRepository.saveAll(categories);
  }

}
