package com.example.parking_hub.service;

import com.example.parking_hub.exception.InvalidParameterException;
import org.springframework.stereotype.Service;

@Service
public class ParkingValidationService {
    
    /**
     * 위도/경도 파라미터 유효성 검사
     */
    public void validateCoordinates(Double latitude, Double longitude) {
        if (latitude == null) {
            throw new InvalidParameterException("lat", "위도 값은 필수입니다");
        }
        
        if (longitude == null) {
            throw new InvalidParameterException("lng", "경도 값은 필수입니다");
        }
        
        if (latitude < -90 || latitude > 90) {
            throw new InvalidParameterException("lat", "위도 값은 -90부터 90 사이의 값이어야 합니다");
        }
        
        if (longitude < -180 || longitude > 180) {
            throw new InvalidParameterException("lng", "경도 값은 -180부터 180 사이의 값이어야 합니다");
        }
    }
    
    /**
     * 검색 반경 파라미터 유효성 검사
     */
    public double validateRadius(Double radiusKm) {
        if (radiusKm == null) {
            return 2.0; // 기본값
        }
        
        if (radiusKm <= 0) {
            throw new InvalidParameterException("radius", "검색 반경은 0보다 커야 합니다");
        }
        
        // 최대 반경 제한
        if (radiusKm > 50) {
            return 50.0;
        }
        
        return radiusKm;
    }
    
    /**
     * 문자열 검색어 유효성 검사
     */
    public void validateSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new InvalidParameterException("query", "검색어는 비어있을 수 없습니다");
        }
        
        if (query.length() < 2) {
            throw new InvalidParameterException("query", "검색어는 최소 2자 이상이어야 합니다");
        }
    }
} 