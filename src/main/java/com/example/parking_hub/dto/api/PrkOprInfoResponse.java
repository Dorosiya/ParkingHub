package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrkOprInfoResponse {
    private String resultCode;
    private String resultMsg;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    
    @JsonProperty("PrkOprInfo")
    private List<PrkOprInfo> prkOprInfoList;

    @Getter
    @Setter
    public static class PrkOprInfo {
        private String prkCenterId;
        private String opertnBsFreeTime;    // 기본 회차(기본무료)시간
        private String parkingChrgeBsTime;  // 기본 시간
        private String parkingChrgeBsChrge; // 기본 요금
        private String parkingChrgeAditUnitTime;  // 추가 단위 시간
        private String parkingChrgeAditUnitChrge; // 추가 단위 요금
        private String parkingChrgeOneDayChrge;   // 1일 요금
        private String parkingChrgeMonUnitChrge;  // 월 정액
    }
} 