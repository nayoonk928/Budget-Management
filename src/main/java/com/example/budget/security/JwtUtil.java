package com.example.budget.security;

import com.example.budget.exception.CustomException;
import com.example.budget.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  private final SecretKey key;
  private final long accessTokenValidityInMilliseconds;

  public JwtUtil(
      @Value("${security.jwt.token.secret-key}") final String secretKey,
      @Value("${security.jwt.token.access.expire-length}") final long accessTokenValidityInMilliseconds
  ) {
    this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
  }

  private String createToken(Long memberId) {
    Date now = new Date();
    Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

    return Jwts.builder()
        .setSubject(memberId.toString())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public boolean validateToken(String token) {
    try {
      Jws<Claims> claims = Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);

      return claims.getBody()
          .getExpiration()
          .before(new Date());
    } catch (ExpiredJwtException e) {
      throw new CustomException(ErrorCode.EXPIRE_TOKEN);
    } catch (JwtException | IllegalArgumentException e) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }
  }

  public Long extractMemberId(String token) {
    if (!validateToken(token)) {
      throw new CustomException(ErrorCode.INVALID_TOKEN);
    }

    String payload = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();

    return Long.valueOf(payload);
  }

}
