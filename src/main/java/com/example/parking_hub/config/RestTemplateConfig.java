package com.example.parking_hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * RestTemplate 설정 클래스
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 10초
        factory.setReadTimeout(10000); // 10초
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // UTF-8 인코딩 처리를 위한 StringHttpMessageConverter 설정
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        
        // 다양한 응답 형식을 처리할 수 있도록 MappingJackson2HttpMessageConverter 확장
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        
        // 다양한 MediaType 지원 추가
        List<MediaType> supportedMediaTypes = new ArrayList<>();
        supportedMediaTypes.add(MediaType.APPLICATION_JSON);
        supportedMediaTypes.add(MediaType.TEXT_PLAIN);
        supportedMediaTypes.add(MediaType.TEXT_HTML);
        supportedMediaTypes.add(new MediaType("application", "*+json"));
        supportedMediaTypes.add(new MediaType("text", "json"));
        // API에서 실제로 반환하는 타입이 있다면 여기에 추가
        jacksonConverter.setSupportedMediaTypes(supportedMediaTypes);
        
        // 기존 MessageConverter 교체
        restTemplate.setMessageConverters(Arrays.asList(stringConverter, jacksonConverter));
        
        return restTemplate;
    }
} 