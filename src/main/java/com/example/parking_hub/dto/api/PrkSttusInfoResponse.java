package com.example.parking_hub.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PrkSttusInfoResponse {
    private String resultCode;
    private String resultMsg;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
    
    @JsonProperty("PrkSttusInfo")
    private List<PrkSttusInfo> prkSttusInfoList;

    @Getter
    @Setter
    public static class PrkSttusInfo {
        private String prkCenterId;
        private String prkPlceNm;
        private String prkPlceAdres;
        private double prkPlceEntrcLa;
        private double prkPlceEntrcLo;
        private int prkCmprtCo;
    }
} 