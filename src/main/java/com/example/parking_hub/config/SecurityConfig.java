package com.example.parking_hub.config;

import com.example.parking_hub.security.CustomUserDetailsService;
import com.example.parking_hub.security.JwtAuthenticationFilter;
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
        http
                .csrf().disable()  // CSRF 보호는 상황에 따라 활성화 고려
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 폼 로그인을 위한 세션 생성
                .and()
                .authorizeRequests()
                .antMatchers("/", "/home", "/login", "/register", "/mapSearch", "/api/auth/**").permitAll()
                .antMatchers("/api/parkings/**").permitAll()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")                                // JSP 파일명이 아닌 컨트롤러 매핑 URL
                .loginProcessingUrl("/perform-login")               // 로그인 처리 URL
                .defaultSuccessUrl("/")                         // 로그인 성공 시 리다이렉트
                .failureUrl("/login?error=true")  // 로그인 실패 시
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")  // 로그아웃 성공 시 리다이렉트
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
} 