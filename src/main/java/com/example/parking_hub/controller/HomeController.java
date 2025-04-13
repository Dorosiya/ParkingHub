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
        return "home";
    }
    
    @GetMapping("/mapSearch")
    public String mapSearch(Model model) {
        addAuthToModel(model);
        return "mapSearch";
    }
    
    @GetMapping("/search")
    public String search(Model model, 
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String region) {
        
        addAuthToModel(model);
        
        // 서비스를 통해 주차장 검색
        List<ParkingInfo> parkingList = searchService.searchParking(keyword, region);
        model.addAttribute("parkingList", parkingList);
        
        return "search";
    }
    
    @GetMapping("/parking/{id}")
    public String parkingDetail(@PathVariable String id, Model model) {
        addAuthToModel(model);
        
        // 서비스를 통해 주차장 상세 정보 조회
        Map<String, Object> parkingDetails = searchService.getParkingDetail(id);
        
        if (parkingDetails == null || parkingDetails.isEmpty()) {
            return "redirect:/search?error=notfound";
        }
        
        model.addAttribute("parking", parkingDetails);
        
        // JSP에서 사용할 값 미리 문자열로 변환하여 추가
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
            model.addAttribute("latitude", "37.5665");
            model.addAttribute("longitude", "126.9780");
        }
        
        return "parkingDetail";
    }
} 