<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>페이지를 찾을 수 없습니다 - Parking Hub</title>
    
    <!-- 부트스트랩 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="/css/common.css">
    
    <style>
        :root {
            --primary-color: #3498db;
            --secondary-color: #2980b9;
            --accent-color: #f39c12;
            --light-color: #ecf0f1;
            --dark-color: #2c3e50;
        }

        body {
            font-family: "Noto Sans KR", sans-serif;
            background-color: #f8f9fa;
            color: #333;
        }

        .navbar {
            background-color: var(--primary-color) !important;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .navbar-brand {
            font-weight: 700;
            color: white !important;
        }

        .nav-link {
            color: rgba(255, 255, 255, 0.85) !important;
            font-weight: 500;
            transition: all 0.3s;
        }

        .nav-link:hover {
            color: white !important;
        }
        
        .error-container {
            text-align: center;
            padding: 50px 20px;
        }
        
        .error-code {
            font-size: 120px;
            font-weight: 700;
            color: var(--primary-color);
            margin-bottom: 0;
        }
        
        .error-message {
            font-size: 24px;
            margin-bottom: 30px;
        }
        
        .footer {
            background-color: var(--dark-color);
            color: var(--light-color);
            padding: 30px 0 20px;
            margin-top: 50px;
        }
        
        .footer-link {
            color: var(--light-color);
            opacity: 0.8;
            text-decoration: none;
        }
        
        .footer-link:hover {
            opacity: 1;
            color: white;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="/">Parking Hub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/search">주차장 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mapSearch">지도로 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mypage">마이페이지</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- 오류 컨텐츠 -->
    <div class="container">
        <div class="error-container">
            <h1 class="error-code">404</h1>
            <h2 class="error-message">페이지를 찾을 수 없습니다</h2>
            <p class="mb-4">
                요청하신 페이지를 찾을 수 없습니다.<br>
                URL을 확인하시거나 홈으로 이동하세요.
            </p>
            <div>
                <a href="/" class="btn btn-primary me-2">홈으로 이동</a>
                <button onclick="history.back()" class="btn btn-outline-secondary">이전 페이지로</button>
            </div>
            <div class="mt-5">
                <div class="text-muted">요청 URL: ${url}</div>
            </div>
        </div>
    </div>
    
    <!-- 푸터 -->
    <footer class="footer mt-5 py-3 bg-dark text-white">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <h5>Parking Hub</h5>
                    <p>쉽고 편리한 주차 정보 서비스</p>
                </div>
                <div class="col-md-4">
                    <h5>바로가기</h5>
                    <ul class="list-unstyled">
                        <li><a href="/" class="text-white-50">홈</a></li>
                        <li><a href="/search" class="text-white-50">주차장 찾기</a></li>
                        <li><a href="/mapSearch" class="text-white-50">지도로 찾기</a></li>
                        <li><a href="/mypage" class="text-white-50">마이페이지</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>연락처</h5>
                    <p class="text-white-50">
                        <i class="fas fa-envelope"></i> contact@parkinghub.com<br>
                        <i class="fas fa-phone"></i> 02-123-4567
                    </p>
                </div>
            </div>
            <hr>
            <p class="text-center mb-0">&copy; 2025 Parking Hub. All rights reserved.</p>
        </div>
    </footer>

    <!-- 부트스트랩 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 