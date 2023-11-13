package com.example.budget.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.budget.dto.res.CategoriesResDto;
import com.example.budget.entity.Category;
import com.example.budget.repository.CategoryRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

  @InjectMocks
  private CategoryServiceImpl categoryService;

  @Mock
  private CategoryRepository categoryRepository;

  @Test
  @DisplayName("카테고리 목록 불러오기")
  void get_categories() {
    //given
    Category category1 = new Category(1L, "카테고리1", 0);
    Category category2 = new Category(2L, "카테고리2", 0);
    List<Category> mockCategories = Arrays.asList(category1, category2);

    Mockito.when(categoryRepository.findAll()).thenReturn(mockCategories);

    //when
    CategoriesResDto result = categoryService.getCategories();

    //then
    assertEquals(2, result.categories().size());
    assertEquals(category1.getId(), result.categories().get(0).id());
    assertEquals(category2.getId(), result.categories().get(1).id());
  }

}