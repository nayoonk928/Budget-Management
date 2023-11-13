package com.example.budget.repository;

import java.util.Map;

public interface BudgetQRepository {

  Map<Long, Double> getCategoryAverageRates();

}
