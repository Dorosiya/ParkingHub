package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
public class ParkingOperationHours {

    private int id;
    private String prkCenterId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

}
