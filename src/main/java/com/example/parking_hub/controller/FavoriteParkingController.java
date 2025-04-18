package com.example.parking_hub.controller;

import com.example.parking_hub.dto.FavoriteParkingRequest;
import com.example.parking_hub.dto.FavoriteParkingResponse;
import com.example.parking_hub.model.FavoriteParking;
import com.example.parking_hub.security.UserDetailsImpl;
import com.example.parking_hub.service.FavoriteParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 즐겨찾기 주차장 관련 REST API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteParkingController {

    private final FavoriteParkingService favoriteParkingService;

    @Autowired
    public FavoriteParkingController(FavoriteParkingService favoriteParkingService) {
        this.favoriteParkingService = favoriteParkingService;
    }

    /**
     * 사용자의 즐겨찾기 주차장 목록을 조회합니다.
     *
     * @param userDetails 인증된 사용자 정보
     * @return 즐겨찾기 주차장 목록
     */
    @GetMapping
    public ResponseEntity<List<FavoriteParkingResponse>> getUserFavorites(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<FavoriteParking> favorites = favoriteParkingService.getUserFavoritesWithParkingInfo(userDetails.getId());
        
        List<FavoriteParkingResponse> response = favorites.stream()
                .map(FavoriteParkingResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 추가
     *
     * @param request 즐겨찾기 정보
     * @param userDetails 인증된 사용자 정보
     * @return 추가된 즐겨찾기 정보
     */
    @PostMapping
    public ResponseEntity<FavoriteParkingResponse> addFavorite(
            @RequestBody FavoriteParkingRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        FavoriteParking favoriteParking = new FavoriteParking();
        favoriteParking.setUserId(userDetails.getId());
        favoriteParking.setPrkCenterId(request.getPrkCenterId());
        favoriteParking.setMemo(request.getMemo());
        
        FavoriteParking saved = favoriteParkingService.addFavorite(favoriteParking);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(FavoriteParkingResponse.fromEntity(saved));
    }

    /**
     * 즐겨찾기 상태 토글 (추가/제거)
     *
     * @param prkCenterId 주차장 ID
     * @param request 메모 정보를 포함한 요청 (선택적)
     * @param userDetails 인증된 사용자 정보
     * @return 토글 결과 (추가 여부)
     */
    @PostMapping("/toggle/{prkCenterId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable String prkCenterId,
            @RequestBody(required = false) Map<String, String> request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        String memo = request != null ? request.get("memo") : null;
        boolean isAdded = favoriteParkingService.toggleFavorite(userDetails.getId(), prkCenterId, memo);
        
        Map<String, Object> response = new HashMap<>();
        response.put("prkCenterId", prkCenterId);
        response.put("favorited", isAdded);
        response.put("message", isAdded ? "즐겨찾기에 추가되었습니다." : "즐겨찾기에서 제거되었습니다.");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 상태 확인
     *
     * @param prkCenterId 주차장 ID
     * @param userDetails 인증된 사용자 정보
     * @return 즐겨찾기 상태
     */
    @GetMapping("/check/{prkCenterId}")
    public ResponseEntity<Map<String, Object>> checkFavoriteStatus(
            @PathVariable String prkCenterId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        boolean isFavorited = favoriteParkingService
                .getFavoriteByUserAndParkingId(userDetails.getId(), prkCenterId)
                .isPresent();
        
        Map<String, Object> response = new HashMap<>();
        response.put("prkCenterId", prkCenterId);
        response.put("favorited", isFavorited);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 즐겨찾기 삭제
     *
     * @param id 즐겨찾기 ID
     * @param userDetails 인증된 사용자 정보
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // 해당 즐겨찾기가 현재 사용자의 것인지 확인
        return favoriteParkingService.getFavoriteById(id)
                .filter(favorite -> favorite.getUserId().equals(userDetails.getId()))
                .map(favorite -> {
                    favoriteParkingService.deleteFavorite(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 즐겨찾기 메모 업데이트
     *
     * @param id 즐겨찾기 ID
     * @param request 메모 정보를 포함한 요청
     * @param userDetails 인증된 사용자 정보
     * @return 업데이트된 즐겨찾기 정보
     */
    @PatchMapping("/{id}")
    public ResponseEntity<FavoriteParkingResponse> updateFavorite(
            @PathVariable Long id,
            @RequestBody FavoriteParkingRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // 해당 즐겨찾기가 현재 사용자의 것인지 확인
        return favoriteParkingService.getFavoriteById(id)
                .filter(favorite -> favorite.getUserId().equals(userDetails.getId()))
                .map(favorite -> {
                    favorite.setMemo(request.getMemo());
                    FavoriteParking updated = favoriteParkingService.updateFavorite(favorite);
                    return ResponseEntity.ok(FavoriteParkingResponse.fromEntity(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 