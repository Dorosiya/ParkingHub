package com.example.parking_hub.service;

import com.example.parking_hub.mapper.FavoriteParkingMapper;
import com.example.parking_hub.model.FavoriteParking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 즐겨찾기 주차장 관련 비즈니스 로직을 처리하는 서비스
 */
@Service
public class FavoriteParkingService {

    private final FavoriteParkingMapper favoriteParkingMapper;

    @Autowired
    public FavoriteParkingService(FavoriteParkingMapper favoriteParkingMapper) {
        this.favoriteParkingMapper = favoriteParkingMapper;
    }

    /**
     * 사용자의 모든 즐겨찾기 주차장 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 즐겨찾기 주차장 목록
     */
    public List<FavoriteParking> getUserFavorites(Long userId) {
        return favoriteParkingMapper.findAllByUserId(userId);
    }

    /**
     * 주차장 정보가 포함된 사용자의 즐겨찾기 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 주차장 정보가 포함된 즐겨찾기 목록
     */
    public List<FavoriteParking> getUserFavoritesWithParkingInfo(Long userId) {
        return favoriteParkingMapper.findAllWithParkingInfoByUserId(userId);
    }

    /**
     * ID로 특정 즐겨찾기를 조회합니다.
     *
     * @param id 즐겨찾기 ID
     * @return 즐겨찾기 정보
     */
    public Optional<FavoriteParking> getFavoriteById(Long id) {
        return Optional.ofNullable(favoriteParkingMapper.findById(id));
    }

    /**
     * 사용자가 특정 주차장을 즐겨찾기했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @return 즐겨찾기 정보 (없으면 빈 Optional)
     */
    public Optional<FavoriteParking> getFavoriteByUserAndParkingId(Long userId, String prkCenterId) {
        return Optional.ofNullable(favoriteParkingMapper.findByUserIdAndPrkCenterId(userId, prkCenterId));
    }

    /**
     * 새로운 즐겨찾기를 추가합니다.
     *
     * @param favoriteParking 추가할 즐겨찾기 정보
     * @return 추가된 즐겨찾기 정보
     */
    @Transactional
    public FavoriteParking addFavorite(FavoriteParking favoriteParking) {
        // 생성일과 수정일 설정
        LocalDateTime now = LocalDateTime.now();
        favoriteParking.setCreatedAt(now);
        favoriteParking.setUpdatedAt(now);
        
        favoriteParkingMapper.insert(favoriteParking);
        return favoriteParking;
    }

    /**
     * 즐겨찾기 정보를 업데이트합니다.
     *
     * @param favoriteParking 업데이트할 즐겨찾기 정보
     * @return 업데이트된 즐겨찾기 정보
     */
    @Transactional
    public FavoriteParking updateFavorite(FavoriteParking favoriteParking) {
        // 수정일 설정
        favoriteParking.setUpdatedAt(LocalDateTime.now());
        
        favoriteParkingMapper.update(favoriteParking);
        return favoriteParking;
    }

    /**
     * ID로 즐겨찾기를 삭제합니다.
     *
     * @param id 삭제할 즐겨찾기 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteFavorite(Long id) {
        return favoriteParkingMapper.deleteById(id) > 0;
    }

    /**
     * 사용자와 주차장 ID로 즐겨찾기를 삭제합니다.
     *
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @return 삭제 성공 여부
     */
    @Transactional
    public boolean deleteFavoriteByUserAndParkingId(Long userId, String prkCenterId) {
        return favoriteParkingMapper.deleteByUserIdAndPrkCenterId(userId, prkCenterId) > 0;
    }
    
    /**
     * 주차장을 즐겨찾기에 추가하거나 제거합니다. (토글 기능)
     * 
     * @param userId 사용자 ID
     * @param prkCenterId 주차장 ID
     * @param memo 메모 (추가할 경우에만 사용)
     * @return true: 추가됨, false: 제거됨
     */
    @Transactional
    public boolean toggleFavorite(Long userId, String prkCenterId, String memo) {
        FavoriteParking existing = favoriteParkingMapper.findByUserIdAndPrkCenterId(userId, prkCenterId);
        
        if (existing != null) {
            // 이미 즐겨찾기에 있으면 제거
            favoriteParkingMapper.deleteByUserIdAndPrkCenterId(userId, prkCenterId);
            return false;
        } else {
            // 없으면 추가
            FavoriteParking newFavorite = new FavoriteParking();
            newFavorite.setUserId(userId);
            newFavorite.setPrkCenterId(prkCenterId);
            newFavorite.setMemo(memo);
            
            // 생성일과 수정일 설정
            LocalDateTime now = LocalDateTime.now();
            newFavorite.setCreatedAt(now);
            newFavorite.setUpdatedAt(now);
            
            favoriteParkingMapper.insert(newFavorite);
            return true;
        }
    }
} 