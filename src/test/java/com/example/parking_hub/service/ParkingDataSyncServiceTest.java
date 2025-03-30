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
        // 테스트 데이터 설정
        sttusInfoResponse = new PrkSttusInfoResponse();
        sttusInfoResponse.setResultCode("00");
        sttusInfoResponse.setItems(Arrays.asList(
            createSttusInfoItem("TEST001", "테스트 주차장", 37.5665, 126.9780)
        ));

        oprInfoResponse = new PrkOprInfoResponse();
        oprInfoResponse.setResultCode("00");
        oprInfoResponse.setItems(Arrays.asList(
            createOprInfoItem("TEST001", "09:00", "18:00", "30분", "1000")
        ));

        realtimeInfoResponse = new PrkRealtimeInfoResponse();
        realtimeInfoResponse.setResultCode("00");
        realtimeInfoResponse.setItems(Arrays.asList(
            createRealtimeInfoItem("TEST001", 100, 50)
        ));
    }

    @Test
    void testSyncParkingData() {
        // given
        when(parkingApiClient.getPrkSttusInfo(anyInt(), anyInt())).thenReturn(sttusInfoResponse);
        when(parkingApiClient.getPrkOprInfo(anyInt(), anyInt())).thenReturn(oprInfoResponse);
        when(parkingApiClient.getPrkRealtimeInfo(anyInt(), anyInt())).thenReturn(realtimeInfoResponse);

        // when
        parkingDataSyncService.syncParkingData();

        // then
        verify(parkingInfoMapper, times(1)).insertParkingInfo(any(ParkingInfo.class));
        verify(parkingOperationMapper, times(1)).insertParkingOperation(any(ParkingOperation.class));
        verify(parkingRealtimeMapper, times(1)).insertParkingRealtime(any(ParkingRealtime.class));
    }

    @Test
    void testSyncRealtimeData() {
        // given
        when(parkingApiClient.getPrkRealtimeInfo(anyInt(), anyInt())).thenReturn(realtimeInfoResponse);

        // when
        parkingDataSyncService.syncRealtimeData();

        // then
        verify(parkingRealtimeMapper, times(1)).insertParkingRealtime(any(ParkingRealtime.class));
    }

    private PrkSttusInfoResponse.PrkSttusInfo createSttusInfoItem(String prkCenterId, String prkName, double lat, double lng) {
        PrkSttusInfoResponse.PrkSttusInfo item = new PrkSttusInfoResponse.PrkSttusInfo();
        item.setPrkCenterId(prkCenterId);
        item.setPrkName(prkName);
        item.setLatitude(String.valueOf(lat));
        item.setLongitude(String.valueOf(lng));
        return item;
    }

    private PrkOprInfoResponse.PrkOprInfo createOprInfoItem(String prkCenterId, String opertnBsFreeTime, 
                                                         String parkingChrgeBsTime, String parkingChrgeFreeTime, 
                                                         String parkingChrge) {
        PrkOprInfoResponse.PrkOprInfo item = new PrkOprInfoResponse.PrkOprInfo();
        item.setPrkCenterId(prkCenterId);
        item.setOpertnBsFreeTime(opertnBsFreeTime);
        item.setParkingChrgeBsTime(parkingChrgeBsTime);
        item.setParkingChrgeFreeTime(parkingChrgeFreeTime);
        item.setParkingChrge(parkingChrge);
        return item;
    }

    private PrkRealtimeInfoResponse.PrkRealtimeInfo createRealtimeInfoItem(String prkCenterId, 
                                                                        int totalParkingLots, int availableParkingLots) {
        PrkRealtimeInfoResponse.PrkRealtimeInfo item = new PrkRealtimeInfoResponse.PrkRealtimeInfo();
        item.setPrkCenterId(prkCenterId);
        item.setPkfcParkingLotsTotal(String.valueOf(totalParkingLots));
        item.setPkfcAvailableParkingLotsTotal(String.valueOf(availableParkingLots));
        return item;
    }
} 