package com.example.parking_hub.client;

import com.example.parking_hub.dto.api.PrkSttusInfoResponse;
import com.example.parking_hub.dto.api.PrkOprInfoResponse;
import com.example.parking_hub.dto.api.PrkRealtimeInfoResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class KotsaParkingApiClientTest {

    @Autowired
    private ParkingApiClient parkingApiClient;

    @Test
    public void testGetPrkSttusInfo() {
        // given
        int pageNo = 1;
        int numOfRows = 10;

        // when
        PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfo(pageNo, numOfRows);

        // then
        assertNotNull(response);
        assertEquals("00", response.getResultCode());
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty());
    }

    @Test
    public void testGetPrkOprInfo() {
        // given
        int pageNo = 1;
        int numOfRows = 10;

        // when
        PrkOprInfoResponse response = parkingApiClient.getPrkOprInfo(pageNo, numOfRows);

        // then
        assertNotNull(response);
        assertEquals("00", response.getResultCode());
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty());
    }

    @Test
    public void testGetPrkRealtimeInfo() {
        // given
        int pageNo = 1;
        int numOfRows = 10;

        // when
        PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);

        // then
        assertNotNull(response);
        assertEquals("00", response.getResultCode());
        assertNotNull(response.getItems());
        assertFalse(response.getItems().isEmpty());
    }
} 