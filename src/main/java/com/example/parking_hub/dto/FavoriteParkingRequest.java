package com.example.parking_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 주차장 추가/수정을 위한 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteParkingRequest {
    
    /**
     * 주차장 ID (한국교통안전공단 제공 ID)
     */
    private String prkCenterId;
    
    /**
     * 사용자 메모
     */
    private String memo;
} 