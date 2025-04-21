<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인 - Parking Hub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="/css/common.css" rel="stylesheet">
    <link href="/css/login.css" rel="stylesheet">
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
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" href="/login">로그인</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/register">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="login-container">
            <h2 class="text-center mb-4">로그인</h2>
            
            <c:if test="${param.registered eq 'true'}">
                <div class="alert alert-success text-center mb-3">
                    회원가입이 완료되었습니다. 로그인해주세요.
                </div>
            </c:if>
            
            <c:if test="${param.error eq 'true'}">
                <div class="alert alert-danger text-center mb-3">
                    아이디 또는 비밀번호가 일치하지 않습니다.
                </div>
            </c:if>
            
            <c:if test="${param.logout eq 'true'}">
                <div class="alert alert-success text-center mb-3">
                    로그아웃되었습니다.
                </div>
            </c:if>
            
            <div id="loginAlert" class="alert alert-danger text-center mb-3" style="display: none;"></div>
            
            <form id="loginForm">
                <div class="mb-3">
                    <label for="username" class="form-label">아이디</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">비밀번호</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="rememberMe" name="remember-me">
                    <label class="form-check-label" for="rememberMe">로그인 상태 유지</label>
                </div>
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary btn-lg">로그인</button>
                </div>
                <div class="text-center mt-3">
                    <a href="#" class="text-decoration-none text-muted small">비밀번호를 잊으셨나요?</a>
                </div>
            </form>
            
            <hr class="my-4">
            
            <div class="text-center">
                <p class="mb-3">아직 계정이 없으신가요?</p>
                <a href="/register" class="btn btn-outline-primary">회원가입</a>
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
            <p class="text-center mb-0">&copy; 2023 Parking Hub. All rights reserved.</p>
        </div>
    </footer>

    <!-- jQuery 및 Bootstrap JavaScript -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/login.js"></script>
</body>
</html> 