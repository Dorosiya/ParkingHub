package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class ParkingRealtime {

    private String prkCenterId;
    private int pkfcParkingLotsTotal;
    private int pkfcAvailableParkingLotsTotal;
    private LocalDateTime updatedAt;
}
