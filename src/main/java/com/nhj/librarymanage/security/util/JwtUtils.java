package com.nhj.librarymanage.security.util;

import com.nhj.librarymanage.security.exception.authenticate.AuthenticateErrorCode;
import com.nhj.librarymanage.security.exception.authenticate.CustomJwtException;
import com.nhj.librarymanage.security.member.Role;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    public static List<SimpleGrantedAuthority> extractRole(Claims claims) {
        String role = claims.get("role").toString();

        if (Role.hasRole(role)) {
            return List.of(new SimpleGrantedAuthority(role));
        }
        else {
            return Collections.emptyList();
        }
    }

    public static String extractLoginId(Claims claims) {
        try {
            return claims.get("loginId").toString();
        }
        catch (Exception e) {
            throw new CustomJwtException(AuthenticateErrorCode.INVALID_AUTHENTICATION);
        }
    }

    public static String extractId(Claims claims) {
        try {
            return claims.get("id").toString();
        }
        catch (Exception e) {
            throw new CustomJwtException(AuthenticateErrorCode.INVALID_AUTHENTICATION);
        }
    }

}