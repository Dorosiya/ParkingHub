package com.example.parking_hub.controller;

import com.example.parking_hub.model.ParkingInfo;
import com.example.parking_hub.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
    private final SearchService searchService;
    
    @Autowired
    public HomeController(SearchService searchService) {
        this.searchService = searchService;
    }
    
    /**
     * 인증 정보를 모델에 추가합니다.
     */
    private void addAuthToModel(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            model.addAttribute("isAuthenticated", true);
            model.addAttribute("username", auth.getName());
        } else {
            model.addAttribute("isAuthenticated", false);
        }
    }
    
    @GetMapping("/")
    public String home(Model model) {
        addAuthToModel(model);
        return "home/home";
    }
    
    @GetMapping("/map")
    public String mapSearch(Model model) {
        addAuthToModel(model);
        return "map/mapSearch";
    }
    
    @GetMapping("/mapSearch")
    public String oldMapSearch() {
        return "redirect:/map";
    }
    
    @GetMapping("/search")
    public String search(Model model, 
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String region) {
        
        addAuthToModel(model);
        
        try {
            logger.info("주차장 검색 요청 - 키워드: {}, 지역: {}", keyword, region);
            
            // 전처리: null 값 처리 및 trim
            keyword = (keyword != null) ? keyword.trim() : "";
            region = (region != null) ? region.trim() : "";
            
            // 빈 검색어 처리 (선택적)
            if (keyword.isEmpty() && region.isEmpty()) {
                logger.info("검색어와 지역이 모두 비어있어 빈 결과 반환");
                model.addAttribute("parkingList", Collections.emptyList());
                model.addAttribute("keyword", keyword);
                model.addAttribute("region", region);
                return "search/search";
            }
            
            // 서비스를 통해 주차장 검색
            List<ParkingInfo> parkingList = searchService.searchParking(keyword, region);
            
            if (parkingList == null) {
                logger.warn("검색 결과가 null 반환됨");
                parkingList = Collections.emptyList();
            }
            
            logger.info("검색 결과: {} 건의 주차장 정보", parkingList.size());
            
            // 요금 정보 임의 설정 (실제로는 DB에서 가져와야 함)
            for (ParkingInfo parking : parkingList) {
                try {
                    // ID 기반으로 임의 요금 정보 설정 (실제 구현에서는 DB 데이터 사용)
                    if (parking.getPrkCenterId() != null) {
                        // 주차장 ID의 마지막 글자가 짝수이면 유료, 홀수이면 무료로 가정
                        String id = parking.getPrkCenterId();
                        char lastChar = id.charAt(id.length() - 1);
                        boolean isFree = Character.isDigit(lastChar) && 
                                       (Character.getNumericValue(lastChar) % 2 != 0);
                        
                        parking.setFree(isFree);
                        if (isFree) {
                            parking.setParkingFeeInfo("무료");
                        } else {
                            parking.setParkingFeeInfo("시간당 1,000원");
                        }
                    } else {
                        parking.setFree(false);
                        parking.setParkingFeeInfo("정보 없음");
                    }
                } catch (Exception e) {
                    logger.error("주차장 요금 정보 설정 중 오류: ID={}, 오류={}", 
                               parking.getPrkCenterId(), e.getMessage());
                    parking.setFree(false);
                    parking.setParkingFeeInfo("정보 없음");
                }
            }
            
            model.addAttribute("parkingList", parkingList);
            model.addAttribute("keyword", keyword);
            model.addAttribute("region", region);
            
            return "search/search";
        } catch (Exception e) {
            logger.error("주차장 검색 처리 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "주차장 정보를 조회하는 중 오류가 발생했습니다.");
            model.addAttribute("parkingList", Collections.emptyList());
            model.addAttribute("keyword", keyword != null ? keyword : "");
            model.addAttribute("region", region != null ? region : "");
            return "search/search";
        }
    }
    
    @GetMapping("/parking/{id}")
    public String parkingDetail(@PathVariable String id, Model model) {
        addAuthToModel(model);
        
        try {
            logger.info("주차장 상세 정보 조회 요청 - ID: {}", id);
            
            // ID 유효성 검사
            if (id == null || id.trim().isEmpty()) {
                logger.warn("유효하지 않은 주차장 ID: {}", id);
                return "redirect:/search?error=invalid_id";
            }
            
            // 서비스를 통해 주차장 상세 정보 조회
            Map<String, Object> parkingDetails = searchService.getParkingDetail(id);
            
            if (parkingDetails == null || parkingDetails.isEmpty()) {
                logger.warn("주차장 상세 정보를 찾을 수 없음 - ID: {}", id);
                return "redirect:/search?error=notfound";
            }
            
            model.addAttribute("parking", parkingDetails);
            
            // JSP에서 사용할 값 미리 문자열로 변환하여 추가
            try {
                if (parkingDetails.containsKey("info") && 
                    parkingDetails.get("info") != null && 
                    ((Map)parkingDetails.get("info")).containsKey("latitude") && 
                    ((Map)parkingDetails.get("info")).containsKey("longitude")) {
                    
                    Object latObj = ((Map)parkingDetails.get("info")).get("latitude");
                    Object lngObj = ((Map)parkingDetails.get("info")).get("longitude");
                    
                    String latitude = (latObj != null) ? String.valueOf(latObj) : "37.5665";
                    String longitude = (lngObj != null) ? String.valueOf(lngObj) : "126.9780";
                    
                    model.addAttribute("latitude", latitude);
                    model.addAttribute("longitude", longitude);
                } else {
                    // 기본값 설정
                    logger.warn("주차장의 위치 정보가 없음 - ID: {}", id);
                    model.addAttribute("latitude", "37.5665");
                    model.addAttribute("longitude", "126.9780");
                }
            } catch (Exception e) {
                logger.error("주차장 위치 정보 처리 중 오류: {}", e.getMessage());
                model.addAttribute("latitude", "37.5665");
                model.addAttribute("longitude", "126.9780");
            }
            
            logger.info("주차장 상세 정보 조회 성공 - ID: {}", id);
            return "parking/parkingDetail";
        } catch (Exception e) {
            logger.error("주차장 상세 정보 처리 중 오류 발생: {}", e.getMessage(), e);
            return "redirect:/search?error=system";
        }
    }
} 