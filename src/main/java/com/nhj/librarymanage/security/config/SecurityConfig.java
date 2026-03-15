package com.nhj.librarymanage.security.config;

import com.nhj.librarymanage.security.authenticate.CustomAuthenticationProcessingFilter;
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
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/test")
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

    // UserDetailsService 구현체가 2개 이상인 경우, AuthenticationManager 가 어떤 UserDetailsService 를 사용 해야 할 지 모르겠는데? 라는 상황이 나올 것이므로
    // 직접 하나씩 지정해줌으로써 모호성을 해결하여 AuthenticationManager 를 생성한다.. 라는 목적으로 쓰는거구만?
    // 그것도 기본 UserDetailsService 를 구현한 구현체를 쓴다는 전제 조건 하에..! 그래야 내부 로직에서 처리할 수 있으니까.. 난 그 과정을 오버라이드 했으니 필요가 없는거지.
    /*@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }*/


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationProcessingFilter customAuthenticationProcessingFilter = new CustomAuthenticationProcessingFilter(authenticationManager);

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
                    request.anyRequest().permitAll();
                    // request.requestMatchers(DEFAULT_PERMIT_REQUEST_MATCHERS).permitAll();

                    //request.anyRequest().authenticated();
                })
                .addFilterBefore(customAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);
        ;

               /* .addFilterBefore(securityCustomFilterFactory.jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(securityCustomFilterFactory.refreshTokenFilter(), JwtAuthorizationFilter.class)
                .addFilterAfter(securityCustomFilterFactory.customAuthenticationProcessingFilter(), JwtAuthorizationFilter.class);*/

        return httpSecurity.build();
    }

}