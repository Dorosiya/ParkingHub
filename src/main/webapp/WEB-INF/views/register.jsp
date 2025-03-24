<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 - Parking Hub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/css/common.css" rel="stylesheet">
    <link href="/css/register.css" rel="stylesheet">
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
                        <a class="nav-link" href="/login">로그인</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="/register">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="register-container">
            <h2 class="text-center mb-3">회원가입</h2>
            <p class="text-center text-muted mb-4">Parking Hub에 가입하여 다양한 서비스를 이용해보세요.</p>
            
            <c:if test="${error != null}">
                <div class="error-message text-center">
                    ${error}
                </div>
            </c:if>
            
            <form action="/register" method="post" id="registerForm" onsubmit="return validateForm()">
                <div class="mb-3">
                    <label for="email" class="form-label">이메일</label>
                    <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com" required>
                    <div class="form-text">로그인 시 사용할 이메일 주소입니다.</div>
                </div>
                
                <div class="mb-3">
                    <label for="username" class="form-label">닉네임</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="커뮤니티에서 사용할 이름" required>
                    <div class="form-text">커뮤니티에서 사용할 이름입니다.</div>
                </div>
                
                <div class="mb-3">
                    <label for="password" class="form-label">비밀번호</label>
                    <input type="password" class="form-control" id="password" name="password" placeholder="8-20자, 영문, 숫자, 특수문자 포함" required>
                    <div class="form-text">8-20자, 영문, 숫자, 특수문자를 포함해야 합니다.</div>
                </div>
                
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">비밀번호 확인</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" placeholder="비밀번호를 다시 입력하세요" required>
                </div>
                
                <div class="mb-3">
                    <label for="phone" class="form-label">휴대전화 (선택)</label>
                    <input type="text" class="form-control" id="phone" name="phone" placeholder="'-' 없이 숫자만 입력">
                    <div class="form-text">선택 사항입니다.</div>
                </div>
                
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="termsCheck" required>
                    <label class="form-check-label" for="termsCheck">서비스 이용약관에 동의합니다 (필수)</label>
                </div>
                
                <div class="mb-4 form-check">
                    <input type="checkbox" class="form-check-input" id="privacyCheck" required>
                    <label class="form-check-label" for="privacyCheck">개인정보 처리방침에 동의합니다 (필수)</label>
                </div>
                
                <div class="d-grid gap-2">
                    <button type="submit" class="btn btn-primary py-2">회원가입</button>
                </div>
            </form>
            
            <div class="text-center mt-4">
                <p class="mb-0">이미 회원이신가요? <a href="/login" class="text-decoration-none" style="color: #4dabf7;">로그인하기</a></p>
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
    <script src="/js/register.js"></script>
</body>
</html> 