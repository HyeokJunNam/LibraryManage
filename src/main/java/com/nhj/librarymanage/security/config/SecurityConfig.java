package com.nhj.librarymanage.security.config;

import com.nhj.librarymanage.security.authenticate.CustomAuthenticationProcessingFilter;
import com.nhj.librarymanage.security.authorize.JwtAuthorizationFilter;
import com.nhj.librarymanage.security.jwt.JwtProvider;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Slf4j
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    //private final JwtProperties jwtProperties;

    // private final AuthenticationEntryPoint authenticationEntryPoint;
    // private final AccessDeniedHandler accessDeniedHandler;

    //private final SecurityCustomFilterFactory securityCustomFilterFactory;



    private final List<String> ALLOWED_ORIGIN_PATTERN_LIST = List.of(
            CorsConfiguration.ALL
    );

    private final List<String> ALLOWED_METHOD_LIST = List.of(
            HttpMethod.HEAD.name(),
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name()
    );

    private final List<String> ALLOWED_HEADER_LIST = List.of(
            HttpHeaders.AUTHORIZATION,
            HttpHeaders.CACHE_CONTROL,
            HttpHeaders.CONTENT_TYPE
    );

    private final List<String> EXPOSED_HEADER_LIST = List.of(
            //jwtProperties.getHeader(),
            "id",
            HttpHeaders.SET_COOKIE
    );

    private final PathPatternRequestMatcher[] DEFAULT_PERMIT_REQUEST_MATCHERS = {
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/refresh"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/logout"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/login")
    };

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(ALLOWED_ORIGIN_PATTERN_LIST);
        corsConfiguration.setAllowedMethods(ALLOWED_METHOD_LIST);
        corsConfiguration.setAllowedHeaders(ALLOWED_HEADER_LIST);
        corsConfiguration.setExposedHeaders(EXPOSED_HEADER_LIST);
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           AuthenticationManager authenticationManager,
                                           AuthenticationEntryPoint authenticationEntryPoint,
                                           JwtProvider jwtProvider) {

        CustomAuthenticationProcessingFilter customAuthenticationProcessingFilter =
                new CustomAuthenticationProcessingFilter(authenticationManager, authenticationEntryPoint, jwtProvider);

        JwtAuthorizationFilter jwtAuthorizationFilter =
                new JwtAuthorizationFilter(authenticationEntryPoint, jwtProvider);


        httpSecurity
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
//                        .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
//                        .authenticationEntryPoint(authenticationEntryPoint)
//                        .accessDeniedHandler(accessDeniedHandler))
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> {
                    //request.anyRequest().permitAll();
                    request.requestMatchers(DEFAULT_PERMIT_REQUEST_MATCHERS).permitAll();
                    request.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(customAuthenticationProcessingFilter, JwtAuthorizationFilter.class);
        ;

               /* .addFilterBefore(securityCustomFilterFactory.jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(securityCustomFilterFactory.refreshTokenFilter(), JwtAuthorizationFilter.class)
                .addFilterAfter(securityCustomFilterFactory.customAuthenticationProcessingFilter(), JwtAuthorizationFilter.class);*/

        return httpSecurity.build();
    }

}