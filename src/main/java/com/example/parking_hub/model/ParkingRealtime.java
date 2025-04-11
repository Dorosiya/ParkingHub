package com.example.parking_hub.model;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
public class ParkingRealtime {

    private String prkCenterId;
    private int pkfcParkingLotsTotal;
    private int pkfcAvailableParkingLotsTotal;
    private LocalDateTime updatedAt;
    
    // Getter 메서드
    public String getPrkCenterId() {
        return prkCenterId;
    }
    
    public int getPkfcParkingLotsTotal() {
        return pkfcParkingLotsTotal;
    }
    
    public int getPkfcAvailableParkingLotsTotal() {
        return pkfcAvailableParkingLotsTotal;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setter 메서드
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public void setPkfcParkingLotsTotal(int pkfcParkingLotsTotal) {
        this.pkfcParkingLotsTotal = pkfcParkingLotsTotal;
    }
    
    public void setPkfcAvailableParkingLotsTotal(int pkfcAvailableParkingLotsTotal) {
        this.pkfcAvailableParkingLotsTotal = pkfcAvailableParkingLotsTotal;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
