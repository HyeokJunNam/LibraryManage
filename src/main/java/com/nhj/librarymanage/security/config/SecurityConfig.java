package com.nhj.librarymanage.security.config;

import com.nhj.librarymanage.security.session.LoginFailureHandler;
import com.nhj.librarymanage.security.session.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
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

    private static final RequestMatcher[] PERMIT_URLS = {
            PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/"),
            PathPatternRequestMatcher.pathPattern(HttpMethod.POST, "/members"),
            //PathPatternRequestMatcher.pathPattern(null, "/library/**"),
            PathPatternRequestMatcher.pathPattern(null, "/login"),
            PathPatternRequestMatcher.pathPattern(null, "/signup"),
            PathPatternRequestMatcher.pathPattern(null, "/oauth2/**"),
            PathPatternRequestMatcher.pathPattern(null, "/favicon.ico"),
            PathPatternRequestMatcher.pathPattern(null, "/css/**"),
            PathPatternRequestMatcher.pathPattern(null, "/js/**"),
            PathPatternRequestMatcher.pathPattern(null, "/images/**"),
            PathPatternRequestMatcher.pathPattern(null, "/error")
    };


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
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) {
        LoginSuccessHandler loginSuccessHandler = new LoginSuccessHandler("/");
        LoginFailureHandler loginFailureHandler = new LoginFailureHandler("/login?error");

        httpSecurity
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(AbstractHttpConfigurer::disable)
                // 임시 해제
                .csrf(AbstractHttpConfigurer::disable)
                /*.exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))*/
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("loginId")
                        .successHandler(loginSuccessHandler)
                        .failureHandler(loginFailureHandler)
                        .permitAll()
                )
                // TODO 토큰 작업 필요 / 리다이렉트는 되나 토큰이 없어서 결국 데이터는 못불러옴
                //.oauth2Login(oauth -> oauth.defaultSuccessUrl("/library/book-list", true))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(request -> {
                    request.requestMatchers(PERMIT_URLS).permitAll();
                    request.anyRequest().authenticated();
                });
                //.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                //.addFilterAfter(customAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

}