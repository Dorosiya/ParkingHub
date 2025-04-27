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

import java.util.ArrayList;

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
        logger.info("주차장 데이터 초기화 시작");
        
        try {
            // 기본 정보 로드
            int parkingInfoCount = parkingInfoMapper.countParkingInfo();
            logger.info("현재 주차장 기본 정보 수: {}", parkingInfoCount);
            
            // 기본 정보도 항상 로드하도록 변경 (조건 제거)
            logger.info("주차장 기본 정보를 로드합니다.");
            try {
                syncParkingBasicInfo();
                logger.info("주차장 기본 정보 로드 완료");
                
                // 락 경쟁 감소를 위한 대기 시간
                Thread.sleep(5000);
            } catch (Exception e) {
                logger.error("주차장 기본 정보 로드 중 오류 발생: {}", e.getMessage());
                // 기본 정보 로드 실패 시에도 다음 단계 진행
            }
            
            // 운영 정보 로드
            int parkingOperationCount = parkingOperationMapper.countParkingOperation();
            logger.info("현재 주차장 운영 정보 수: {}", parkingOperationCount);
            
            // 운영 정보는 항상 로드하도록 변경 (조건 제거)
            logger.info("주차장 운영 정보를 로드합니다.");
            try {
                syncParkingOperationInfo();
                logger.info("주차장 운영 정보 로드 완료");
                
                // 락 경쟁 감소를 위한 대기 시간
                Thread.sleep(5000);
            } catch (Exception e) {
                logger.error("주차장 운영 정보 로드 중 오류 발생: {}", e.getMessage());
                // 운영 정보 로드 실패 시에도 다음 단계 진행
            }
            
            // 실시간 정보 로드
            int parkingRealtimeCount = parkingRealtimeMapper.countParkingRealtime();
            logger.info("현재 주차장 실시간 정보 수: {}", parkingRealtimeCount);
            
            // 실시간 정보는 어떤 경우에도 최신 데이터를 로드
            logger.info("주차장 실시간 정보를 갱신합니다.");
            try {
                syncParkingRealtimeInfo();
                logger.info("주차장 실시간 정보 로드 완료");
            } catch (Exception e) {
                logger.error("주차장 실시간 정보 로드 중 오류 발생: {}", e.getMessage());
            }
            
            logger.info("주차장 데이터 초기화 완료");
        } catch (Exception e) {
            logger.error("주차장 데이터 초기화 중 오류 발생", e);
        }
    }

    /**
     * 유효하지 않은 데이터를 정리합니다.
     * ID 불일치 문제로 인해 발생한 데이터를 삭제합니다.
     */
    @Transactional
    public void cleanupInvalidData() {
        logger.info("유효하지 않은 주차장 데이터 정리 시작");
        try {
            // 실시간 정보 테이블 초기화 (외래키 제약조건으로 인해 먼저 삭제)
            parkingRealtimeMapper.deleteAllParkingRealtime();
            logger.info("실시간 정보 테이블 초기화 완료");
            
            // 운영 정보 테이블 초기화
            parkingOperationMapper.deleteAllParkingOperation();
            logger.info("운영 정보 테이블 초기화 완료");
            
            // 기본 정보 테이블 초기화
            parkingInfoMapper.deleteAllParkingInfo();
            logger.info("기본 정보 테이블 초기화 완료");
            
            logger.info("유효하지 않은 주차장 데이터 정리 완료");
        } catch (Exception e) {
            logger.error("데이터 정리 중 오류 발생", e);
            throw e; // 트랜잭션 롤백을 위해 예외 다시 던지기
        }
    }

    /**
     * 주차장 데이터 전체 동기화
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void syncParkingData() {
        logger.info("주차장 데이터 동기화 시작");
        
        try {
            // 1. 기본정보 조회 및 저장
            syncParkingBasicInfo();
            
            // 2. 운영정보 조회 및 저장
            syncParkingOperationInfo();
            
            // 3. 실시간 정보 조회 및 저장
            syncParkingRealtimeInfo();
            
            logger.info("주차장 데이터 동기화 완료");
        } catch (Exception e) {
            logger.error("주차장 데이터 동기화 중 오류 발생", e);
        }
    }

    /**
     * 주차장 기본정보 동기화
     */
    public void syncParkingBasicInfo() {
        logger.info("주차장 기본정보 동기화 시작");
        
        int maxRetries = 3; // 최대 재시도 횟수
        int retryCount = 0;
        int pageNo = 1;
        int numOfRows = 200; // 500에서 200으로 변경
        int totalProcessed = 0;
        boolean isConnectionError = false;
        
        while (true) {
            try {
                if (isConnectionError) {
                    // 연결 오류 발생 시 잠시 대기 후 재시도
                    retryCount++;
                    if (retryCount > maxRetries) {
                        logger.error("최대 재시도 횟수({})를 초과하여 작업을 중단합니다.", maxRetries);
                        break;
                    }
                    
                    int waitTime = retryCount * 5000; // 점진적으로 대기 시간 증가 (5초, 10초, 15초)
                    logger.info("데이터베이스 연결 오류 후 재시도 중 ({}/{}), {}ms 대기 후 재시도합니다.", 
                        retryCount, maxRetries, waitTime);
                    
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    
                    isConnectionError = false; // 재시도 플래그 초기화
                }
                
                logger.info("주차장 기본정보 페이지 {} 요청 시작, 건수: {}", pageNo, numOfRows);
                PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfo(pageNo, numOfRows);
                
                // API 응답이 NULL인지 확인
                if (response == null) {
                    logger.error("주차장 기본정보 API 응답이 NULL입니다. 네트워크 또는 API 서버 오류일 수 있습니다.");
                    break;
                }
                
                // 응답 코드 확인
                String resultCode = getResultCodeFromResponse(response);
                String resultMsg = getResultMsgFromResponse(response);
                logger.info("주차장 기본정보 API 응답 - 코드: {}, 메시지: {}", resultCode, resultMsg);
                
                if (!"00".equals(resultCode) && !"SUCCESS".equals(resultMsg)) {
                    logger.error("주차장 기본정보 API 오류: {}", resultMsg);
                    break;
                }
                
                List<PrkSttusInfoResponse.PrkSttusInfo> items = getItemsFromResponse(response);
                
                if (items == null || items.isEmpty()) {
                    logger.info("주차장 기본정보에 데이터가 없습니다.");
                    break;
                }

                logger.info("주차장 기본정보 페이지 {}: {} 건 조회됨", pageNo, items.size());

                // 페이지별로 별도 트랜잭션으로 처리
                int processed = processBasicInfoPage(items);
                totalProcessed += processed;
                logger.info("주차장 기본정보 페이지 {} 처리 완료: {} 건 처리됨, 총 처리: {}", pageNo, processed, totalProcessed);

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    logger.info("요청한 건수({})보다 적은 응답({})이 왔으므로 마지막 페이지로 간주합니다.", numOfRows, items.size());
                    break;
                }

                pageNo++;
                retryCount = 0; // 성공 시 재시도 카운트 초기화
                
                // 다음 페이지 요청 전 잠시 대기 (API 부하 감소)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                if (isConnectionError(e)) {
                    isConnectionError = true;
                    logger.error("데이터베이스 연결 오류 발생: {}", e.getMessage());
                    continue; // 재시도 로직으로 이동
                } else {
                    logger.error("주차장 기본정보 동기화 중 오류 발생: {}", e.getMessage());
                    break;
                }
            }
        }
        
        logger.info("주차장 기본정보 동기화 완료: 총 {} 건 처리", totalProcessed);
    }

    /**
     * 각 페이지의 기본정보 처리
     * 트랜잭션 없이 배치 단위로 나눠서 처리
     */
    public int processBasicInfoPage(List<PrkSttusInfoResponse.PrkSttusInfo> items) {
        int processed = 0;
        int batchSize = 10; // 배치 크기 10으로 설정
        
        List<List<PrkSttusInfoResponse.PrkSttusInfo>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(items.subList(i, Math.min(i + batchSize, items.size())));
        }
        
        logger.info("기본 정보 배치 처리 시작 - 총 {}개 배치", batches.size());
        
        for (List<PrkSttusInfoResponse.PrkSttusInfo> batch : batches) {
            logger.info("기본 정보 배치 처리 중 - 배치 {}/{}, 항목 수: {}", 
                batches.indexOf(batch) + 1, batches.size(), batch.size());
            
            try {
                // 배치 단위로 처리
                int batchResult = processBasicInfoBatch(batch);
                processed += batchResult;
                
                // 배치 사이에 잠시 대기 - lock contention 감소
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.error("기본 정보 배치 처리 중 오류 발생 - 배치 {}/{}", 
                    batches.indexOf(batch) + 1, batches.size(), e);
            }
        }
        
        logger.info("기본 정보 페이지 처리 완료 - 처리된 항목: {}", processed);
        return processed;
    }
    
    /**
     * 기본정보 배치 처리 (트랜잭션 분리)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 45) // 타임아웃 45초로 수정
    public int processBasicInfoBatch(List<PrkSttusInfoResponse.PrkSttusInfo> batchItems) {
        int batchProcessed = 0;
        
        for (PrkSttusInfoResponse.PrkSttusInfo item : batchItems) {
            try {
                ParkingInfo parkingInfo = convertToParkingInfo(item);
                
                // ID 정규화
                String normalizedId = normalizeId(parkingInfo.getPrkCenterId());
                logger.debug("주차장 기본 정보 처리 시작 - ID: {}", normalizedId);
                
                // 명시적으로 정규화된 ID 설정
                parkingInfo.setPrkCenterId(normalizedId);
                
                // 기본 정보가 존재하는지 확인
                boolean exists = parkingInfoMapper.existsById(normalizedId);
                
                try {
                    if (exists) {
                        // 기존에 존재하면 업데이트
                        parkingInfoMapper.updateParkingInfo(parkingInfo);
                        logger.debug("주차장 기본 정보 업데이트 완료 (ID: {})", normalizedId);
                    } else {
                        // 존재하지 않으면 새로 삽입
                        try {
                            parkingInfoMapper.insertParkingInfo(parkingInfo);
                            logger.debug("주차장 기본 정보 삽입 완료 (ID: {})", normalizedId);
                        } catch (org.springframework.dao.DuplicateKeyException e) {
                            // 삽입 중 오류 발생 - 동시성 문제로 이미 삽입된 경우 무시
                            logger.warn("동시성으로 인한 중복 삽입 시도 감지 - 업데이트로 전환 (ID: {})", normalizedId);
                            parkingInfoMapper.updateParkingInfo(parkingInfo);
                        }
                    }
                    batchProcessed++;
                } catch (Exception e) {
                    logger.error("주차장 기본 정보 처리 실패 (ID: {}): {}", normalizedId, e.getMessage());
                }
            } catch (Exception e) {
                logger.error("주차장 기본 정보 변환 중 오류: {}", item.getPrkCenterId(), e);
                // 개별 항목 오류는 전체 처리에 영향 없음
            }
        }
        
        return batchProcessed;
    }
    
    // 비-트랜잭션 메서드: 트랜잭션이 분리된 다른 메서드를 호출하기 위한 메서드
    public void fetchAndProcessBasicInfoNonTransactional(String parkingId, PrkRealtimeInfoResponse.PrkRealtimeItem item) {
        // 정규화된 ID 사용
        String normalizedId = normalizeId(parkingId);
        
        try {
            // 단일 주차장 정보 조회 API 호출
            PrkSttusInfoResponse response = parkingApiClient.getPrkSttusInfoById(normalizedId);
            List<PrkSttusInfoResponse.PrkSttusInfo> infoItems = getItemsFromResponse(response);
            
            if (infoItems != null && !infoItems.isEmpty()) {
                // 기본 정보 저장 및 실시간 정보 처리 (이미 트랜잭션 분리되어 있는 메서드 호출)
                saveBasicInfoAndProcessRealtime(infoItems.get(0), item, normalizedId);
                
                // 성공적으로 처리되었는지 확인
                if (!parkingInfoMapper.existsById(normalizedId)) {
                    throw new RuntimeException("기본 정보 저장 후에도 ID가 존재하지 않음: " + normalizedId);
                }
            } else {
                logger.warn("주차장 기본 정보 조회 결과 없음 (ID: {})", normalizedId);
                throw new RuntimeException("API에서 주차장 정보를 찾을 수 없음: " + normalizedId);
            }
        } catch (Exception e) {
            logger.error("주차장 기본 정보 조회 실패 (ID: {}): {}", normalizedId, e.getMessage());
            throw e; // 호출자에게 예외 전파
        }
    }

    // 기본 정보 저장 및 실시간 정보 처리 (별도 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 60)
    public void saveBasicInfoAndProcessRealtime(PrkSttusInfoResponse.PrkSttusInfo infoItem, 
                                             PrkRealtimeInfoResponse.PrkRealtimeItem realtimeItem,
                                             String parkingId) {
        try {
            // ID 확인 및 정규화
            String normalizedId = normalizeId(parkingId);
            
            // API 응답의 ID도 정규화
            String apiParkingId = normalizeId(infoItem.getPrkCenterId());
            
            // ID 불일치 확인 및 로깅
            if (!normalizedId.equals(apiParkingId)) {
                logger.warn("주차장 ID 불일치 감지: 요청 ID={}, API ID={}, 요청 ID를 사용합니다.", normalizedId, apiParkingId);
            }
            
            // 트랜잭션 내에서 바로 처리
            ParkingInfo parkingInfo = convertToParkingInfo(infoItem);
            
            // ID 명시적 설정
            parkingInfo.setPrkCenterId(normalizedId);
            
            // 락 경쟁을 줄이기 위해 먼저 존재하는지 확인 (트랜잭션 내에서)
            boolean exists = parkingInfoMapper.existsById(normalizedId);
            
            // 주차장 기본 정보 처리
            if (exists) {
                // 기존에 존재하면 업데이트
                parkingInfoMapper.updateParkingInfo(parkingInfo);
                logger.debug("주차장 기본 정보 업데이트 완료 (ID: {})", normalizedId);
            } else {
                // 존재하지 않으면 새로 삽입
                try {
                    parkingInfoMapper.insertParkingInfo(parkingInfo);
                    logger.debug("주차장 기본 정보 삽입 완료 (ID: {})", normalizedId);
                } catch (Exception e) {
                    // 삽입 중 오류 발생 - 동시성 문제로 이미 삽입된 경우 무시
                    if (e instanceof org.springframework.dao.DuplicateKeyException) {
                        logger.warn("동시성으로 인한 중복 삽입 시도, 업데이트로 전환 (ID: {})", normalizedId);
                        parkingInfoMapper.updateParkingInfo(parkingInfo);
                    } else {
                        throw e;
                    }
                }
            }
            
            // 기본 정보 삽입/업데이트 후 재확인
            if (parkingInfoMapper.existsById(normalizedId)) {
                // 실시간 정보도 같은 트랜잭션 내에서 바로 처리
                ParkingRealtime realtime = convertToParkingRealtime(realtimeItem);
                realtime.setPrkCenterId(normalizedId); // 정규화된 ID 사용
                parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
                logger.debug("주차장 실시간 정보 처리 완료 (ID: {})", normalizedId);
            } else {
                logger.error("주차장 기본 정보 저장 후에도 조회 불가 (ID: {})", normalizedId);
            }
            
            logger.info("주차장 기본 정보 및 실시간 정보 처리 완료 (ID: {})", normalizedId);
        } catch (Exception e) {
            logger.error("주차장 정보 저장 실패 (ID: {}): {}", parkingId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * ID 정규화 메서드 - ID 형식 통일
     */
    private String normalizeId(String id) {
        if (id == null) {
            return null;
        }
        
        // ID 형식 전처리 (예: 00000-19199-00001-00-1 -> 00000-19199-00001-00-1)
        // 필요에 따라 ID 형식을 조정하는 로직 추가
        return id.trim();
    }
    
    // 기본 정보만 저장 (별도 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 45) // 타임아웃 45초로 수정
    public void saveBasicInfo(PrkSttusInfoResponse.PrkSttusInfo infoItem) {
        try {
            ParkingInfo parkingInfo = convertToParkingInfo(infoItem);
            
            // 가능한 락 경쟁을 줄이기 위해 먼저 존재하는지 확인
            String parkingId = parkingInfo.getPrkCenterId();
            boolean exists = parkingInfoMapper.existsById(parkingId);
            
            if (exists) {
                // 기존에 존재하면 업데이트
                parkingInfoMapper.updateParkingInfo(parkingInfo);
            } else {
                // 존재하지 않으면 새로 삽입
                parkingInfoMapper.insertParkingInfo(parkingInfo);
            }
            
            logger.info("주차장 기본 정보 {} 성공 (ID: {})", (exists ? "업데이트" : "추가"), infoItem.getPrkCenterId());
        } catch (Exception e) {
            logger.error("주차장 기본 정보 저장 실패 (ID: {})", infoItem.getPrkCenterId(), e);
            throw e;
        }
    }
    
    // 실시간 정보만 저장 (별도 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 90) // 1.5분 타임아웃
    public void saveRealtimeInfo(PrkRealtimeInfoResponse.PrkRealtimeItem item, String parkingId) {
        try {
            ParkingRealtime realtime = convertToParkingRealtime(item);
            
            // 실시간 정보의 ID가 기본 정보와 동일한지 다시 확인
            if (!parkingId.equals(realtime.getPrkCenterId())) {
                logger.error("ID 불일치: API({}) vs Entity({})", parkingId, realtime.getPrkCenterId());
                realtime.setPrkCenterId(parkingId); // ID 강제 일치
            }
            
            logger.debug("실시간 정보 삽입/갱신 시도 - ID: {}", realtime.getPrkCenterId());
            parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
            logger.debug("실시간 정보 처리 완료 - ID: {}", parkingId);
        } catch (Exception e) {
            logger.error("주차장 실시간 정보 DB 저장 중 오류: {}", parkingId, e);
            throw e;
        }
    }

    // 실시간 정보 처리를 위한 기본 정보 가져오기 (별도 트랜잭션) - 이전 메서드
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 180) // 3분으로 타임아웃 증가
    public void processBasicInfoForRealtime(String parkingId, PrkRealtimeInfoResponse.PrkRealtimeItem item) {
        try {
            // 새로운 기본 정보 가져오기 및 처리 메서드 호출
            fetchAndProcessBasicInfoNonTransactional(parkingId, item);
        } catch (Exception e) {
            logger.error("주차장 기본 정보 처리 실패 (ID: {})", parkingId, e);
            // 예외를 던지지 않고 로깅만 수행
        }
    }

    // 단일 실시간 정보 처리 (별도 트랜잭션)
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 90) // 1.5분으로 타임아웃 증가
    public void processSingleRealtimeInfo(PrkRealtimeInfoResponse.PrkRealtimeItem item, String parkingId) {
        try {
            // 실시간 정보 저장 메서드 호출
            saveRealtimeInfo(item, parkingId);
        } catch (Exception e) {
            logger.error("주차장 실시간 정보 DB 저장 중 오류: {}", parkingId, e);
            throw e; // 상위로 예외 전파
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
        
        // 주차장 ID 형식 확인 및 보정
        String prkCenterId = item.getPrkCenterId();
        if (prkCenterId != null) {
            // ID 정규화 적용
            prkCenterId = normalizeId(prkCenterId);
            
            // API 응답에서 받은 정규화된 ID를 사용
            parkingInfo.setPrkCenterId(prkCenterId);
            logger.debug("주차장 ID 설정: {}", prkCenterId);
        }
        
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
        String originalId = item.getPrkCenterId();
        String normalizedId = normalizeId(originalId);
        
        logger.info("Converting parking operation info - Original ID: {}, Normalized ID: {}", originalId, normalizedId);
        
        ParkingOperation parkingOperation = new ParkingOperation();
        parkingOperation.setPrkCenterId(normalizedId);
        
        // Set operation times and charges
        String freeTimeStr = item.getOpertnBsFreeTime();
        if (freeTimeStr != null && !freeTimeStr.isEmpty()) {
            try {
                parkingOperation.setOpertnBsFreeTime(Integer.parseInt(freeTimeStr));
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse opertnBsFreeTime: {}", freeTimeStr);
                parkingOperation.setOpertnBsFreeTime(0);
            }
        } else {
            parkingOperation.setOpertnBsFreeTime(0);
        }
        
        parkingOperation.setParkingChrgeBsTime(item.getParkingChrgeBsTime());
        parkingOperation.setParkingChrgeBsChrg(item.getParkingChrgeBsChrg());
        parkingOperation.setOperationDayInfo(item.getOperationDayInfo());
        
        return parkingOperation;
    }

    /**
     * API 응답을 ParkingRealtime 엔티티로 변환
     */
    private ParkingRealtime convertToParkingRealtime(PrkRealtimeInfoResponse.PrkRealtimeItem item) {
        ParkingRealtime realtime = new ParkingRealtime();
        
        // 주차장 ID 형식 확인 및 보정
        String prkCenterId = item.getPrkCenterId();
        if (prkCenterId != null) {
            // ID 정규화 적용
            prkCenterId = normalizeId(prkCenterId);
            
            // 정규화된 ID 사용
            realtime.setPrkCenterId(prkCenterId);
            logger.debug("주차장 실시간 정보 ID 설정: {}", prkCenterId);
        }
        
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

    /**
     * 데이터베이스 초기화 메서드 - 기존 데이터를 모두 삭제합니다.
     * 개발 및 테스트 환경에서만 사용해야 합니다.
     */
    @Transactional
    public void cleanAllData() {
        logger.warn("모든 주차장 데이터 초기화 시작 - 프로덕션 환경에서는 절대 실행하지 마세요!");
        
        try {
            // 실시간 정보 삭제
            int realtimeCount = parkingRealtimeMapper.deleteAllParkingRealtime();
            logger.info("주차장 실시간 정보 {} 건 삭제 완료", realtimeCount);
            
            // 운영 정보 삭제
            int operationCount = parkingOperationMapper.deleteAllParkingOperation();
            logger.info("주차장 운영 정보 {} 건 삭제 완료", operationCount);
            
            // 기본 정보 삭제 (가장 마지막에 삭제, 외래키 제약조건)
            int infoCount = parkingInfoMapper.deleteAllParkingInfo();
            logger.info("주차장 기본 정보 {} 건 삭제 완료", infoCount);
            
            logger.info("모든 주차장 데이터 초기화 완료");
        } catch (Exception e) {
            logger.error("데이터 초기화 중 오류 발생", e);
            throw e;
        }
    }
    
    /**
     * 특정 ID로 시작하는 데이터만 삭제
     * 예: 임시 데이터나 잘못된 형식의 데이터 정리
     */
    @Transactional
    public void cleanDataByIdPattern(String idPattern) {
        logger.warn("ID 패턴 '{}' 으로 시작하는 주차장 데이터 삭제 시작", idPattern);
        
        try {
            // 실시간 정보 삭제
            int realtimeCount = parkingRealtimeMapper.deleteParkingRealtimeByIdPattern(idPattern + "%");
            logger.info("주차장 실시간 정보 {} 건 삭제 완료", realtimeCount);
            
            // 운영 정보 삭제
            int operationCount = parkingOperationMapper.deleteParkingOperationByIdPattern(idPattern + "%");
            logger.info("주차장 운영 정보 {} 건 삭제 완료", operationCount);
            
            // 기본 정보 삭제 (가장 마지막에 삭제, 외래키 제약조건)
            int infoCount = parkingInfoMapper.deleteParkingInfoByIdPattern(idPattern + "%");
            logger.info("주차장 기본 정보 {} 건 삭제 완료", infoCount);
            
            logger.info("ID 패턴 '{}' 으로 시작하는 주차장 데이터 삭제 완료", idPattern);
        } catch (Exception e) {
            logger.error("ID 패턴으로 데이터 삭제 중 오류 발생", e);
            throw e;
        }
    }

    /**
     * 강제로 모든 주차장 데이터를 다시 로드하는 메서드
     * 테스트 및 문제 해결용
     */
    public void forceReloadAllParkingData() {
        logger.info("모든 주차장 데이터 강제 리로드 시작");
        try {
            // 1. 기본정보 조회 및 저장
            logger.info("주차장 기본 정보 강제 리로드 시작");
            syncParkingBasicInfo();
            
            // 2. 운영정보 조회 및 저장
            logger.info("주차장 운영 정보 강제 리로드 시작");
            syncParkingOperationInfo();
            
            // 3. 실시간정보 조회 및 저장
            logger.info("주차장 실시간 정보 강제 리로드 시작");
            syncParkingRealtimeInfo();
            
            logger.info("모든 주차장 데이터 강제 리로드 완료");
        } catch (Exception e) {
            logger.error("주차장 데이터 강제 리로드 중 오류 발생", e);
        }
    }
    
    /**
     * 주차장 운영 정보 정기 동기화 (매일 오전 3시에 실행)
     */
    @Scheduled(cron = "0 30 3 * * *")
    public void scheduledParkingOperationSync() {
        logger.info("주차장 운영 정보 정기 동기화 시작 (스케줄러)");
        try {
            syncParkingOperationInfo();
            logger.info("주차장 운영 정보 정기 동기화 완료 (스케줄러)");
        } catch (Exception e) {
            logger.error("주차장 운영 정보 정기 동기화 중 오류 발생 (스케줄러)", e);
        }
    }

    /**
     * 주차장 운영정보 동기화
     */
    public void syncParkingOperationInfo() {
        logger.info("주차장 운영정보 동기화 시작");
        
        int maxRetries = 3; // 최대 재시도 횟수
        int retryCount = 0;
        int pageNo = 1;
        int numOfRows = 200; // 한 번에 가져올 데이터 수
        int totalProcessed = 0;
        boolean isConnectionError = false;
        
        while (true) {
            try {
                if (isConnectionError) {
                    // 연결 오류 발생 시 잠시 대기 후 재시도
                    retryCount++;
                    if (retryCount > maxRetries) {
                        logger.error("최대 재시도 횟수({})를 초과하여 작업을 중단합니다.", maxRetries);
                        break;
                    }
                    
                    int waitTime = retryCount * 5000; // 점진적으로 대기 시간 증가 (5초, 10초, 15초)
                    logger.info("데이터베이스 연결 오류 후 재시도 중 ({}/{}), {}ms 대기 후 재시도합니다.", 
                        retryCount, maxRetries, waitTime);
                    
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    
                    isConnectionError = false; // 재시도 플래그 초기화
                }
                
                logger.info("주차장 운영정보 페이지 {} 요청 시작, 건수: {}", pageNo, numOfRows);
                PrkOprInfoResponse response = parkingApiClient.getPrkOprInfo(pageNo, numOfRows);
                
                // API 응답이 NULL인지 확인
                if (response == null) {
                    logger.error("주차장 운영정보 API 응답이 NULL입니다. 네트워크 또는 API 서버 오류일 수 있습니다.");
                    break;
                }
                
                // 응답 코드 확인
                String resultCode = getResultCodeFromResponse(response);
                String resultMsg = getResultMsgFromResponse(response);
                logger.info("주차장 운영정보 API 응답 - 코드: {}, 메시지: {}", resultCode, resultMsg);
                
                if (!"00".equals(resultCode) && !"SUCCESS".equals(resultMsg)) {
                    logger.error("주차장 운영정보 API 오류: {}", resultMsg);
                    break;
                }
                
                List<PrkOprInfoResponse.PrkOprInfo> items = getItemsFromResponse(response);
                
                if (items == null || items.isEmpty()) {
                    logger.info("주차장 운영정보에 데이터가 없습니다.");
                    break;
                }

                logger.info("주차장 운영정보 페이지 {}: {} 건 조회됨", pageNo, items.size());

                // 운영 정보 배치 처리
                int[] processed = processOperationInfoBatch(items);
                totalProcessed += processed[0];
                
                logger.info("주차장 운영정보 페이지 {} 처리 완료: {} 건 처리됨(성공: {}, 실패: {}), 총 처리: {}", 
                    pageNo, items.size(), processed[0], processed[1], totalProcessed);

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    logger.info("요청한 건수({})보다 적은 응답({})이 왔으므로 마지막 페이지로 간주합니다.", numOfRows, items.size());
                    break;
                }

                pageNo++;
                retryCount = 0; // 성공 시 재시도 카운트 초기화
                
                // 다음 페이지 요청 전 잠시 대기 (API 부하 감소)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                if (isConnectionError(e)) {
                    isConnectionError = true;
                    logger.error("데이터베이스 연결 오류 발생: {}", e.getMessage());
                    continue; // 재시도 로직으로 이동
                } else {
                    logger.error("주차장 운영정보 동기화 중 오류 발생", e);
                    break;
                }
            }
        }
        
        logger.info("주차장 운영정보 동기화 완료: 총 {} 건 처리", totalProcessed);
    }
    
    /**
     * 연결 관련 오류인지 확인
     */
    private boolean isConnectionError(Exception e) {
        String message = e.getMessage();
        Throwable cause = e.getCause();
        
        // 직접적인 연결 오류 메시지 확인
        if (message != null && (
                message.contains("HikariDataSource has been closed") ||
                message.contains("Failed to obtain JDBC Connection") ||
                message.contains("Connection refused") ||
                message.contains("Connection reset") ||
                message.contains("Connection timed out"))) {
            return true;
        }
        
        // 연결 관련 예외 클래스 확인
        while (cause != null) {
            if (cause instanceof java.sql.SQLException || 
                cause instanceof org.springframework.jdbc.CannotGetJdbcConnectionException ||
                cause instanceof java.net.ConnectException) {
                return true;
            }
            cause = cause.getCause();
        }
        
        return false;
    }

    /**
     * 주차장 운영정보 배치 처리
     */
    @Transactional(timeout = 45)
    public int[] processOperationInfoBatch(List<PrkOprInfoResponse.PrkOprInfo> batch) {
        int successCount = 0;
        int failCount = 0;
        
        for (PrkOprInfoResponse.PrkOprInfo item : batch) {
            try {
                // ID가 null이거나 빈 문자열인 경우 건너뜀
                if (item.getPrkCenterId() == null || item.getPrkCenterId().trim().isEmpty()) {
                    logger.warn("운영 정보 ID가 null 또는 빈값이므로 처리 중단: {}", item);
                    failCount++;
                    continue;
                }
                
                // ID 정규화 - 기본 정보와 일치시킴
                String normalizedId = normalizeId(item.getPrkCenterId());
                logger.debug("운영 정보 ID 정규화: {} -> {}", item.getPrkCenterId(), normalizedId);
                
                // 기본 정보 존재 여부 확인
                boolean exists = parkingInfoMapper.existsById(normalizedId);
                
                if (!exists) {
                    logger.warn("ID에 해당하는 기본 정보가 없어 운영 정보를 저장할 수 없습니다: {}", normalizedId);
                    
                    // API에서 해당 ID의 기본 정보 조회 시도 (트랜잭션 타임아웃 위험 감소를 위해 API 호출 시작/완료 로깅 추가)
                    try {
                        logger.info("API에서 기본 정보 조회 시작 (ID: {})", normalizedId);
                        PrkSttusInfoResponse basicResponse = parkingApiClient.getPrkSttusInfoById(normalizedId);
                        logger.info("API에서 기본 정보 조회 완료 (ID: {})", normalizedId);
                        
                        if (basicResponse == null) {
                            logger.warn("API 응답이 null입니다 (ID: {})", normalizedId);
                            failCount++;
                            continue;
                        }
                        
                        List<PrkSttusInfoResponse.PrkSttusInfo> basicItems = getItemsFromResponse(basicResponse);
                        
                        if (basicItems == null || basicItems.isEmpty()) {
                            logger.warn("API에서 해당 ID의 기본 정보를 찾을 수 없습니다: {}", normalizedId);
                            failCount++;
                            continue;
                        }
                        
                        ParkingInfo parkingInfo = convertToParkingInfo(basicItems.get(0));
                        
                        // API 응답 ID 확인 및 로깅
                        String apiResponseId = parkingInfo.getPrkCenterId();
                        if (!normalizedId.equals(apiResponseId)) {
                            logger.warn("API 응답 ID({})가 요청 ID({})와 다릅니다. 요청한 ID를 사용합니다.", apiResponseId, normalizedId);
                        }
                        
                        // 명시적으로 ID 설정 (API 응답 ID가 아닌 요청한 ID 사용)
                        parkingInfo.setPrkCenterId(normalizedId);
                        
                        // 다른 스레드에서 이미 삽입했을 수 있으므로 존재 여부 다시 확인
                        exists = parkingInfoMapper.existsById(normalizedId);
                        
                        if (exists) {
                            // 이미 존재하면 업데이트만 시도
                            logger.info("이미 존재하는 ID: {}, 업데이트만 수행", normalizedId);
                            parkingInfoMapper.updateParkingInfo(parkingInfo);
                        } else {
                            try {
                                // 존재하지 않으면 삽입 시도
                                parkingInfoMapper.insertParkingInfo(parkingInfo);
                                logger.info("새 주차장 기본 정보 삽입 성공: {}", normalizedId);
                                // 다시 존재 확인
                                exists = parkingInfoMapper.existsById(normalizedId);
                            } catch (org.springframework.dao.DuplicateKeyException e) {
                                // 동시성 문제로 다른 스레드가 이미 삽입했을 경우 업데이트로 전환
                                logger.warn("동시성 문제로 인한 중복 삽입 시도, 업데이트로 전환: {} ({})", normalizedId, e.getMessage());
                                parkingInfoMapper.updateParkingInfo(parkingInfo);
                                exists = true; // 업데이트 후 존재한다고 가정
                            }
                        }
                        
                        logger.info("누락된 기본 정보를 API에서 조회하여 저장했습니다: {}", normalizedId);
                    } catch (Exception e) {
                        logger.error("누락된 기본 정보 조회 중 오류 발생: {} ({})", normalizedId, e.getMessage());
                        failCount++;
                        continue;
                    }
                }
                
                // 기본 정보가 여전히 존재하지 않으면 저장하지 않음
                if (!exists) {
                    logger.warn("모든 시도 후에도 해당 ID의 기본 정보가 존재하지 않습니다. 운영 정보를 저장하지 않습니다: {}", normalizedId);
                    failCount++;
                    continue;
                }
                
                // 운영 정보 변환 및 저장
                ParkingOperation parkingOperation = convertToParkingOperation(item);
                
                // ID 명시적 설정 재확인
                parkingOperation.setPrkCenterId(normalizedId);
                
                try {
                    // 운영 정보 저장 (insertOrUpdate 메서드 사용)
                    parkingOperationMapper.insertOrUpdateParkingOperation(parkingOperation);
                    logger.debug("주차장 운영 정보 저장 성공: {}", normalizedId);
                    successCount++;
                } catch (Exception e) {
                    logger.error("주차장 운영 정보 저장 실패: {} ({})", normalizedId, e.getMessage());
                    failCount++;
                }
            } catch (Exception e) {
                logger.error("주차장 운영 정보 처리 중 오류 발생: {} ({})", 
                    item.getPrkCenterId() != null ? item.getPrkCenterId() : "null ID", e.getMessage());
                failCount++;
            }
        }
        
        logger.info("운영 정보 배치 처리 결과 - 성공: {}, 실패: {}", successCount, failCount);
        return new int[]{successCount, failCount};
    }

    /**
     * 주차장 실시간 정보 배치 처리
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 45)
    public int processRealtimeInfoBatch(List<PrkRealtimeInfoResponse.PrkRealtimeItem> batchItems) {
        int successCount = 0;
        int failCount = 0;
        
        for (PrkRealtimeInfoResponse.PrkRealtimeItem item : batchItems) {
            try {
                // ID가 null이거나 비어있는지 확인
                if (item.getPrkCenterId() == null || item.getPrkCenterId().trim().isEmpty()) {
                    logger.warn("실시간 정보의 주차장 ID가 null 또는 빈값이므로 처리 중단");
                    failCount++;
                    continue;
                }
                
                // ID 정규화
                String normalizedId = normalizeId(item.getPrkCenterId());
                logger.debug("실시간 정보 처리 시작 - ID: {}", normalizedId);
                
                // 기본 정보가 존재하는지 확인
                boolean exists = parkingInfoMapper.existsById(normalizedId);
                
                // 기본 정보가 없는 경우 - API에서 조회하여 저장 시도
                if (!exists) {
                    logger.warn("실시간 정보의 주차장 기본 정보가 없음 (ID: {}), API에서 기본 정보 조회 시도", normalizedId);
                    
                    try {
                        logger.info("API에서 기본 정보 조회 시작 (ID: {})", normalizedId);
                        PrkSttusInfoResponse basicResponse = parkingApiClient.getPrkSttusInfoById(normalizedId);
                        logger.info("API에서 기본 정보 조회 완료 (ID: {})", normalizedId);
                        
                        if (basicResponse == null) {
                            logger.warn("API 응답이 null입니다 (ID: {})", normalizedId);
                            failCount++;
                            continue;
                        }
                        
                        List<PrkSttusInfoResponse.PrkSttusInfo> basicItems = getItemsFromResponse(basicResponse);
                        
                        if (basicItems == null || basicItems.isEmpty()) {
                            logger.warn("API에서 해당 ID의 기본 정보를 찾을 수 없습니다: {}", normalizedId);
                            failCount++;
                            continue;
                        }
                        
                        ParkingInfo parkingInfo = convertToParkingInfo(basicItems.get(0));
                        
                        // API 응답 ID 확인 및 로깅
                        String apiResponseId = parkingInfo.getPrkCenterId();
                        if (!normalizedId.equals(apiResponseId)) {
                            logger.warn("API 응답 ID({})가 요청 ID({})와 다릅니다. 요청한 ID를 사용합니다.", apiResponseId, normalizedId);
                        }
                        
                        // 명시적으로 ID 설정 (API 응답 ID가 아닌 요청한 ID 사용)
                        parkingInfo.setPrkCenterId(normalizedId);
                        
                        // 다른 스레드에서 이미 삽입했을 수 있으므로 존재 여부 다시 확인
                        exists = parkingInfoMapper.existsById(normalizedId);
                        
                        if (exists) {
                            // 이미 존재하면 업데이트만 시도
                            logger.info("이미 존재하는 ID: {}, 업데이트만 수행", normalizedId);
                            parkingInfoMapper.updateParkingInfo(parkingInfo);
                        } else {
                            try {
                                // 존재하지 않으면 삽입 시도
                                parkingInfoMapper.insertParkingInfo(parkingInfo);
                                logger.info("새 주차장 기본 정보 삽입 성공: {}", normalizedId);
                                // 다시 존재 확인
                                exists = parkingInfoMapper.existsById(normalizedId);
                            } catch (org.springframework.dao.DuplicateKeyException e) {
                                // 동시성 문제로 다른 스레드가 이미 삽입했을 경우 업데이트로 전환
                                logger.warn("동시성 문제로 인한 중복 삽입 시도, 업데이트로 전환: {} ({})", normalizedId, e.getMessage());
                                parkingInfoMapper.updateParkingInfo(parkingInfo);
                                exists = true; // 업데이트 후 존재한다고 가정
                            }
                        }
                        
                        logger.info("누락된 기본 정보를 API에서 조회하여 저장했습니다: {}", normalizedId);
                    } catch (Exception e) {
                        logger.error("누락된 기본 정보 조회 중 오류 발생: {} ({})", normalizedId, e.getMessage());
                        failCount++;
                        continue;
                    }
                }
                
                // 기본 정보가 여전히 존재하지 않으면 저장하지 않음
                if (!exists) {
                    logger.warn("모든 시도 후에도 해당 ID의 기본 정보가 존재하지 않습니다. 실시간 정보를 저장하지 않습니다: {}", normalizedId);
                    failCount++;
                    continue;
                }
                
                try {
                    // 실시간 정보 저장
                    ParkingRealtime realtime = convertToParkingRealtime(item);
                    realtime.setPrkCenterId(normalizedId);
                    
                    // 실시간 정보 삽입 또는 업데이트
                    parkingRealtimeMapper.insertOrUpdateParkingRealtime(realtime);
                    logger.debug("실시간 정보 처리 완료 (ID: {})", normalizedId);
                    
                    successCount++;
                } catch (Exception e) {
                    logger.error("실시간 정보 저장 중 오류 발생 (ID: {}): {}", normalizedId, e.getMessage());
                    failCount++;
                }
            } catch (Exception e) {
                String itemId = (item != null && item.getPrkCenterId() != null) ? item.getPrkCenterId() : "null ID";
                logger.error("실시간 정보 처리 중 예상치 못한 오류 발생 (ID: {}): {}", itemId, e.getMessage());
                failCount++;
            }
        }
        
        logger.info("실시간 정보 배치 처리 결과 - 성공: {}, 실패: {}", successCount, failCount);
        return successCount;
    }
    
    /**
     * 주차장 실시간 정보 페이지 처리
     */
    public int processRealtimeInfoPage(List<PrkRealtimeInfoResponse.PrkRealtimeItem> items) {
        int processed = 0;
        int batchSize = 10; // 배치 크기 10으로 설정
        
        List<List<PrkRealtimeInfoResponse.PrkRealtimeItem>> batches = new ArrayList<>();
        for (int i = 0; i < items.size(); i += batchSize) {
            batches.add(items.subList(i, Math.min(i + batchSize, items.size())));
        }
        
        logger.info("실시간 정보 배치 처리 시작 - 총 {}개 배치", batches.size());
        
        for (List<PrkRealtimeInfoResponse.PrkRealtimeItem> batch : batches) {
            logger.info("실시간 정보 배치 처리 중 - 배치 {}/{}, 항목 수: {}", 
                batches.indexOf(batch) + 1, batches.size(), batch.size());
            
            try {
                // 배치 단위로 처리
                int batchResult = processRealtimeInfoBatch(batch);
                processed += batchResult;
                
                // 배치 사이에 잠시 대기 - lock contention 감소
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                logger.error("실시간 정보 배치 처리 중 오류 발생 - 배치 {}/{}", 
                    batches.indexOf(batch) + 1, batches.size(), e);
            }
        }
        
        logger.info("실시간 정보 페이지 처리 완료 - 처리된 항목: {}", processed);
        return processed;
    }
    
    /**
     * 주차장 실시간 정보 동기화
     */
    @Scheduled(cron = "0 */30 * * * *") // 30분마다 실행
    public void syncParkingRealtimeInfo() {
        logger.info("주차장 실시간 정보 동기화 시작");
        
        int maxRetries = 3; // 최대 재시도 횟수
        int retryCount = 0;
        int pageNo = 1;
        int numOfRows = 200; // 한 번에 가져올 데이터 수
        int totalProcessed = 0;
        boolean isConnectionError = false;
        
        while (true) {
            try {
                if (isConnectionError) {
                    // 연결 오류 발생 시 잠시 대기 후 재시도
                    retryCount++;
                    if (retryCount > maxRetries) {
                        logger.error("최대 재시도 횟수({})를 초과하여 작업을 중단합니다.", maxRetries);
                        break;
                    }
                    
                    int waitTime = retryCount * 5000; // 점진적으로 대기 시간 증가 (5초, 10초, 15초)
                    logger.info("데이터베이스 연결 오류 후 재시도 중 ({}/{}), {}ms 대기 후 재시도합니다.", 
                        retryCount, maxRetries, waitTime);
                    
                    try {
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    
                    isConnectionError = false; // 재시도 플래그 초기화
                }
                
                logger.info("주차장 실시간 정보 페이지 {} 요청 시작, 건수: {}", pageNo, numOfRows);
                PrkRealtimeInfoResponse response = parkingApiClient.getPrkRealtimeInfo(pageNo, numOfRows);
                
                // API 응답이 NULL인지 확인
                if (response == null) {
                    logger.error("주차장 실시간 정보 API 응답이 NULL입니다. 네트워크 또는 API 서버 오류일 수 있습니다.");
                    break;
                }
                
                // 응답 코드 확인
                String resultCode = getResultCodeFromResponse(response);
                String resultMsg = getResultMsgFromResponse(response);
                logger.info("주차장 실시간 정보 API 응답 - 코드: {}, 메시지: {}", resultCode, resultMsg);
                
                if (!"00".equals(resultCode) && !"SUCCESS".equals(resultMsg)) {
                    logger.error("주차장 실시간 정보 API 오류: {}", resultMsg);
                    break;
                }
                
                List<PrkRealtimeInfoResponse.PrkRealtimeItem> items = getItemsFromResponse(response);
                
                if (items == null || items.isEmpty()) {
                    logger.info("주차장 실시간 정보에 데이터가 없습니다.");
                    break;
                }

                logger.info("주차장 실시간 정보 페이지 {}: {} 건 조회됨", pageNo, items.size());

                // 실시간 정보 페이지 처리
                int processed = processRealtimeInfoPage(items);
                totalProcessed += processed;
                
                logger.info("주차장 실시간 정보 페이지 {} 처리 완료: {} 건 처리됨, 총 처리: {}", 
                    pageNo, processed, totalProcessed);

                // 다음 페이지가 없으면 종료
                if (items.size() < numOfRows) {
                    logger.info("요청한 건수({})보다 적은 응답({})이 왔으므로 마지막 페이지로 간주합니다.", numOfRows, items.size());
                    break;
                }

                pageNo++;
                retryCount = 0; // 성공 시 재시도 카운트 초기화
                
                // 다음 페이지 요청 전 잠시 대기 (API 부하 감소)
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            } catch (Exception e) {
                if (isConnectionError(e)) {
                    isConnectionError = true;
                    logger.error("데이터베이스 연결 오류 발생: {}", e.getMessage());
                    continue; // 재시도 로직으로 이동
                } else {
                    logger.error("주차장 실시간 정보 동기화 중 오류 발생: {}", e.getMessage());
                    break;
                }
            }
        }
        
        logger.info("주차장 실시간 정보 동기화 완료: 총 {} 건 처리", totalProcessed);
    }
} 