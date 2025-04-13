package com.example.parking_hub.service;

import com.example.parking_hub.client.ParkingApiClient;
import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import com.example.parking_hub.mapper.ParkingInfoMapper;
import com.example.parking_hub.mapper.ParkingOperationMapper;
import com.example.parking_hub.mapper.ParkingRealtimeMapper;
import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.model.ParkingOperation;
import com.example.parking_hub.model.ParkingRealtime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ParkingDataSyncServiceTest {

    @Autowired
    private ParkingDataSyncService parkingDataSyncService;

    @MockBean
    private ParkingApiClient parkingApiClient;

    @MockBean
    private ParkingInfoMapper parkingInfoMapper;

    @MockBean
    private ParkingOperationMapper parkingOperationMapper;

    @MockBean
    private ParkingRealtimeMapper parkingRealtimeMapper;

    private PrkSttusInfoResponse sttusInfoResponse;
    private PrkOprInfoResponse oprInfoResponse;
    private PrkRealtimeInfoResponse realtimeInfoResponse;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정 - 단순화된 JSON 응답 구조에 맞춤
        // 주차장 기본정보 응답 설정
        PrkSttusInfoResponse.PrkSttusInfo sttusItem = createSttusInfoItem("TEST001", "테스트 주차장", 37.5665, 126.9780);
        
        sttusInfoResponse = new PrkSttusInfoResponse();
        sttusInfoResponse.setResultCode("00");
        sttusInfoResponse.setResultMsg("OK");
        sttusInfoResponse.setNumOfRows("10");
        sttusInfoResponse.setPageNo("1");
        sttusInfoResponse.setTotalCount("1");
        sttusInfoResponse.setPrkSttusInfo(Arrays.asList(sttusItem));
        
        // 운영정보 응답 설정
        PrkOprInfoResponse.PrkOprInfo oprItem = createOprInfoItem("TEST001", "30", "60", "30분", "1000");
        
        oprInfoResponse = new PrkOprInfoResponse();
        oprInfoResponse.setResultCode("00");
        oprInfoResponse.setResultMsg("OK");
        oprInfoResponse.setNumOfRows("10");
        oprInfoResponse.setPageNo("1");
        oprInfoResponse.setTotalCount("1");
        oprInfoResponse.setPrkOprInfo(Arrays.asList(oprItem));
        
        // 실시간 정보 응답 설정
        PrkRealtimeInfoResponse.PrkRealtimeItem realtimeItem = createRealtimeInfoItem("TEST001", 100, 50);
        
        realtimeInfoResponse = new PrkRealtimeInfoResponse();
        realtimeInfoResponse.setResultCode("00");
        realtimeInfoResponse.setResultMsg("SUCCESS");
        realtimeInfoResponse.setNumOfRows("10");
        realtimeInfoResponse.setPageNo("1");
        realtimeInfoResponse.setTotalCount("1");
        realtimeInfoResponse.setPrkRealtimeInfo(Arrays.asList(realtimeItem));
    }

    @Test
    void testSyncParkingData() {
        // given
        when(parkingApiClient.getPrkSttusInfo(anyInt(), anyInt())).thenReturn(sttusInfoResponse);
        when(parkingApiClient.getPrkOprInfo(anyInt(), anyInt())).thenReturn(oprInfoResponse);

        // when
        parkingDataSyncService.syncParkingData();

        // then
        verify(parkingInfoMapper, times(1)).insertOrUpdateParkingInfo(any(ParkingInfo.class));
        verify(parkingOperationMapper, times(1)).insertOrUpdateParkingOperation(any(ParkingOperation.class));
    }

    @Test
    void testSyncRealtimeData() {
        // given
        when(parkingApiClient.getPrkRealtimeInfo(anyInt(), anyInt())).thenReturn(realtimeInfoResponse);

        // when
        parkingDataSyncService.syncParkingRealtimeInfo();

        // then
        verify(parkingRealtimeMapper, times(1)).insertOrUpdateParkingRealtime(any(ParkingRealtime.class));
    }

    private PrkSttusInfoResponse.PrkSttusInfo createSttusInfoItem(String prkCenterId, String prkName, double lat, double lng) {
        PrkSttusInfoResponse.PrkSttusInfo item = new PrkSttusInfoResponse.PrkSttusInfo();
        item.setPrkCenterId(prkCenterId);
        item.setPrkCenterNm(prkName);
        item.setLatitude(lat);
        item.setLongitude(lng);
        item.setPrkCmprtCo(100); // 주차면 수 추가
        item.setPrkPlceSe("노외"); // 주차장 구분 추가
        return item;
    }

    private PrkOprInfoResponse.PrkOprInfo createOprInfoItem(String prkCenterId, String opertnBsFreeTime, 
                                                         String parkingChrgeBsTime, String parkingChrgeFreeTime, 
                                                         String parkingChrge) {
        PrkOprInfoResponse.PrkOprInfo item = new PrkOprInfoResponse.PrkOprInfo();
        item.setPrkCenterId(prkCenterId);
        item.setOpertnBsFreeTime(opertnBsFreeTime);
        item.setParkingChrgeBsTime(parkingChrgeBsTime);
        item.setParkingChrgeBsChrg(parkingChrge);
        item.setOperationDayInfo("평일+주말");
        return item;
    }

    private PrkRealtimeInfoResponse.PrkRealtimeItem createRealtimeInfoItem(String prkCenterId, 
                                                                        int totalParkingLots, int availableParkingLots) {
        PrkRealtimeInfoResponse.PrkRealtimeItem item = new PrkRealtimeInfoResponse.PrkRealtimeItem();
        item.setPrkCenterId(prkCenterId);
        item.setPkfcParkingLotsTotal(totalParkingLots);
        item.setPkfcAvailableParkingLotsTotal(availableParkingLots);
        return item;
    }
} 