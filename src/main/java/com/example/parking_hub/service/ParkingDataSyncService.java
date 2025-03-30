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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주차장 데이터 동기화 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ParkingDataSyncService {

    private final ParkingApiClient parkingApiClient;
    private final ParkingInfoMapper parkingInfoMapper;
    private final ParkingOperationMapper parkingOperationMapper;
    private final ParkingRealtimeMapper parkingRealtimeMapper;

    /**
     * 주차장 데이터 동기화
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void syncParkingData() {
        log.info("주차장 데이터 동기화 시작");
        
        try {
            // 1. 기본정보 조회 및 저장
            syncParkingBasicInfo();
            
            // 2. 운영정보 조회 및 저장
            syncParkingOperationInfo();
            
            log.info("주차장 데이터 동기화 완료");
        } catch (Exception e) {
            log.error("주차장 데이터 동기화 중 오류 발생", e);
        }
    }

    /**
     * 주차장 기본정보 동기화
     */
    @Transactional
    public void syncParkingBasicInfo() {
        log.info("주차장 기본정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 1000;
            int totalProcessed = 0;
            
            while (true) {
                PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfo(pageNo, numOfRows);
                
                if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                    log.info("주차장 기본정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인
                if (!"00".equals(response.getResultCode())) {
                    log.error("주차장 기본정보 API 오류: {}", response.getResultMsg());
                    break;
                }

                log.info("주차장 기본정보 페이지 {}: {} 건", pageNo, response.getItems().size());

                // 새로운 데이터 처리
                for (PrkSttusInfoResponse.PrkSttusInfo item : response.getItems()) {
                    try {
                        ParkingInfo parkingInfo = convertToParkingInfo(item);
                        parkingInfoMapper.insertOrUpdateParkingInfo(parkingInfo);
                        totalProcessed++;
                    } catch (Exception e) {
                        log.error("주차장 기본정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (response.getItems().size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            log.info("주차장 기본정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            log.error("주차장 기본정보 동기화 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 주차장 운영정보 동기화
     */
    @Transactional
    public void syncParkingOperationInfo() {
        log.info("주차장 운영정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 1000;
            int totalProcessed = 0;
            
            while (true) {
                PrkOprInfoResponse response = parkingApiClient.getPrkOprInfo(pageNo, numOfRows);
                
                if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                    log.info("주차장 운영정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인
                if (!"00".equals(response.getResultCode())) {
                    log.error("주차장 운영정보 API 오류: {}", response.getResultMsg());
                    break;
                }

                log.info("주차장 운영정보 페이지 {}: {} 건", pageNo, response.getItems().size());

                // 새로운 데이터 처리
                for (PrkOprInfoResponse.PrkOprInfo item : response.getItems()) {
                    try {
                        ParkingOperation operation = convertToParkingOperation(item);
                        parkingOperationMapper.insertOrUpdateParkingOperation(operation);
                        totalProcessed++;
                    } catch (Exception e) {
                        log.error("주차장 운영정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (response.getItems().size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            log.info("주차장 운영정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            log.error("주차장 운영정보 동기화 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 주차장 실시간 정보 동기화
     * 5분마다 실행
     */
    @Scheduled(fixedRate = 300000) // 5분
    @Transactional
    public void syncParkingRealtimeInfo() {
        log.info("주차장 실시간 정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 1000;
            int totalProcessed = 0;
            
            while (true) {
                PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);
                
                if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                    log.info("주차장 실시간 정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인
                if (!"00".equals(response.getResultCode())) {
                    log.error("주차장 실시간 정보 API 오류: {}", response.getResultMsg());
                    break;
                }

                log.info("주차장 실시간 정보 페이지 {}: {} 건", pageNo, response.getItems().size());

                // 실시간 정보 업데이트
                for (PrkRealtimeInfoResponse.PrkRealtimeInfo item : response.getItems()) {
                    try {
                        ParkingRealtime realtime = convertToParkingRealtime(item);
                        parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
                        totalProcessed++;
                    } catch (Exception e) {
                        log.error("주차장 실시간 정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (response.getItems().size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            log.info("주차장 실시간 정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            log.error("주차장 실시간 정보 동기화 중 오류 발생", e);
        }
    }

    /**
     * API 응답을 ParkingInfo 엔티티로 변환
     */
    private ParkingInfo convertToParkingInfo(PrkSttusInfoResponse.PrkSttusInfo info) {
        ParkingInfo parkingInfo = new ParkingInfo();
        parkingInfo.setPrkCenterId(info.getPrkCenterId());
        parkingInfo.setPrkPlceNm(info.getPrkCenterNm());
        parkingInfo.setPrkPlceAdres(info.getRdnmadr() != null ? info.getRdnmadr() : info.getLnmadr());
        
        try {
            // 위도, 경도 설정 (null 체크)
            if (info.getLatitude() != null) {
                parkingInfo.setPrkPlceEntrcLa(info.getLatitude());
            }
            if (info.getLongitude() != null) {
                parkingInfo.setPrkPlceEntrcLo(info.getLongitude());
            }
            
            // 주차 가능 대수
            if (info.getParkingLotCount() != null) {
                parkingInfo.setPrkCmprtCo(info.getParkingLotCount());
            }
        } catch (Exception e) {
            log.warn("주차장 기본정보 변환 중 오류: {}", info.getPrkCenterId(), e);
        }
        
        return parkingInfo;
    }

    /**
     * API 응답을 ParkingOperation 엔티티로 변환
     */
    private ParkingOperation convertToParkingOperation(PrkOprInfoResponse.PrkOprInfo info) {
        ParkingOperation operation = new ParkingOperation();
        operation.setPrkCenterId(info.getPrkCenterId());
        
        try {
            // 무료 운영 시간 설정 (문자열 → 정수 변환)
            if (info.getOpertnBsFreeTime() != null && !info.getOpertnBsFreeTime().trim().isEmpty()) {
                int freeTime = 0;
                try {
                    freeTime = Integer.parseInt(info.getOpertnBsFreeTime().trim());
                } catch (NumberFormatException e) {
                    // 기본값 사용
                    freeTime = 0;
                }
                operation.setOpertnBsFreeTime(freeTime);
            }
        } catch (Exception e) {
            log.warn("주차장 운영정보 변환 중 오류: {}", info.getPrkCenterId(), e);
        }
        
        return operation;
    }

    /**
     * API 응답을 ParkingRealtime 엔티티로 변환
     */
    private ParkingRealtime convertToParkingRealtime(PrkRealtimeInfoResponse.PrkRealtimeInfo info) {
        ParkingRealtime realtime = new ParkingRealtime();
        realtime.setPrkCenterId(info.getPrkCenterId());
        
        try {
            // 총 주차면, 가용 주차면 설정
            if (info.getPkfcParkingLotsTotal() != null) {
                realtime.setPkfcParkingLotsTotal(info.getPkfcParkingLotsTotal());
            }
            if (info.getPkfcAvailableParkingLotsTotal() != null) {
                realtime.setPkfcAvailableParkingLotsTotal(info.getPkfcAvailableParkingLotsTotal());
            }
        } catch (Exception e) {
            log.warn("주차장 실시간정보 변환 중 오류: {}", info.getPrkCenterId(), e);
        }
        
        // 업데이트 시간 설정
        realtime.setUpdatedAt(LocalDateTime.now());
        
        return realtime;
    }
} 