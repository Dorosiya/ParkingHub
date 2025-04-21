<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - Parking Hub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="/css/common.css" rel="stylesheet">
    <link href="/css/register.css" rel="stylesheet">
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

        .nav-link.active {
            color: white !important;
            border-bottom: 2px solid white;
        }

        .register-container {
            max-width: 550px;
            margin: 50px auto;
            background-color: white;
            border-radius: 10px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
        }

        .btn-primary {
            background-color: var(--primary-color);
            border-color: var(--primary-color);
        }

        .btn-primary:hover {
            background-color: var(--secondary-color);
            border-color: var(--secondary-color);
        }

        .error-message {
            color: #dc3545;
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 0.25rem;
            padding: 0.75rem 1.25rem;
            margin-bottom: 1rem;
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
            
            <form action="/register" method="post" id="registerForm">
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
    
    <!-- 이용약관 모달 -->
    <div class="modal fade" id="termsModal" tabindex="-1" aria-labelledby="termsModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="termsModalLabel">서비스 이용약관</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="max-height: 400px; overflow-y: auto;">
                    <h5>제1조 (목적)</h5>
                    <p>이 약관은 주차장 정보 서비스(이하 "서비스")를 제공하는 Parking Hub(이하 "회사")와 이를 이용하는 회원(이하 "회원") 간에 서비스 이용에 관한 권리, 의무 및 책임사항, 기타 필요한 사항을 규정함을 목적으로 합니다.</p>
                    
                    <h5>제2조 (정의)</h5>
                    <p>"서비스"라 함은 구현되는 단말기와 상관없이 "회원"이 이용할 수 있는 주차장 정보 서비스 및 관련 부가 서비스를 의미합니다.</p>
                    <p>"회원"이라 함은 "회사"의 "서비스"에 접속하여 이 약관에 따라 "회사"와 이용계약을 체결하고 "회사"가 제공하는 "서비스"를 이용하는 고객을 말합니다.</p>
                    <p>"아이디(ID)"라 함은 "회원"의 식별과 "서비스" 이용을 위하여 "회원"이 정하고 "회사"가 승인하는 문자와 숫자의 조합을 의미합니다.</p>
                    
                    <h5>제3조 (약관의 게시와 개정)</h5>
                    <p>"회사"는 이 약관의 내용을 "회원"이 쉽게 알 수 있도록 서비스 초기 화면에 게시합니다.</p>
                    <p>"회사"는 "약관의 규제에 관한 법률", "정보통신망 이용촉진 및 정보보호 등에 관한 법률" 등 관련법을 위배하지 않는 범위에서 이 약관을 개정할 수 있습니다.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
                </div>
            </div>
        </div>
    </div>
    
    <!-- 개인정보 처리방침 모달 -->
    <div class="modal fade" id="privacyModal" tabindex="-1" aria-labelledby="privacyModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="privacyModalLabel">개인정보 처리방침</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body" style="max-height: 400px; overflow-y: auto;">
                    <p>Parking Hub(이하 "회사")는 개인정보 보호법 제30조에 따라 정보주체의 개인정보를 보호하고 이와 관련한 고충을 신속하고 원활하게 처리할 수 있도록 하기 위하여 다음과 같이 개인정보 처리방침을 수립·공개합니다.</p>
                    
                    <h5>제1조 (개인정보의 처리 목적)</h5>
                    <p>회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리하고 있는 개인정보는 다음의 목적 이외의 용도로는 이용되지 않으며, 이용 목적이 변경되는 경우에는 개인정보 보호법 제18조에 따라 별도의 동의를 받는 등 필요한 조치를 이행할 예정입니다.</p>
                    <ol>
                        <li>회원 가입 및 관리: 회원 가입의사 확인, 회원제 서비스 제공에 따른 본인 식별·인증, 회원자격 유지·관리, 서비스 부정이용 방지, 고충처리 등을 목적으로 개인정보를 처리합니다.</li>
                        <li>서비스 제공: 주차장 정보 제공, 콘텐츠 제공, 맞춤형 서비스 제공 등을 목적으로 개인정보를 처리합니다.</li>
                    </ol>
                    
                    <h5>제2조 (개인정보의 처리 및 보유 기간)</h5>
                    <p>회사는 법령에 따른 개인정보 보유·이용기간 또는 정보주체로부터 개인정보를 수집 시에 동의 받은 개인정보 보유·이용기간 내에서 개인정보를 처리·보유합니다.</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">닫기</button>
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
                        <li><a href="/" class="footer-link">홈</a></li>
                        <li><a href="/search" class="footer-link">주차장 찾기</a></li>
                        <li><a href="/mapSearch" class="footer-link">지도로 찾기</a></li>
                        <li><a href="/mypage" class="footer-link">마이페이지</a></li>
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
    <script src="/js/register.js"></script>
</body>
</html> 
