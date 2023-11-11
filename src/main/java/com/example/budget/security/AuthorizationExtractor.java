package com.example.budget.security;

import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public class AuthorizationExtractor {

  private static final String BEARER_TYPE = "Bearer ";

  public static String extract(HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if(authorizationHeader == null || authorizationHeader.isBlank()) {
      throw new CustomException(ErrorCode.EMPTY_AUTHORIZATION_HEADER);
    }

    validateAuthorizationFormat(authorizationHeader);
    return authorizationHeader.substring(BEARER_TYPE.length()).trim();
  }

  private static void validateAuthorizationFormat(String authorizationHeader) {
    if (!authorizationHeader.toLowerCase().startsWith(BEARER_TYPE.toLowerCase())) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

}
