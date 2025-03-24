package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ParkingPriceInfo {

    private String prkCenterId;
    private int parkingChrgeBsTime;
    private int parkingChrgeBsChrge;
    private int parkingChrgeAditUnitTime;
    private int parkingChrgeAditUnitChrge;
    private int parkingChrgeOneDayChrge;
    private int parkingChrgeMonUnitChrge;
}
