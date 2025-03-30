package com.example.parking_hub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄링 기능 활성화 설정
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 별도의 추가 설정이 필요하지 않음
    // @EnableScheduling 어노테이션만으로 스프링의 스케줄링 기능이 활성화됨
} 