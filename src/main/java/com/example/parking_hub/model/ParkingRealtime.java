package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ParkingRealtime {

    private String prkCenterId;
    private Integer pkfcParkingLotsTotal;
    private Integer pkfcAvailableParkingLotsTotal;
    private LocalDateTime updatedAt;
    
    // Getter 메서드
    public String getPrkCenterId() {
        return prkCenterId;
    }
    
    public Integer getPkfcParkingLotsTotal() {
        return pkfcParkingLotsTotal;
    }
    
    public Integer getPkfcAvailableParkingLotsTotal() {
        return pkfcAvailableParkingLotsTotal;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Setter 메서드
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public void setPkfcParkingLotsTotal(Integer pkfcParkingLotsTotal) {
        this.pkfcParkingLotsTotal = pkfcParkingLotsTotal;
    }
    
    public void setPkfcParkingLotsTotal(int pkfcParkingLotsTotal) {
        this.pkfcParkingLotsTotal = pkfcParkingLotsTotal;
    }
    
    public void setPkfcAvailableParkingLotsTotal(Integer pkfcAvailableParkingLotsTotal) {
        this.pkfcAvailableParkingLotsTotal = pkfcAvailableParkingLotsTotal;
    }
    
    public void setPkfcAvailableParkingLotsTotal(int pkfcAvailableParkingLotsTotal) {
        this.pkfcAvailableParkingLotsTotal = pkfcAvailableParkingLotsTotal;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
