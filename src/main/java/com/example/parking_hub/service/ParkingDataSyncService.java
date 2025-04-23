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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 주차장 데이터 동기화 서비스
 */
@Service
public class ParkingDataSyncService implements ApplicationListener<ApplicationReadyEvent> {

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
     * 애플리케이션이 완전히 시작된 후 주차장 데이터 초기화를 수행합니다.
     * ApplicationReadyEvent는 모든 빈이 초기화되고 애플리케이션이 요청을 처리할 준비가 된 상태에서 발생합니다.
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initializeParkingData();
    }

    /**
     * 애플리케이션 시작 시 필요한 데이터 초기화
     * 데이터베이스에 주차장 정보가 없는 경우 기본 데이터를 로드합니다.
     */
    public void initializeParkingData() {
        logger.info("주차장 데이터 초기화 검사 시작");
        
        try {
            // 기존 데이터 확인
            int infoCount = parkingInfoMapper.countParkingInfo();
            int operationCount = parkingOperationMapper.countParkingOperation();
            
            if (infoCount == 0) {
                logger.info("주차장 기본정보가 없습니다. 데이터를 로드합니다.");
                syncParkingBasicInfo();
            } else {
                logger.info("주차장 기본정보가 이미 존재합니다: {} 건", infoCount);
            }
            
            if (operationCount == 0) {
                logger.info("주차장 운영정보가 없습니다. 데이터를 로드합니다.");
                syncParkingOperationInfo();
            } else {
                logger.info("주차장 운영정보가 이미 존재합니다: {} 건", operationCount);
            }
            
            // 실시간 데이터는 항상 최신 상태를 유지해야 하므로 초기 로드 실행
            logger.info("실시간 주차장 정보를 초기화합니다.");
            syncParkingRealtimeInfo();
            
            logger.info("주차장 데이터 초기화 완료");
        } catch (Exception e) {
            logger.error("주차장 데이터 초기화 중 오류 발생", e);
            // 초기화 실패해도 애플리케이션은 계속 실행되어야 함
        }
    }

    /**
     * 주차장 데이터 전체 동기화
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

                // 페이지별로 별도 트랜잭션으로 처리
                int processed = processBasicInfoPage(items);
                totalProcessed += processed;

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
     * 각 페이지의 기본정보 처리 (별도 트랜잭션)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int processBasicInfoPage(List<PrkSttusInfoResponse.PrkSttusInfo> items) {
        int processed = 0;
        for (PrkSttusInfoResponse.PrkSttusInfo item : items) {
            try {
                ParkingInfo parkingInfo = convertToParkingInfo(item);
                parkingInfoMapper.insertOrUpdateParkingInfo(parkingInfo);
                processed++;
            } catch (Exception e) {
                logger.error("주차장 기본정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                // 개별 항목 오류는 전체 처리에 영향 없음
            }
        }
        return processed;
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

                // 페이지별로 별도 트랜잭션으로 처리
                int processed = processOperationInfoPage(items);
                totalProcessed += processed;

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
     * 각 페이지의 운영정보 처리 (별도 트랜잭션)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int processOperationInfoPage(List<PrkOprInfoResponse.PrkOprInfo> items) {
        int processed = 0;
        for (PrkOprInfoResponse.PrkOprInfo item : items) {
            try {
                ParkingOperation operation = convertToParkingOperation(item);
                parkingOperationMapper.insertOrUpdateParkingOperation(operation);
                processed++;
            } catch (Exception e) {
                logger.error("주차장 운영정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                // 개별 항목 오류는 전체 처리에 영향 없음
            }
        }
        return processed;
    }

    /**
     * 주차장 실시간 정보 동기화
     * 30분마다 실행되며, 실시간 주차장 정보를 가져와 DB에 갱신
     */
    @Scheduled(fixedDelay = 1800000) // fixedRate에서 fixedDelay로 변경 - 30분 간격 유지
    @Transactional
    public void syncParkingRealtimeInfo() {
        logger.info("주차장 실시간 정보 동기화 시작");
        
        try {
            int pageNo = 1;
            int numOfRows = 500;
            int totalProcessed = 0;
            int maxRetries = 3;
            int retryCount = 0;
            boolean success = false;
            
            while (!success && retryCount < maxRetries) {
                try {
                    retryCount++;
                    logger.info("실시간 정보 동기화 시도 {}/{}", retryCount, maxRetries);
                    
                    PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);
                    List<PrkRealtimeInfoResponse.PrkRealtimeItem> items = getItemsFromResponse(response);
                    
                    if (response == null || items == null || items.isEmpty()) {
                        logger.info("주차장 실시간 정보 더 이상 데이터 없음");
                        // 데이터가 없어도 성공으로 간주
                        success = true;
                        break;
                    }
                    
                    // API 응답 코드 확인 - SUCCESS 메시지는 정상 처리
                    String resultMsg = getResultMsgFromResponse(response);
                    if (resultMsg != null && "SUCCESS".equals(resultMsg)) {
                        // 정상 응답이므로 처리 계속 진행
                        logger.debug("주차장 실시간 정보 API 응답 정상: {}", resultMsg);
                    } else if (!"00".equals(getResultCodeFromResponse(response))) {
                        // 실제 오류인 경우 재시도
                        logger.error("주차장 실시간 정보 API 오류: {}, 재시도 {}/{}", resultMsg, retryCount, maxRetries);
                        if (retryCount >= maxRetries) {
                            break;
                        }
                        continue;
                    }

                    logger.info("주차장 실시간 정보 페이지 {}: {} 건", pageNo, items.size());

                    // 페이지별로 별도 트랜잭션으로 처리
                    int processed = processRealtimeInfoPage(items);
                    totalProcessed += processed;

                    // 다음 페이지가 없으면 종료
                    if (items.size() < numOfRows) {
                        success = true;
                        break;
                    }

                    pageNo++;
                    success = true; // 이 지점까지 오면 현재 페이지 처리 성공
                } catch (Exception e) {
                    logger.error("주차장 실시간 정보 동기화 중 오류 발생, 재시도 {}/{}", retryCount, maxRetries, e);
                    // 일시적인 네트워크 오류 등에 대비한 재시도
                    if (retryCount >= maxRetries) {
                        throw e; // 최대 재시도 횟수 초과시 예외 던짐
                    }
                    // 재시도 전 약간의 딜레이
                    try {
                        Thread.sleep(2000); // 2초 대기
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            // 최종 결과 로깅
            if (success) {
                logger.info("주차장 실시간 정보 동기화 완료: 총 {} 건 처리", totalProcessed);
            } else {
                logger.error("주차장 실시간 정보 동기화 실패: 최대 재시도 횟수 초과");
            }
            
        } catch (Exception e) {
            logger.error("주차장 실시간 정보 동기화 중 예외 발생", e);
            // 최종 예외는 로깅만 하고 던지지 않음 - 다음 스케줄링 실행 보장
        }
    }

    /**
     * 각 페이지의 실시간 정보 처리 (별도 트랜잭션)
     * 참조하는 주차장 정보가 없는 경우 해당 정보를 먼저 가져오는 로직 추가
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int processRealtimeInfoPage(List<PrkRealtimeInfoResponse.PrkRealtimeItem> items) {
        int processed = 0;
        for (PrkRealtimeInfoResponse.PrkRealtimeItem item : items) {
            try {
                String parkingId = item.getPrkCenterId();
                
                // 주차장 ID로 기본 정보가 있는지 확인
                ParkingInfo existingInfo = parkingInfoMapper.findParkingInfoById(parkingId);
                
                // 기본 정보가 없는 경우, 해당 주차장의 기본 정보부터 먼저 가져옴
                if (existingInfo == null) {
                    logger.info("실시간 정보의 주차장 기본 정보가 없음 (ID: {}), 기본 정보 가져오기 시도", parkingId);
                    try {
                        // 단일 주차장 정보 조회 API 호출 (여기서는 예시로 일반 API를 사용)
                        PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfoById(parkingId);
                        List<PrkSttusInfoResponse.PrkSttusInfo> infoItems = getItemsFromResponse(response);
                        
                        if (infoItems != null && !infoItems.isEmpty()) {
                            ParkingInfo parkingInfo = convertToParkingInfo(infoItems.get(0));
                            parkingInfoMapper.insertOrUpdateParkingInfo(parkingInfo);
                            logger.info("주차장 기본 정보 추가 성공 (ID: {})", parkingId);
                        }
                    } catch (Exception e) {
                        logger.error("주차장 기본 정보 조회 실패 (ID: {})", parkingId, e);
                        // 기본 정보 조회 실패해도 실시간 정보는 처리 시도
                    }
                }
                
                // 실시간 정보 처리
                ParkingRealtime realtime = convertToParkingRealtime(item);
                parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
                processed++;
            } catch (Exception e) {
                logger.error("주차장 실시간 정보 처리 중 오류: {}", item.getPrkCenterId(), e);
                // 개별 항목 오류는 전체 처리에 영향 없음
            }
        }
        return processed;
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