package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Setter
@Getter
public class ParkingInfo {

    private String prkCenterId;
    private String prkPlceNm;
    private String prkPlceAdres;
    private Double prkPlceEntrcLa;
    private Double prkPlceEntrcLo;
    private Integer prkCmprtCo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isFree;
    private String parkingFeeInfo;
    
    // 명시적인 setter 메서드 추가
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public void setPrkPlceNm(String prkPlceNm) {
        this.prkPlceNm = prkPlceNm;
    }
    
    public void setPrkPlceAdres(String prkPlceAdres) {
        this.prkPlceAdres = prkPlceAdres;
    }
    
    public void setPrkPlceEntrcLa(Double prkPlceEntrcLa) {
        this.prkPlceEntrcLa = prkPlceEntrcLa;
    }
    
    public void setPrkPlceEntrcLo(Double prkPlceEntrcLo) {
        this.prkPlceEntrcLo = prkPlceEntrcLo;
    }
    
    public void setPrkCmprtCo(Integer prkCmprtCo) {
        this.prkCmprtCo = prkCmprtCo;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsFree() {
        return isFree;
    }
    
    public void setFree(Boolean isFree) {
        this.isFree = isFree;
    }
    
    public String getParkingFeeInfo() {
        return parkingFeeInfo;
    }
    
    public void setParkingFeeInfo(String parkingFeeInfo) {
        this.parkingFeeInfo = parkingFeeInfo;
    }
}
