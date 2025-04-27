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
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/search.css">
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
                        <a class="nav-link" href="/map">지도로 찾기</a>
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
                            <input type="text" class="form-control" name="keyword" placeholder="주차장 이름 또는 주소" value="${keyword}">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <select class="form-select" name="region">
                            <option value="">지역 선택</option>
                            <option value="서울" ${region == '서울' ? 'selected' : ''}>서울특별시</option>
                            <option value="부산" ${region == '부산' ? 'selected' : ''}>부산광역시</option>
                            <option value="대구" ${region == '대구' ? 'selected' : ''}>대구광역시</option>
                            <option value="인천" ${region == '인천' ? 'selected' : ''}>인천광역시</option>
                            <option value="광주" ${region == '광주' ? 'selected' : ''}>광주광역시</option>
                            <option value="대전" ${region == '대전' ? 'selected' : ''}>대전광역시</option>
                            <option value="울산" ${region == '울산' ? 'selected' : ''}>울산광역시</option>
                            <option value="세종" ${region == '세종' ? 'selected' : ''}>세종특별자치시</option>
                            <option value="경기" ${region == '경기' ? 'selected' : ''}>경기도</option>
                            <option value="강원" ${region == '강원' ? 'selected' : ''}>강원도</option>
                            <option value="충북" ${region == '충북' ? 'selected' : ''}>충청북도</option>
                            <option value="충남" ${region == '충남' ? 'selected' : ''}>충청남도</option>
                            <option value="전북" ${region == '전북' ? 'selected' : ''}>전라북도</option>
                            <option value="전남" ${region == '전남' ? 'selected' : ''}>전라남도</option>
                            <option value="경북" ${region == '경북' ? 'selected' : ''}>경상북도</option>
                            <option value="경남" ${region == '경남' ? 'selected' : ''}>경상남도</option>
                            <option value="제주" ${region == '제주' ? 'selected' : ''}>제주특별자치도</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary w-100">검색</button>
                    </div>
                </div>
            </form>
        </div>

        <!-- 오류 메시지 표시 -->
        <c:if test="${not empty errorMessage}">
            <div class="alert alert-danger mt-3" role="alert">
                ${errorMessage}
            </div>
        </c:if>

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
    <footer class="footer">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>Parking Hub</h5>
                    <p>더 쉽고 빠른 주차 경험을 제공하는 서비스</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p>&copy; 2025 Parking Hub. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <!-- 인증 상태를 JavaScript로 전달하기 위한 숨겨진 필드 -->
    <input type="hidden" id="isAuthenticated" value="${isAuthenticated}" />
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/auth.js"></script>
    <script src="/js/search.js"></script>
</body>
</html> 