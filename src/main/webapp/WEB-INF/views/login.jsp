<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인 - Parking Hub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/common.css" rel="stylesheet">
    <link href="/css/login.css" rel="stylesheet">
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand logo" href="/">Parking Hub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav mx-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/">홈</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/mapSearch">검색</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/dashboard">대시보드</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/community">커뮤니티</a>
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
            <h2 class="text-center mb-3">로그인</h2>
            <p class="text-center text-muted mb-4">Parking Hub에 오신 것을 환영합니다.</p>
            
            <c:if test="${param.error != null}">
                <div class="error-message text-center">
                    아이디 또는 비밀번호가 올바르지 않습니다.
                </div>
            </c:if>
            
            <c:if test="${param.registered != null}">
                <div class="success-message text-center">
                    회원가입이 완료되었습니다. 로그인해주세요.
                </div>
            </c:if>
            
            <form action="/perform-login" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">이메일</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="name@example.com" required>
                </div>
                <div class="mb-3">
                    <label for="password" class="form-label">비밀번호</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="비밀번호를 입력하세요" required>
                </div>
                
                <div class="login-help">
                    <a href="#">아이디 찾기</a>
                    <span class="divider"></span>
                    <a href="#">비밀번호 찾기</a>
                </div>
                
                <div class="d-grid gap-2 mt-4">
                    <button type="submit" class="btn btn-primary py-2">로그인</button>
                </div>
            </form>
            
            <div class="text-center mt-4">
                <p class="mb-0">아직 회원이 아니신가요? <a href="/register" class="text-decoration-none" style="color: #4dabf7;">회원가입하기</a></p>
            </div>
        </div>
    </div>
    
    <footer class="text-center">
        <div class="container">
            <p class="mb-0 small">© 2023 Parking Hub. All rights reserved.</p>
            <p class="mb-0 small">
                <a href="#" class="text-decoration-none text-muted">이용약관</a> | 
                <a href="#" class="text-decoration-none text-muted">개인정보처리방침</a> | 
                <a href="#" class="text-decoration-none text-muted">문의하기</a>
            </p>
        </div>
    </footer>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 