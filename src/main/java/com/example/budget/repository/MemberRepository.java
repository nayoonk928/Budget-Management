package com.example.budget.repository;

import com.example.budget.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByAccount(String account);

  boolean existsByNickname(String nickname);

  Optional<Member> findByAccount(String account);

}
