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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주차장 데이터 동기화 서비스
 */
@Service
public class ParkingDataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingDataSyncService.class);

    private final ParkingApiClient parkingApiClient;
    private final ParkingInfoMapper parkingInfoMapper;
    private final ParkingOperationMapper parkingOperationMapper;
    private final ParkingRealtimeMapper parkingRealtimeMapper;

    @Autowired
    public ParkingDataSyncService(ParkingApiClient parkingApiClient,
                                ParkingInfoMapper parkingInfoMapper,
                                ParkingOperationMapper parkingOperationMapper,
                                ParkingRealtimeMapper parkingRealtimeMapper) {
        this.parkingApiClient = parkingApiClient;
        this.parkingInfoMapper = parkingInfoMapper;
        this.parkingOperationMapper = parkingOperationMapper;
        this.parkingRealtimeMapper = parkingRealtimeMapper;
    }

    /**
     * 주차장 데이터 동기화
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void syncParkingData() {
        logger.info("주차장 데이터 동기화 시작");
        
        try {
            // 1. 기본정보 조회 및 저장
            syncParkingBasicInfo();
            
            // 2. 운영정보 조회 및 저장
            syncParkingOperationInfo();
            
            logger.info("주차장 데이터 동기화 완료");
        } catch (Exception e) {
            logger.error("주차장 데이터 동기화 중 오류 발생", e);
        }
    }

    /**
     * 주차장 기본정보 동기화
     */
    @Transactional
    public void syncParkingBasicInfo() {
        logger.info("주차장 기본정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 1000;
            int totalProcessed = 0;
            
            while (true) {
                PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfo(pageNo, numOfRows);
                List<PrkSttusInfoResponse.PrkSttusInfo> items = getItemsFromResponse(response);
                
                if (response == null || items == null || items.isEmpty()) {
                    logger.info("주차장 기본정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인
                if (!"00".equals(getResultCodeFromResponse(response))) {
                    logger.error("주차장 기본정보 API 오류: {}", getResultMsgFromResponse(response));
                    break;
                }

                logger.info("주차장 기본정보 페이지 {}: {} 건", pageNo, items.size());

                // 새로운 데이터 처리
                for (PrkSttusInfoResponse.PrkSttusInfo item : items) {
                    try {
                        ParkingInfo parkingInfo = convertToParkingInfo(item);
                        parkingInfoMapper.insertOrUpdateParkingInfo(parkingInfo);
                        totalProcessed++;
                    } catch (Exception e) {
                        logger.error("주차장 기본정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            logger.info("주차장 기본정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            logger.error("주차장 기본정보 동기화 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 주차장 운영정보 동기화
     */
    @Transactional
    public void syncParkingOperationInfo() {
        logger.info("주차장 운영정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 1000;
            int totalProcessed = 0;
            
            while (true) {
                PrkOprInfoResponse response = parkingApiClient.getPrkOprInfo(pageNo, numOfRows);
                List<PrkOprInfoResponse.PrkOprInfo> items = getItemsFromResponse(response);
                
                if (response == null || items == null || items.isEmpty()) {
                    logger.info("주차장 운영정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인
                if (!"00".equals(getResultCodeFromResponse(response))) {
                    logger.error("주차장 운영정보 API 오류: {}", getResultMsgFromResponse(response));
                    break;
                }

                logger.info("주차장 운영정보 페이지 {}: {} 건", pageNo, items.size());

                // 새로운 데이터 처리
                for (PrkOprInfoResponse.PrkOprInfo item : items) {
                    try {
                        ParkingOperation operation = convertToParkingOperation(item);
                        parkingOperationMapper.insertOrUpdateParkingOperation(operation);
                        totalProcessed++;
                    } catch (Exception e) {
                        logger.error("주차장 운영정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            logger.info("주차장 운영정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            logger.error("주차장 운영정보 동기화 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 주차장 실시간 정보 동기화
     * 5분마다 실행
     */
//    @Scheduled(fixedRate = 300000) // 5분
    @Transactional
    public void syncParkingRealtimeInfo() {
        logger.info("주차장 실시간 정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 10;
            int totalProcessed = 0;
            
            while (true) {
                PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);
                List<PrkRealtimeInfoResponse.PrkRealtimeItem> items = getItemsFromResponse(response);
                
                if (response == null || items == null || items.isEmpty()) {
                    logger.info("주차장 실시간 정보 더 이상 데이터 없음");
                    break;
                }
                
                // API 응답 코드 확인 - SUCCESS 메시지는 정상 처리
                String resultMsg = getResultMsgFromResponse(response);
                if (resultMsg != null && "SUCCESS".equals(resultMsg)) {
                    // 정상 응답이므로 처리 계속 진행
                    logger.debug("주차장 실시간 정보 API 응답 정상: {}", resultMsg);
                } else if (!"00".equals(getResultCodeFromResponse(response))) {
                    // 실제 오류인 경우만 로깅
                    logger.error("주차장 실시간 정보 API 오류: {}", resultMsg);
                    break;
                }

                logger.info("주차장 실시간 정보 페이지 {}: {} 건", pageNo, items.size());

                // 실시간 정보 업데이트
                for (PrkRealtimeInfoResponse.PrkRealtimeItem item : items) {
                    try {
                        ParkingRealtime realtime = convertToParkingRealtime(item);
                        parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
                        totalProcessed++;
                    } catch (Exception e) {
                        logger.error("주차장 실시간 정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                    }
                }

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    break;
                }

                pageNo++;
            }
            
            logger.info("주차장 실시간 정보 동기화 완료: 총 {} 건 처리", totalProcessed);
        } catch (Exception e) {
            logger.error("주차장 실시간 정보 동기화 중 오류 발생", e);
        }
    }
    
    // 응답으로부터 결과 코드 추출 헬퍼 메서드 (제네릭 변환)
    private <T> String getResultCodeFromResponse(T response) {
        if (response instanceof PrkSttusInfoResponse) {
            PrkSttusInfoResponse r = (PrkSttusInfoResponse) response;
            return r.getResultCode();
        } else if (response instanceof PrkOprInfoResponse) {
            PrkOprInfoResponse r = (PrkOprInfoResponse) response;
            return r.getResultCode();
        } else if (response instanceof PrkRealtimeInfoResponse) {
            PrkRealtimeInfoResponse r = (PrkRealtimeInfoResponse) response;
            return r.getResultCode();
        }
        return null;
    }
    
    // 응답으로부터 결과 메시지 추출 헬퍼 메서드 (제네릭 변환)
    private <T> String getResultMsgFromResponse(T response) {
        if (response instanceof PrkSttusInfoResponse) {
            PrkSttusInfoResponse r = (PrkSttusInfoResponse) response;
            return r.getResultMsg();
        } else if (response instanceof PrkOprInfoResponse) {
            PrkOprInfoResponse r = (PrkOprInfoResponse) response;
            return r.getResultMsg();
        } else if (response instanceof PrkRealtimeInfoResponse) {
            PrkRealtimeInfoResponse r = (PrkRealtimeInfoResponse) response;
            return r.getResultMsg();
        }
        return null;
    }
    
    // 응답으로부터 아이템 목록 추출 헬퍼 메서드 (제네릭 변환)
    @SuppressWarnings("unchecked")
    private <T, R> List<R> getItemsFromResponse(T response) {
        if (response instanceof PrkSttusInfoResponse) {
            PrkSttusInfoResponse r = (PrkSttusInfoResponse) response;
            if (r.getItems() != null) {
                return (List<R>) r.getItems();
            }
        } else if (response instanceof PrkOprInfoResponse) {
            PrkOprInfoResponse r = (PrkOprInfoResponse) response;
            if (r.getItems() != null) {
                return (List<R>) r.getItems();
            }
        } else if (response instanceof PrkRealtimeInfoResponse) {
            PrkRealtimeInfoResponse r = (PrkRealtimeInfoResponse) response;
            if (r.getItems() != null) {
                logger.debug("PrkRealtimeInfo 아이템 추출: {}", r.getItems().size());
                return (List<R>) r.getItems();
            }
        }
        return Collections.emptyList();
    }

    /**
     * API 응답을 ParkingInfo 엔티티로 변환
     */
    private ParkingInfo convertToParkingInfo(PrkSttusInfoResponse.PrkSttusInfo item) {
        ParkingInfo parkingInfo = new ParkingInfo();
        parkingInfo.setPrkCenterId(item.getPrkCenterId());
        parkingInfo.setPrkPlceNm(item.getPrkPlceNm());
        parkingInfo.setPrkPlceAdres(item.getPrkPlceAdres());
        
        // 위도/경도 값 설정 (Double 타입으로 변경됨)
        try {
            if (item.getLatitude() != null) {
                parkingInfo.setPrkPlceEntrcLa(item.getLatitude());
            }
        } catch (Exception e) {
            logger.warn("위도 설정 오류: {}", item.getPrkCenterId(), e);
        }
        
        try {
            if (item.getLongitude() != null) {
                parkingInfo.setPrkPlceEntrcLo(item.getLongitude());
            }
        } catch (Exception e) {
            logger.warn("경도 설정 오류: {}", item.getPrkCenterId(), e);
        }
        
        // 주차면수 설정 (Integer 타입으로 변경됨)
        try {
            if (item.getPrkCmprtCo() != null) {
                parkingInfo.setPrkCmprtCo(item.getPrkCmprtCo());
            }
        } catch (Exception e) {
            logger.warn("주차면수 설정 오류: {}", item.getPrkCenterId(), e);
        }
        
        return parkingInfo;
    }

    /**
     * API 응답을 ParkingOperation 엔티티로 변환
     */
    private ParkingOperation convertToParkingOperation(PrkOprInfoResponse.PrkOprInfo item) {
        ParkingOperation operation = new ParkingOperation();
        operation.setPrkCenterId(item.getPrkCenterId());
        
        // 무료 주차 시간 설정
        try {
            if (item.getOpertnBsFreeTime() != null && !item.getOpertnBsFreeTime().isEmpty()) {
                operation.setOpertnBsFreeTime(Integer.parseInt(item.getOpertnBsFreeTime()));
            }
        } catch (NumberFormatException e) {
            logger.warn("무료 주차 시간 변환 오류: {} - {}", item.getPrkCenterId(), item.getOpertnBsFreeTime());
        }
        
        // 추가 필드 설정
        if (item.getParkingChrgeBsTime() != null) {
            operation.setParkingChrgeBsTime(item.getParkingChrgeBsTime());
        }
        
        if (item.getParkingChrgeBsChrg() != null) {
            operation.setParkingChrgeBsChrg(item.getParkingChrgeBsChrg());
        }
        
        if (item.getOperationDayInfo() != null) {
            operation.setOperationDayInfo(item.getOperationDayInfo());
        }
        
        return operation;
    }

    /**
     * API 응답을 ParkingRealtime 엔티티로 변환
     */
    private ParkingRealtime convertToParkingRealtime(PrkRealtimeInfoResponse.PrkRealtimeItem item) {
        ParkingRealtime realtime = new ParkingRealtime();
        realtime.setPrkCenterId(item.getPrkCenterId());
        
        // 전체 주차면 수 (Integer 타입으로 변경됨)
        try {
            if (item.getPkfcParkingLotsTotal() != null) {
                realtime.setPkfcParkingLotsTotal(item.getPkfcParkingLotsTotal());
            }
        } catch (Exception e) {
            logger.warn("전체 주차면 수 설정 오류: {}", item.getPrkCenterId(), e);
        }
        
        // 가용 주차면 수 (Integer 타입으로 변경됨)
        try {
            if (item.getPkfcAvailableParkingLotsTotal() != null) {
                realtime.setPkfcAvailableParkingLotsTotal(item.getPkfcAvailableParkingLotsTotal());
            }
        } catch (Exception e) {
            logger.warn("가용 주차면 수 설정 오류: {}", item.getPrkCenterId(), e);
        }
        
        // 업데이트 시간 설정
        realtime.setUpdatedAt(LocalDateTime.now());
        
        return realtime;
    }
} 