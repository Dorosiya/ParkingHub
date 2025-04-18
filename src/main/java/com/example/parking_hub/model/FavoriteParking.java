package com.example.parking_hub.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 즐겨찾기 주차장 정보를 저장하는 모델 클래스
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteParking {
    
    /** 즐겨찾기 ID */
    private Long id;
    
    /** 사용자 ID */
    private Long userId;
    
    /** 주차장 ID (공공데이터 API 주차장 ID) */
    private String prkCenterId;
    
    /** 메모 */
    private String memo;
    
    /** 생성 일시 */
    private LocalDateTime createdAt;
    
    /** 수정 일시 */
    private LocalDateTime updatedAt;
    
    // --- 주차장 정보 필드 (JOIN 시 사용) ---
    
    /** 주차장 이름 */
    private String prkPlceNm;
    
    /** 주차장 주소 */
    private String prkPlceAdres;
    
    /** 주차 구획 수 */
    private Integer prkCmprtCo;
}
