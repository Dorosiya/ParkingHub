<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>비밀번호 변경 - Parking Hub</title>
    
    <!-- 부트스트랩 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/mypage.css">
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="/">Parking Hub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
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
                        <a class="nav-link active" href="/mypage">마이페이지</a>
                    </li>
                </ul>
                <c:choose>
                    <c:when test="${isAuthenticated}">
                        <span class="navbar-text me-3">
                            <i class="bi bi-person-circle"></i> ${username}님
                        </span>
                        <a href="javascript:void(0);" onclick="logout()" class="btn btn-outline-light btn-sm">로그아웃</a>
                    </c:when>
                    <c:otherwise>
                        <a href="/login" class="btn btn-outline-light me-2">로그인</a>
                        <a href="/register" class="btn btn-outline-light">회원가입</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>

    <!-- 메인 컨텐츠 -->
    <div class="container mt-5">
        <div class="row">
            <div class="col-12">
                <h1 class="mb-4">비밀번호 변경</h1>
                
                <ul class="nav nav-tabs mb-4">
                    <li class="nav-item">
                        <a class="nav-link" href="/mypage">내 정보</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mypage/favorites">즐겨찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mypage/history">이용 내역</a>
                    </li>
                </ul>
                
                <div class="row">
                    <div class="col-md-8">
                        <!-- 비밀번호 변경 폼 -->
                        <div class="password-form">
                            <h3 class="mb-4">비밀번호 변경</h3>
                            
                            <c:if test="${not empty message}">
                                <div class="alert alert-success" role="alert">
                                    ${message}
                                </div>
                            </c:if>
                            
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger" role="alert">
                                    ${error}
                                </div>
                            </c:if>
                            
                            <!-- 알림 메시지 -->
                            <div id="alertMessage" class="alert" style="display: none;" role="alert"></div>
                            
                            <form id="passwordForm" action="/api/users/password" method="PUT">
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">현재 비밀번호</label>
                                    <input type="password" class="form-control" id="currentPassword" name="currentPassword" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">새 비밀번호</label>
                                    <input type="password" class="form-control" id="newPassword" name="newPassword" required>
                                    <div class="password-strength bg-light rounded"></div>
                                    <div class="password-feedback text-muted">비밀번호는 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.</div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">비밀번호 확인</label>
                                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required>
                                    <div class="confirm-feedback text-muted"></div>
                                </div>
                                
                                <div class="d-grid gap-2 mt-4">
                                    <button type="submit" class="btn btn-primary py-2">비밀번호 변경</button>
                                    <a href="/mypage/edit" class="btn btn-outline-secondary py-2">기본 정보 수정으로 돌아가기</a>
                                </div>
                            </form>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-body">
                                <h5 class="card-title">비밀번호 안내</h5>
                                <p class="card-text">강력한 비밀번호를 사용하여 계정 보안을 강화하세요.</p>
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item">
                                        <i class="fas fa-check text-success me-2"></i>
                                        8자 이상 입력
                                    </li>
                                    <li class="list-group-item">
                                        <i class="fas fa-check text-success me-2"></i>
                                        영문자 포함
                                    </li>
                                    <li class="list-group-item">
                                        <i class="fas fa-check text-success me-2"></i>
                                        숫자 포함
                                    </li>
                                    <li class="list-group-item">
                                        <i class="fas fa-check text-success me-2"></i>
                                        특수문자 포함 (!@#$%^&*)
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
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

    <!-- jQuery 먼저 로드 -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    
    <!-- 부트스트랩 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- 인증 스크립트 -->
    <script src="/js/auth.js"></script>
    
    <!-- 마이페이지 스크립트 -->
    <script src="/js/mypage.js"></script>
</body>
</html> 