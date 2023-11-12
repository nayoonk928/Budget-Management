package com.example.budget.repository;

import com.example.budget.entity.Member;
import java.util.Map;

public interface BudgetQRepository {

  Map<Long, Long> getCountGroupByCategory(Member member);

}
