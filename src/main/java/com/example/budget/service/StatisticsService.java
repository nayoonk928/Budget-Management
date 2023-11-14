package com.example.budget.service;

import com.example.budget.dto.res.StatisticsDto;
import com.example.budget.entity.Member;

public interface StatisticsService {

  StatisticsDto compareWithLastMonth(Member member);

  StatisticsDto compareWithLastWeek(Member member);

}
