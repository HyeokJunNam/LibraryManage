package com.nhj.librarymanage.security.jwt;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateError;
import com.nhj.librarymanage.security.exception.authenticate.CustomJwtException;
import com.nhj.librarymanage.security.member.SecurityUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;

    public String genNewKey(MacAlgorithm macAlgorithm) {
        return Encoders.BASE64.encode(macAlgorithm.key().build().getEncoded());
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String parseJwt(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer")) {
            throw new CustomJwtException(AuthenticateError.INVALID_AUTHENTICATION);
        }

        return authorization.replace("Bearer ", "");
    }

    public Claims getClaims(String jwt) throws JwtException {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    public String generateToken(SecurityUser securityUser) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS512");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", securityUser.getId());
        claims.put("loginId", securityUser.getLoginId());
        claims.put("role", securityUser.getRole());

        return "Bearer " + Jwts.builder()
                .header().add(headers).and()
                .claims(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(jwtProperties.getExpirySeconds())))
                .signWith(getSignKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String generateRefreshToken(String id) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS512");

        return null;

        /*return Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setHeader(headers)
                .setAudience(id)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(jwtProperties.getRefreshExpirySeconds())))
                .signWith(Keys.hmacShaKeyFor(key), SignatureAlgorithm.HS512)
                .compact();*/
    }

    public ResponseCookie generateRefreshTokenCookie(String refreshToken) {
       return ResponseCookie.from("refresh_token", refreshToken)
               .path("/")
               .httpOnly(true)
               //.secure(true)
               .secure(false)
               .sameSite("None")
               //.sameSite("Strict")
               .build();
    }

}