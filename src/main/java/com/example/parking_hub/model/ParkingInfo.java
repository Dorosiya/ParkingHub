package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingInfo {

    private String prkCenterId;
    private String prkPlceNm;
    private String prkPlceAdres;
    private double prkPlceEntrcLa;
    private double prkPlceEntrcLo;
    private int prkCmprtCo;
}
