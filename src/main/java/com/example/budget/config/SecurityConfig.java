package com.example.budget.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable) // csrf 보안 사용 X
        // 세션 사용하지 않으므로 STATELESS 로 설정
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // URL 별 권한 관리 옵션
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
            .requestMatchers(HttpMethod.POST, "/api/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/members").permitAll()
            .anyRequest().authenticated());

    return http.build();
  }

}
