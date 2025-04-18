<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - 즐겨찾기 | ParkingHub</title>
    
    <!-- 부트스트랩 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/mypage.css">
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="/">ParkingHub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/parking/search">주차장 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/map">지도 보기</a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" href="/mypage">마이페이지</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="javascript:void(0);" onclick="logout()">로그아웃</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- 메인 컨텐츠 -->
    <div class="mypage-container">
        <div class="mypage-header">
            <h1 class="mypage-title">마이페이지</h1>
        </div>
        
        <div class="mypage-tabs">
            <div class="mypage-tab" onclick="location.href='/mypage'">내 정보</div>
            <div class="mypage-tab active">즐겨찾기</div>
            <div class="mypage-tab" onclick="location.href='/mypage/history'">이용 내역</div>
        </div>
        
        <div class="favorites-container">
            <h2>즐겨찾기한 주차장</h2>
            <p>자주 이용하는 주차장을 관리하세요.</p>
            
            <div id="favorites-list">
                <!-- JavaScript에서 즐겨찾기 목록 내용이 채워집니다 -->
                <p class="loading">로딩 중...</p>
            </div>
        </div>
    </div>

    <!-- 부트스트랩 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- 커스텀 JS -->
    <script src="/js/auth.js"></script>
    <script src="/js/mypage.js"></script>
    
    <script>
        // 페이지 로드 시 사용자 인증 확인
        document.addEventListener('DOMContentLoaded', function() {
            // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
            if (!isAuthenticated()) {
                window.location.href = '/login-page?redirect=/mypage/favorites';
            }
        });
        
        // 로그아웃 함수
        function logout() {
            // 전역 로그아웃 함수 호출
            window.logout();
        }
        
        // 사용자 인증 확인
        function isAuthenticated() {
            // 전역 isAuthenticated 함수 호출
            return window.isAuthenticated();
        }
    </script>
</body>
</html> 