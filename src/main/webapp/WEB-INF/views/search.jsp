<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>주차장 검색 - Parking Hub</title>
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

        .page-header {
            background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
            color: white;
            padding: 40px 0;
            margin-bottom: 30px;
        }

        .search-form {
            background-color: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            margin-bottom: 30px;
        }

        .parking-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            padding: 20px;
            margin-bottom: 20px;
            transition: transform 0.3s ease;
        }

        .parking-card:hover {
            transform: translateY(-5px);
        }

        .parking-status {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 4px;
            font-weight: 500;
            font-size: 0.8rem;
        }

        .status-available {
            background-color: #2ecc71;
            color: white;
        }

        .status-limited {
            background-color: #f39c12;
            color: white;
        }

        .status-full {
            background-color: #e74c3c;
            color: white;
        }

        .status-unknown {
            background-color: #95a5a6;
            color: white;
        }
        
        .footer {
            background-color: var(--dark-color);
            color: var(--light-color);
            padding: 50px 0 20px;
            margin-top: 50px;
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
                        <a class="nav-link" href="/">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="/search">주차장 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mapSearch">지도로 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/dashboard">마이페이지</a>
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

    <!-- 페이지 헤더 -->
    <header class="page-header">
        <div class="container">
            <h1>주차장 검색</h1>
            <p>전국의 주차장 정보를 검색하고 실시간 주차 가능 여부를 확인하세요.</p>
        </div>
    </header>
    
    <!-- 검색 폼 -->
    <div class="container">
        <div class="search-form">
            <form action="/search" method="get">
                <div class="row g-3">
                    <div class="col-md-6">
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                            <input type="text" class="form-control" name="keyword" placeholder="주차장 이름 또는 주소" value="${param.keyword}">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <select class="form-select" name="region">
                            <option value="">지역 선택</option>
                            <option value="서울" ${param.region == '서울' ? 'selected' : ''}>서울특별시</option>
                            <option value="부산" ${param.region == '부산' ? 'selected' : ''}>부산광역시</option>
                            <option value="대구" ${param.region == '대구' ? 'selected' : ''}>대구광역시</option>
                            <option value="인천" ${param.region == '인천' ? 'selected' : ''}>인천광역시</option>
                            <option value="광주" ${param.region == '광주' ? 'selected' : ''}>광주광역시</option>
                            <option value="대전" ${param.region == '대전' ? 'selected' : ''}>대전광역시</option>
                            <option value="울산" ${param.region == '울산' ? 'selected' : ''}>울산광역시</option>
                            <option value="세종" ${param.region == '세종' ? 'selected' : ''}>세종특별자치시</option>
                            <option value="경기" ${param.region == '경기' ? 'selected' : ''}>경기도</option>
                            <option value="강원" ${param.region == '강원' ? 'selected' : ''}>강원도</option>
                            <option value="충북" ${param.region == '충북' ? 'selected' : ''}>충청북도</option>
                            <option value="충남" ${param.region == '충남' ? 'selected' : ''}>충청남도</option>
                            <option value="전북" ${param.region == '전북' ? 'selected' : ''}>전라북도</option>
                            <option value="전남" ${param.region == '전남' ? 'selected' : ''}>전라남도</option>
                            <option value="경북" ${param.region == '경북' ? 'selected' : ''}>경상북도</option>
                            <option value="경남" ${param.region == '경남' ? 'selected' : ''}>경상남도</option>
                            <option value="제주" ${param.region == '제주' ? 'selected' : ''}>제주특별자치도</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary w-100">검색</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- 검색 결과 -->
        <div class="row">
            <div class="col-12 mb-4">
                <p class="text-muted">
                    <c:choose>
                        <c:when test="${empty parkingList}">
                            검색 결과가 없습니다.
                        </c:when>
                        <c:otherwise>
                            총 ${parkingList.size()}개의 주차장이 검색되었습니다.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            
            <c:forEach var="parking" items="${parkingList}">
                <div class="col-md-6 col-lg-4">
                    <div class="parking-card">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h5 class="mb-0">${parking.prkPlceNm}</h5>
                            <c:choose>
                                <c:when test="${parking.free}">
                                    <span class="badge bg-success">무료</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-info">유료</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="mb-3">
                            <p class="text-muted small mb-1">
                                <i class="bi bi-geo-alt-fill"></i> ${parking.prkPlceAdres}
                            </p>
                            <p class="text-muted small mb-1">
                                <i class="bi bi-p-circle-fill"></i> 주차 공간: ${parking.prkCmprtCo}대
                            </p>
                            <p class="text-muted small mb-0">
                                <i class="bi bi-currency-dollar"></i> ${parking.parkingFeeInfo}
                            </p>
                        </div>
                        <div class="d-flex justify-content-between align-items-center">
                            <a href="/parking/${parking.prkCenterId}" class="btn btn-sm btn-outline-primary">상세 정보</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

    <!-- 푸터 -->
    <footer class="footer mt-auto">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <h5>Parking Hub</h5>
                    <p class="text-muted">더 쉽고 빠른 주차 경험을 제공하는 서비스</p>
                </div>
                <div class="col-md-4">
                    <h5>바로가기</h5>
                    <ul class="list-unstyled">
                        <li><a href="/" class="text-decoration-none footer-link">홈</a></li>
                        <li><a href="/search" class="text-decoration-none footer-link">주차장 찾기</a></li>
                        <li><a href="/mapSearch" class="text-decoration-none footer-link">지도로 찾기</a></li>
                        <li><a href="/dashboard" class="text-decoration-none footer-link">마이페이지</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>고객센터</h5>
                    <p class="text-muted">문의: support@parkinghub.co.kr</p>
                    <p class="text-muted">전화: 1234-5678 (평일 9:00-18:00)</p>
                </div>
            </div>
            <div class="text-center footer-bottom">
                <p>&copy; 2025 Parking Hub. All rights reserved.</p>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/auth.js"></script>
    <script>
        $(document).ready(function() {
            // 서버 측 인증 상태 체크 (JSP 표현식을 문자열로 변환)
            var isServerAuthenticated = "<c:out value='${isAuthenticated}'/>" === "true";
            
            // 서버 인증이 되지 않았지만 클라이언트에 로그인 쿠키가 있는 경우에만 처리
            var isClientLoggedIn = document.cookie.split(';').some(item => item.trim().startsWith('logged_in='));
            
            // 이미 서버에서 인증된 경우
            if (isServerAuthenticated) {
                // 서버가 제공한 정보로 UI 이미 업데이트되어 있음
                // 아무 작업 필요 없음
            }
            // 클라이언트 측 쿠키로 로그인된 경우
            else if (isClientLoggedIn) {
                // 로그인/회원가입 버튼 숨기기
                $('#navbarNav').find('.d-flex:not(.logged-in-menu)').hide();
                
                // 이미 로그인 상태 UI가 있는지 확인
                if ($('.logged-in-menu').length === 0) {
                    // 서버에 현재 사용자 정보 요청
                    $.ajax({
                        url: '/api/user/current',
                        type: 'GET',
                        xhrFields: {
                            withCredentials: true
                        }
                    }).done(function(user) {
                        // 사용자 정보로 UI 업데이트
                        var userHtml = '<div class="d-flex logged-in-menu">' +
                            '<span class="navbar-text me-3">' +
                            '<i class="bi bi-person-circle"></i> ' + user.username + '님' +
                            '</span>' +
                            '<button id="logoutBtn" class="btn btn-outline-light btn-sm d-flex align-items-center justify-content-center" style="min-height: 31px;">로그아웃</button>' +
                            '</div>';
                        
                        $('#navbarNav .navbar-nav').after(userHtml);
                        
                        // 로그아웃 버튼 이벤트
                        $('#logoutBtn').click(function() {
                            $.ajax({
                                url: '/api/auth/logout',
                                type: 'POST',
                                xhrFields: {
                                    withCredentials: true
                                }
                            }).always(function() {
                                window.location.reload();
                            });
                        });
                    }).fail(function() {
                        // 토큰이 유효하지 않은 경우 쿠키 삭제
                        document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
                    });
                }
            }
        });
    </script>
</body>
</html> 