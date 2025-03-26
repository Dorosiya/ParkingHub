<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Parking Hub - 스마트 주차 가이드</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <style>
        :root {
            --primary-color: #3498db;
            --secondary-color: #2980b9;
            --accent-color: #f39c12;
            --light-color: #ecf0f1;
            --dark-color: #2c3e50;
        }
        
        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f8f9fa;
            color: #333;
        }
        
        .navbar {
            background-color: var(--primary-color);
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
        
        .nav-link.active {
            color: white !important;
            border-bottom: 2px solid white;
        }
        
        .hero-section {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            padding: 100px 0;
            text-align: center;
        }
        
        .hero-title {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 20px;
        }
        
        .hero-subtitle {
            font-size: 1.2rem;
            margin-bottom: 30px;
            opacity: 0.9;
        }
        
        .btn-primary {
            background-color: var(--accent-color);
            border-color: var(--accent-color);
            font-weight: 500;
            padding: 10px 24px;
        }
        
        .btn-primary:hover {
            background-color: #e67e22;
            border-color: #e67e22;
        }
        
        .feature-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            padding: 30px;
            margin-bottom: 30px;
            transition: transform 0.3s ease;
            text-align: center;
            height: 100%;
        }
        
        .feature-card:hover {
            transform: translateY(-5px);
        }
        
        .feature-icon {
            font-size: 2.5rem;
            color: var(--primary-color);
            margin-bottom: 20px;
        }
        
        .section-title {
            margin-bottom: 50px;
            text-align: center;
            font-weight: 700;
            color: var(--dark-color);
        }
        
        .section-padding {
            padding: 80px 0;
        }
        
        .cta-section {
            background-color: var(--dark-color);
            color: white;
            padding: 70px 0;
            text-align: center;
        }
        
        .footer {
            background-color: var(--dark-color);
            color: var(--light-color);
            padding: 50px 0 20px;
        }
        
        .footer-link {
            color: var(--light-color);
            opacity: 0.8;
            transition: opacity 0.3s;
        }
        
        .footer-link:hover {
            opacity: 1;
            color: white;
        }
        
        .social-icon {
            font-size: 1.5rem;
            margin-right: 15px;
            color: var(--light-color);
            transition: color 0.3s;
        }
        
        .social-icon:hover {
            color: var(--accent-color);
        }
        
        .footer-bottom {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid rgba(255, 255, 255, 0.1);
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
        <div class="container">
            <a class="navbar-brand" href="/">Parking Hub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link active" href="/">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/search">주차장 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/dashboard">마이페이지</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/community">커뮤니티</a>
                    </li>
                </ul>
                <div class="d-flex">
                    <c:choose>
                        <c:when test="${isAuthenticated}">
                            <span class="navbar-text me-3">
                                <i class="bi bi-person-circle"></i> ${username}님
                            </span>
                            <a href="/logout" class="btn btn-outline-light btn-sm">로그아웃</a>
                        </c:when>
                        <c:otherwise>
                            <a href="/login" class="btn btn-outline-light btn-sm me-2">로그인</a>
                            <a href="/register" class="btn btn-light btn-sm">회원가입</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </nav>

    <!-- 히어로 섹션 -->
    <section class="hero-section">
        <div class="container">
            <h1 class="hero-title">더 쉽고 빠른 주차 경험</h1>
            <p class="hero-subtitle">주차장을 쉽게 찾고, 예약하고, 이용해보세요.</p>
            <a href="/search" class="btn btn-primary btn-lg">주차장 찾기</a>
        </div>
    </section>

    <!-- 주요 기능 소개 -->
    <section class="section-padding">
        <div class="container">
            <h2 class="section-title">Parking Hub 서비스</h2>
            <div class="row">
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-search"></i>
                        </div>
                        <h3>실시간 주차장 검색</h3>
                        <p>현재 위치 기반으로 가까운 주차장을 찾고, 실시간 주차 가능 여부를 확인하세요.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-calendar-check"></i>
                        </div>
                        <h3>간편한 주차 예약</h3>
                        <p>미리 주차 공간을 예약하여 도착 시 바로 주차할 수 있습니다.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-credit-card"></i>
                        </div>
                        <h3>간편 결제</h3>
                        <p>앱에서 바로 결제하고 주차 영수증을 디지털로 관리하세요.</p>
                    </div>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-graph-up"></i>
                        </div>
                        <h3>주차 통계</h3>
                        <p>자주 가는 장소의 주차 패턴을 분석하여 최적의 주차 시간을 추천해드립니다.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-people"></i>
                        </div>
                        <h3>커뮤니티</h3>
                        <p>다른 사용자들과 주차 팁을 공유하고 정보를 교환하세요.</p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="feature-card">
                        <div class="feature-icon">
                            <i class="bi bi-bell"></i>
                        </div>
                        <h3>주차 알림</h3>
                        <p>주차 시간 만료 전 알림을 받아 연장 또는 출차 준비를 하세요.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 사용 방법 -->
    <section class="section-padding bg-light">
        <div class="container">
            <h2 class="section-title">이용 방법</h2>
            <div class="row">
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="d-flex flex-column align-items-center">
                        <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
                            <h3 class="text-white m-0">1</h3>
                        </div>
                        <h4>회원가입</h4>
                        <p class="text-center">간단한 정보로 빠르게 가입하세요.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="d-flex flex-column align-items-center">
                        <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
                            <h3 class="text-white m-0">2</h3>
                        </div>
                        <h4>주차장 검색</h4>
                        <p class="text-center">목적지 주변의 주차장을 검색합니다.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="d-flex flex-column align-items-center">
                        <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
                            <h3 class="text-white m-0">3</h3>
                        </div>
                        <h4>예약 또는 이용</h4>
                        <p class="text-center">원하는 시간에 예약하거나 바로 이용하세요.</p>
                    </div>
                </div>
                <div class="col-lg-3 col-md-6 mb-4">
                    <div class="d-flex flex-column align-items-center">
                        <div class="bg-primary rounded-circle d-flex align-items-center justify-content-center mb-3" style="width: 80px; height: 80px;">
                            <h3 class="text-white m-0">4</h3>
                        </div>
                        <h4>결제 완료</h4>
                        <p class="text-center">앱에서 간편하게 결제하고 이용 내역을 확인하세요.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 추천 주차장 -->
    <section class="section-padding">
        <div class="container">
            <h2 class="section-title">인기 주차장</h2>
            <div class="row">
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <img src="https://via.placeholder.com/300x150?text=주차장+이미지" class="card-img-top" alt="주차장 이미지">
                        <div class="card-body">
                            <h5 class="card-title">강남역 주차장</h5>
                            <p class="card-text">
                                <i class="bi bi-geo-alt text-primary"></i> 서울시 강남구<br>
                                <i class="bi bi-currency-exchange text-success"></i> 시간당 3,000원<br>
                                <i class="bi bi-p-circle text-info"></i> 남은 자리: 25
                            </p>
                            <a href="#" class="btn btn-primary btn-sm">자세히 보기</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <img src="https://via.placeholder.com/300x150?text=주차장+이미지" class="card-img-top" alt="주차장 이미지">
                        <div class="card-body">
                            <h5 class="card-title">코엑스 주차장</h5>
                            <p class="card-text">
                                <i class="bi bi-geo-alt text-primary"></i> 서울시 강남구<br>
                                <i class="bi bi-currency-exchange text-success"></i> 시간당 5,000원<br>
                                <i class="bi bi-p-circle text-info"></i> 남은 자리: 120
                            </p>
                            <a href="#" class="btn btn-primary btn-sm">자세히 보기</a>
                        </div>
                    </div>
                </div>
                <div class="col-md-4 mb-4">
                    <div class="card">
                        <img src="https://via.placeholder.com/300x150?text=주차장+이미지" class="card-img-top" alt="주차장 이미지">
                        <div class="card-body">
                            <h5 class="card-title">명동 공영주차장</h5>
                            <p class="card-text">
                                <i class="bi bi-geo-alt text-primary"></i> 서울시 중구<br>
                                <i class="bi bi-currency-exchange text-success"></i> 시간당 2,500원<br>
                                <i class="bi bi-p-circle text-info"></i> 남은 자리: 8
                            </p>
                            <a href="#" class="btn btn-primary btn-sm">자세히 보기</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 앱 홍보 섹션 -->
    <section class="cta-section">
        <div class="container">
            <div class="row align-items-center">
                <div class="col-md-8 mx-auto text-center">
                    <h2 class="mb-4">모바일 앱으로 더 편리하게 이용하세요</h2>
                    <p class="mb-4">Parking Hub 모바일 앱을 다운로드하여 언제 어디서나 주차장을 찾고 예약하세요.</p>
                    <div>
                        <a href="#" class="btn btn-light me-2 mb-2"><i class="bi bi-apple me-2"></i>App Store</a>
                        <a href="#" class="btn btn-light mb-2"><i class="bi bi-google-play me-2"></i>Google Play</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <!-- 푸터 -->
    <footer class="footer">
        <div class="container">
            <div class="row">
                <div class="col-md-4 mb-4">
                    <h5>Parking Hub</h5>
                    <p>더 쉽고 빠른 주차 경험을 제공하는<br>스마트 주차 가이드 서비스</p>
                    <div class="mt-3">
                        <a href="#" class="social-icon"><i class="bi bi-facebook"></i></a>
                        <a href="#" class="social-icon"><i class="bi bi-instagram"></i></a>
                        <a href="#" class="social-icon"><i class="bi bi-twitter"></i></a>
                        <a href="#" class="social-icon"><i class="bi bi-youtube"></i></a>
                    </div>
                </div>
                <div class="col-md-2 mb-4">
                    <h5>서비스</h5>
                    <ul class="list-unstyled">
                        <li><a href="#" class="footer-link">주차장 찾기</a></li>
                        <li><a href="#" class="footer-link">예약하기</a></li>
                        <li><a href="#" class="footer-link">요금 안내</a></li>
                        <li><a href="#" class="footer-link">이용 방법</a></li>
                    </ul>
                </div>
                <div class="col-md-2 mb-4">
                    <h5>고객지원</h5>
                    <ul class="list-unstyled">
                        <li><a href="#" class="footer-link">자주 묻는 질문</a></li>
                        <li><a href="#" class="footer-link">문의하기</a></li>
                        <li><a href="#" class="footer-link">이용약관</a></li>
                        <li><a href="#" class="footer-link">개인정보처리방침</a></li>
                    </ul>
                </div>
                <div class="col-md-4 mb-4">
                    <h5>뉴스레터 구독</h5>
                    <p>최신 소식과 프로모션 정보를 받아보세요.</p>
                    <form class="mt-3">
                        <div class="input-group">
                            <input type="email" class="form-control" placeholder="이메일 주소" required>
                            <button class="btn btn-primary" type="submit">구독</button>
                        </div>
                    </form>
                </div>
            </div>
            <div class="footer-bottom text-center">
                <p>&copy; 2023 Parking Hub. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 