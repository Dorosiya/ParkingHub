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
                    <label for="username" class="form-label">아이디</label>
                    <input type="text" class="form-control" id="username" name="username" placeholder="커뮤니티에서 사용할 아이디" required>
                    <div class="form-text">커뮤니티에서 사용할 아이디입니다.</div>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">이메일</label>
                    <input type="email" class="form-control" id="email" name="email" placeholder="name@example.com" required>
                    <div class="form-text">로그인 시 사용할 이메일 주소입니다.</div>
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
                    <label for="phone" class="form-label">휴대전화</label>
                    <input type="text" class="form-control" id="phone" name="phone" placeholder="핸드폰 번호를 입력하세요">
                    <div class="form-text">선택 사항입니다.</div>
                </div>
                
                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="termsCheck" required>
                    <label class="form-check-label" for="termsCheck">
                        <a href="#" data-bs-toggle="modal" data-bs-target="#termsModal">서비스 이용약관</a>에 동의합니다 (필수)
                    </label>
                </div>
                
                <div class="mb-4 form-check">
                    <input type="checkbox" class="form-check-input" id="privacyCheck" required>
                    <label class="form-check-label" for="privacyCheck">
                        <a href="#" data-bs-toggle="modal" data-bs-target="#privacyModal">개인정보 처리방침</a>에 동의합니다 (필수)
                    </label>
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
    
    <!-- 이용약관 모달 -->
    <div class="modal fade" id="termsModal" tabindex="-1" aria-labelledby="termsModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="termsModalLabel">서비스 이용약관</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h6>제1조 (목적)</h6>
                    <p>이 약관은 Parking Hub(이하 "회사")가 제공하는 주차장 정보 서비스(이하 "서비스")의 이용조건 및 절차, 회사와 회원 간의 권리, 의무 및 책임사항 등을 규정함을 목적으로 합니다.</p>
                    
                    <h6>제2조 (용어의 정의)</h6>
                    <p>이 약관에서 사용하는 용어의 정의는 다음과 같습니다.</p>
                    <ul>
                        <li>"서비스"란 회사가 제공하는 모든 서비스를 의미합니다.</li>
                        <li>"회원"이란 회사와 서비스 이용계약을 체결하고 회사가 제공하는 서비스를 이용하는 자를 의미합니다.</li>
                        <li>"아이디(ID)"란 회원의 식별과 서비스 이용을 위하여 회원이 설정하고 회사가 승인하는 문자와 숫자의 조합을 의미합니다.</li>
                    </ul>
                    
                    <h6>제3조 (약관의 효력 및 변경)</h6>
                    <p>회사는 약관의 내용을 회원이 쉽게 알 수 있도록 서비스 초기 화면에 게시하거나 기타의 방법으로 공지합니다.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                </div>
            </div>
        </div>
    </div>

    <!-- 개인정보처리방침 모달 -->
    <div class="modal fade" id="privacyModal" tabindex="-1" aria-labelledby="privacyModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="privacyModalLabel">개인정보 처리방침</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <h6>1. 개인정보의 수집 및 이용 목적</h6>
                    <p>회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 개인정보 보호법 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.</p>
                    
                    <h6>2. 개인정보의 처리 및 보유기간</h6>
                    <p>회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.</p>
                    
                    <h6>3. 개인정보의 제3자 제공</h6>
                    <p>회사는 정보주체의 개인정보를 제1조(개인정보의 처리 목적)에서 명시한 범위 내에서만 처리하며, 정보주체의 동의, 법률의 특별한 규정 등 개인정보 보호법 제17조에 해당하는 경우에만 개인정보를 제3자에게 제공합니다.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="/js/register.js"></script>
</body>
</html> 