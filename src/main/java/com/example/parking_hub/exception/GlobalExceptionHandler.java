package com.example.parking_hub.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * 모든 컨트롤러에서 발생하는 예외를 일관되게 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * REST API 예외 처리
     * API 호출 시 발생하는 예외를 처리하여 JSON 형태로 응답
     */
    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class,
            ResourceAccessException.class
    })
    public ResponseEntity<Map<String, Object>> handleApiException(Exception e, HttpServletRequest request) {
        logger.error("API 예외 발생: {} - {}", request.getRequestURI(), e.getMessage(), e);
        
        Map<String, Object> response = new HashMap<>();
        HttpStatus status;
        
        if (e instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
            response.put("message", e.getMessage());
        } else if (e instanceof ResourceAccessException) {
            status = HttpStatus.SERVICE_UNAVAILABLE;
            
            if (e.getCause() instanceof SocketTimeoutException) {
                response.put("message", "외부 서비스 타임아웃이 발생했습니다. 잠시 후 다시 시도해주세요.");
            } else {
                response.put("message", "외부 서비스에 접근할 수 없습니다. 잠시 후 다시 시도해주세요.");
            }
        } else if (e instanceof MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST;
            Map<String, String> errors = new HashMap<>();
            MethodArgumentNotValidException validationException = (MethodArgumentNotValidException) e;
            
            for (FieldError error : validationException.getBindingResult().getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            
            response.put("message", "입력값 검증에 실패했습니다.");
            response.put("errors", errors);
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.put("message", "서버 내부 오류가 발생했습니다.");
        }
        
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("path", request.getRequestURI());
        
        return new ResponseEntity<>(response, status);
    }
    
    /**
     * 웹 페이지 예외 처리
     * 일반 웹 페이지 접근 시 발생하는 예외를 처리하여 오류 페이지로 이동
     */
    @ExceptionHandler(value = {
            Exception.class,
            NoHandlerFoundException.class
    })
    public ModelAndView handleWebException(Exception e, HttpServletRequest request) {
        logger.error("웹 예외 발생: {} - {}", request.getRequestURI(), e.getMessage(), e);
        
        // API 요청인 경우 JSON 응답 반환
        if (request.getRequestURI().startsWith("/api/")) {
            return null; // API 요청은 다른 핸들러에서 처리
        }
        
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("errorMessage", e.getMessage());
        modelAndView.addObject("errorTrace", e.toString());
        modelAndView.addObject("url", request.getRequestURI());
        
        if (e instanceof NoHandlerFoundException) {
            modelAndView.setViewName("error/404");
            modelAndView.addObject("statusCode", 404);
        } else {
            modelAndView.setViewName("error/500");
            modelAndView.addObject("statusCode", 500);
        }
        
        return modelAndView;
    }
    
    /**
     * 데이터 검증 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ModelAndView handleBindException(BindException e, HttpServletRequest request) {
        logger.error("바인딩 예외 발생: {} - {}", request.getRequestURI(), e.getMessage(), e);
        
        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> errors = new HashMap<>();
        
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        modelAndView.addObject("errors", errors);
        modelAndView.addObject("errorMessage", "입력값 검증에 실패했습니다.");
        modelAndView.addObject("url", request.getRequestURI());
        modelAndView.setViewName("error/400");
        
        return modelAndView;
    }
} 