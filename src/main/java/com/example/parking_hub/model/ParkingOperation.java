package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingOperation {

    private String prkCenterId;
    private Integer opertnBsFreeTime;
    private String parkingChrgeBsTime;
    private String parkingChrgeBsChrg;
    private String operationDayInfo;
    
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
    
    public void setOperationDayInfo(String operationDayInfo) {
        this.operationDayInfo = operationDayInfo;
    }
}
