package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ParkingOperationTime {

    private Long id;
    private String prkCenterId;
    private String dayOfWeek; // 'Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Holiday'
    private String opertnStartTime; // 운영 시작시간 (ex: '080000')
    private String opertnEndTime;   // 운영 종료시간 (ex: '200000')
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 생성자
    public ParkingOperationTime(String prkCenterId, String dayOfWeek, String opertnStartTime, String opertnEndTime) {
        this.prkCenterId = prkCenterId;
        this.dayOfWeek = dayOfWeek;
        this.opertnStartTime = opertnStartTime;
        this.opertnEndTime = opertnEndTime;
    }
    
    // Getter, Setter 메서드 (Lombok으로 자동 생성되지만 명시적으로 추가)
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPrkCenterId() {
        return prkCenterId;
    }
    
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getOpertnStartTime() {
        return opertnStartTime;
    }
    
    public void setOpertnStartTime(String opertnStartTime) {
        this.opertnStartTime = opertnStartTime;
    }
    
    public String getOpertnEndTime() {
        return opertnEndTime;
    }
    
    public void setOpertnEndTime(String opertnEndTime) {
        this.opertnEndTime = opertnEndTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 