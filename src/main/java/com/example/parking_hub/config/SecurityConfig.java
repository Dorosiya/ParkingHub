package com.example.parking_hub.config;

import com.example.parking_hub.security.CustomUserDetailsService;
import com.example.parking_hub.security.JwtAuthorizationFilter;
import com.example.parking_hub.security.JwtLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 인증 관리자 가져오기
        AuthenticationManager authManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        
        // JWT 로그인 필터 설정
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authManager, jwtUtil);
        jwtLoginFilter.setFilterProcessesUrl("/api/auth/login");
        
        http
                .csrf().disable()  // CSRF 보호 비활성화 (JWT 사용 시 일반적)
                .formLogin().disable()  // 기본 폼 로그인 비활성화
                .httpBasic().disable()  // HTTP Basic 인증 비활성화
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 사용 안함 (JWT 사용)
                .and()
                .authorizeRequests()
                    // 접근 허용할 URL 설정
                    .antMatchers("/", "/home", "/login", "/register", "/mapSearch", "/search", "/parking/**").permitAll()
                    .antMatchers("/api/auth/**").permitAll()
                    .antMatchers("/api/parking/**").permitAll()
                    .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                    // 그 외 모든 요청은 인증 필요
                    .anyRequest().authenticated()
                .and()
                // JWT 로그인 필터 추가 (UsernamePasswordAuthenticationFilter 위치에)
                .addFilter(jwtLoginFilter)
                // JWT 인증 확인 필터 추가
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        
        http
                .logout()
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"로그아웃 되었습니다.\"}");
                })
                .invalidateHttpSession(true)
                .clearAuthentication(true);

        return http.build();
    }
} 