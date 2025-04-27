package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
public class ParkingOperation {

    private String prkCenterId;
    private Integer opertnBsFreeTime;
    private String parkingChrgeBsTime;
    private String parkingChrgeBsChrg;
    private String parkingChrgeAditUnitTime;
    private String parkingChrgeAditUnitChrge;
    private String parkingChrgeOneDayChrge;
    private String parkingChrgeMonUnitChrge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public ParkingOperation() {
    }
    
    // 명시적인 setter 메서드 추가
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public void setOpertnBsFreeTime(Integer opertnBsFreeTime) {
        this.opertnBsFreeTime = opertnBsFreeTime;
    }
    
    // int 타입도 받을 수 있는 오버로드 메서드 추가
    public void setOpertnBsFreeTime(int opertnBsFreeTime) {
        this.opertnBsFreeTime = opertnBsFreeTime;
    }
    
    public void setParkingChrgeBsTime(String parkingChrgeBsTime) {
        this.parkingChrgeBsTime = parkingChrgeBsTime;
    }
    
    public void setParkingChrgeBsChrg(String parkingChrgeBsChrg) {
        this.parkingChrgeBsChrg = parkingChrgeBsChrg;
    }
    
    public void setParkingChrgeAditUnitTime(String parkingChrgeAditUnitTime) {
        this.parkingChrgeAditUnitTime = parkingChrgeAditUnitTime;
    }
    
    public void setParkingChrgeAditUnitChrge(String parkingChrgeAditUnitChrge) {
        this.parkingChrgeAditUnitChrge = parkingChrgeAditUnitChrge;
    }
    
    public void setParkingChrgeOneDayChrge(String parkingChrgeOneDayChrge) {
        this.parkingChrgeOneDayChrge = parkingChrgeOneDayChrge;
    }
    
    public void setParkingChrgeMonUnitChrge(String parkingChrgeMonUnitChrge) {
        this.parkingChrgeMonUnitChrge = parkingChrgeMonUnitChrge;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
