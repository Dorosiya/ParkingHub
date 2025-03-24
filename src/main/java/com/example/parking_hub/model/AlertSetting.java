package com.example.parking_hub.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlertSetting {

    private int id;
    private int userId;
    private String parkingCode;
    private String dayOfWeek;
    private int hour;
    private int minFreeSlots;
}
