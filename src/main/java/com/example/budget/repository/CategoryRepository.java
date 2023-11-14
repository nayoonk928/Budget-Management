package com.example.budget.repository;

import com.example.budget.entity.Category;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

  Optional<Category> findByName(String name);

  boolean existsByName(String name);

}
