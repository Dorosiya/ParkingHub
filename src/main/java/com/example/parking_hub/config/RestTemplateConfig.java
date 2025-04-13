package com.example.parking_hub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
        factory.setReadTimeout(60000); // 60초
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // UTF-8 인코딩 처리를 위한 StringHttpMessageConverter 설정
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        stringConverter.setWriteAcceptCharset(false);
        
        // JSON 응답 처리를 위한 MappingJackson2HttpMessageConverter 설정
        MappingJackson2HttpMessageConverter jacksonConverter = new MappingJackson2HttpMessageConverter();
        List<MediaType> jsonMediaTypes = new ArrayList<>();
        jsonMediaTypes.add(MediaType.APPLICATION_JSON);
        jsonMediaTypes.add(MediaType.TEXT_PLAIN); // 일부 API는 text/plain으로 JSON을 반환하기도 함
        jsonMediaTypes.add(new MediaType("application", "*+json"));
        jsonMediaTypes.add(new MediaType("text", "json"));
        jacksonConverter.setSupportedMediaTypes(jsonMediaTypes);
        
        // 컨버터 등록
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(stringConverter);
        messageConverters.add(jacksonConverter);
        
        restTemplate.setMessageConverters(messageConverters);
        
        return restTemplate;
    }
} 