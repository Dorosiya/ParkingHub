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
                        <a class="nav-link active" href="/login-page">로그인</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/register-page">회원가입</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container">
        <div class="login-container">
            <h2 class="text-center mb-3">로그인</h2>
            <p class="text-center text-muted mb-4">Parking Hub에 오신 것을 환영합니다.</p>
            
            <div id="error-message" class="error-message text-center" style="display: none;">
                아이디 또는 비밀번호가 올바르지 않습니다.
            </div>
            
            <c:if test="${param.registered != null}">
                <div class="success-message text-center">
                    회원가입이 완료되었습니다. 로그인해주세요.
                </div>
            </c:if>
            
            <form id="loginForm">
                <div class="mb-3">
                    <label for="email" class="form-label">이메일</label>
                    <input type="email" class="form-control" id="email" name="email" placeholder="이메일을 입력하세요" required>
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
                    <button type="button" class="btn btn-primary py-2" id="loginButton">로그인</button>
                </div>
            </form>
            
            <div class="text-center mt-4">
                <p class="mb-0">아직 회원이 아니신가요? <a href="/register-page" class="text-decoration-none" style="color: #4dabf7;">회원가입하기</a></p>
            </div>
        </div>
    </div>
    
    <footer class="text-center">
        <div class="container">
            <p class="mb-0 small">© 2025 Parking Hub. All rights reserved.</p>
            <p class="mb-0 small">
                <a href="#" class="text-decoration-none text-muted">이용약관</a> | 
                <a href="#" class="text-decoration-none text-muted">개인정보처리방침</a> | 
                <a href="#" class="text-decoration-none text-muted">문의하기</a>
            </p>
        </div>
    </footer>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/auth.js"></script>
    
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // 로그인 버튼 클릭 이벤트
            document.getElementById('loginButton').addEventListener('click', function() {
                const email = document.getElementById('email').value;
                const password = document.getElementById('password').value;
                
                // 입력 검증
                if (!email || !password) {
                    document.getElementById('error-message').textContent = '이메일과 비밀번호를 모두 입력해주세요.';
                    document.getElementById('error-message').style.display = 'block';
                    return;
                }
                
                // 로그인 요청
                login(email, password)
                    .then(data => {
                        // 로그인 성공 시 메인 페이지로 이동
                        window.location.href = '/';
                    })
                    .catch(error => {
                        // 로그인 실패 처리
                        document.getElementById('error-message').textContent = 
                            '이메일 또는 비밀번호가 올바르지 않습니다.';
                        document.getElementById('error-message').style.display = 'block';
                        console.error('로그인 오류:', error);
                    });
            });
            
            // 엔터키 처리
            document.querySelectorAll('#loginForm input').forEach(input => {
                input.addEventListener('keypress', function(e) {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        document.getElementById('loginButton').click();
                    }
                });
            });
        });
    </script>
</body>
</html> 