package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingInfo {

    private String prkCenterId;
    private String prkPlceNm;
    private String prkPlceAdres;
    private Double prkPlceEntrcLa;
    private Double prkPlceEntrcLo;
    private Integer prkCmprtCo;
    
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
}
