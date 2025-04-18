package com.example.parking_hub.dto;

import com.example.parking_hub.model.FavoriteParking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 즐겨찾기 주차장 조회를 위한 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteParkingResponse {
    
    /**
     * 즐겨찾기 ID
     */
    private Long id;
    
    /**
     * 사용자 ID
     */
    private Long userId;
    
    /**
     * 주차장 ID (한국교통안전공단 제공 ID)
     */
    private String prkCenterId;
    
    /**
     * 주차장 이름
     */
    private String prkPlceNm;
    
    /**
     * 주차장 주소
     */
    private String prkPlceAdres;
    
    /**
     * 사용자 메모
     */
    private String memo;
    
    /**
     * 생성 일시
     */
    private LocalDateTime createdAt;
    
    /**
     * 수정 일시
     */
    private LocalDateTime updatedAt;
    
    /**
     * FavoriteParking 엔티티를 DTO로 변환합니다.
     *
     * @param entity FavoriteParking 엔티티
     * @return FavoriteParkingResponse DTO
     */
    public static FavoriteParkingResponse fromEntity(FavoriteParking entity) {
        return FavoriteParkingResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .prkCenterId(entity.getPrkCenterId())
                .prkPlceNm(entity.getPrkPlceNm())
                .prkPlceAdres(entity.getPrkPlceAdres())
                .memo(entity.getMemo())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
} 