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
        // 테스트 데이터 설정: 새 응답 구조 반영
        PrkSttusInfoResponse.PrkSttusInfo sttusItem = createSttusInfoItem("TEST001", "테스트 주차장", 37.5665, 126.9780);
        PrkSttusInfoResponse.Items sttusItems = new PrkSttusInfoResponse.Items();
        sttusItems.setItem(Arrays.asList(sttusItem));
        
        PrkSttusInfoResponse.Body sttusBody = new PrkSttusInfoResponse.Body();
        sttusBody.setItems(sttusItems);
        sttusBody.setNumOfRows(10);
        sttusBody.setPageNo(1);
        sttusBody.setTotalCount(1);
        
        PrkSttusInfoResponse.Header sttusHeader = new PrkSttusInfoResponse.Header();
        sttusHeader.setResultCode("00");
        sttusHeader.setResultMsg("OK");
        
        PrkSttusInfoResponse.Response sttusResponse = new PrkSttusInfoResponse.Response();
        sttusResponse.setHeader(sttusHeader);
        sttusResponse.setBody(sttusBody);
        
        sttusInfoResponse = new PrkSttusInfoResponse();
        sttusInfoResponse.setResponse(sttusResponse);
        
        // 운영정보 응답 설정
        PrkOprInfoResponse.PrkOprInfo oprItem = createOprInfoItem("TEST001", "09:00", "18:00", "30분", "1000");
        PrkOprInfoResponse.Items oprItems = new PrkOprInfoResponse.Items();
        oprItems.setItem(Arrays.asList(oprItem));
        
        PrkOprInfoResponse.Body oprBody = new PrkOprInfoResponse.Body();
        oprBody.setItems(oprItems);
        oprBody.setNumOfRows(10);
        oprBody.setPageNo(1);
        oprBody.setTotalCount(1);
        
        PrkOprInfoResponse.Header oprHeader = new PrkOprInfoResponse.Header();
        oprHeader.setResultCode("00");
        oprHeader.setResultMsg("OK");
        
        PrkOprInfoResponse.Response oprResponse = new PrkOprInfoResponse.Response();
        oprResponse.setHeader(oprHeader);
        oprResponse.setBody(oprBody);
        
        oprInfoResponse = new PrkOprInfoResponse();
        oprInfoResponse.setResponse(oprResponse);
        
        // 실시간 정보 응답 설정
        PrkRealtimeInfoResponse.PrkRealtimeInfo realtimeItem = createRealtimeInfoItem("TEST001", 100, 50);
        PrkRealtimeInfoResponse.Items realtimeItems = new PrkRealtimeInfoResponse.Items();
        realtimeItems.setItem(Arrays.asList(realtimeItem));
        
        PrkRealtimeInfoResponse.Body realtimeBody = new PrkRealtimeInfoResponse.Body();
        realtimeBody.setItems(realtimeItems);
        realtimeBody.setNumOfRows(10);
        realtimeBody.setPageNo(1);
        realtimeBody.setTotalCount(1);
        
        PrkRealtimeInfoResponse.Header realtimeHeader = new PrkRealtimeInfoResponse.Header();
        realtimeHeader.setResultCode("00");
        realtimeHeader.setResultMsg("OK");
        
        PrkRealtimeInfoResponse.Response realtimeResponse = new PrkRealtimeInfoResponse.Response();
        realtimeResponse.setHeader(realtimeHeader);
        realtimeResponse.setBody(realtimeBody);
        
        realtimeInfoResponse = new PrkRealtimeInfoResponse();
        realtimeInfoResponse.setResponse(realtimeResponse);
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
        return item;
    }

    private PrkOprInfoResponse.PrkOprInfo createOprInfoItem(String prkCenterId, String opertnBsFreeTime, 
                                                         String parkingChrgeBsTime, String parkingChrgeFreeTime, 
                                                         String parkingChrge) {
        PrkOprInfoResponse.PrkOprInfo item = new PrkOprInfoResponse.PrkOprInfo();
        item.setPrkCenterId(prkCenterId);
        item.setOpertnBsFreeTime(opertnBsFreeTime);
        item.setParkingChrgeBsTime(parkingChrgeBsTime);
        return item;
    }

    private PrkRealtimeInfoResponse.PrkRealtimeInfo createRealtimeInfoItem(String prkCenterId, 
                                                                        int totalParkingLots, int availableParkingLots) {
        PrkRealtimeInfoResponse.PrkRealtimeInfo item = new PrkRealtimeInfoResponse.PrkRealtimeInfo();
        item.setPrkCenterId(prkCenterId);
        item.setPkfcParkingLotsTotal(totalParkingLots);
        item.setPkfcAvailableParkingLotsTotal(availableParkingLots);
        return item;
    }
} 