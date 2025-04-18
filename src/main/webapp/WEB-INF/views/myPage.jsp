<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 | ParkingHub</title>
    
    <!-- 부트스트랩 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <!-- FontAwesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/mypage.css">

    <style>
        .user-info-section {
            background-color: #fff;
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
        }
        
        .user-info-item {
            margin-bottom: 15px;
            display: flex;
            flex-direction: column;
        }
        
        .user-info-label {
            font-size: 14px;
            color: #888;
            margin-bottom: 5px;
        }
        
        .user-info-value {
            font-size: 16px;
            color: #333;
        }
        
        .shortcuts-section {
            display: flex;
            flex-wrap: wrap;
            gap: 15px;
            margin-top: 20px;
        }
        
        .shortcut-card {
            flex: 1;
            min-width: 200px;
            background-color: #fff;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            cursor: pointer;
            transition: transform 0.2s ease, box-shadow 0.2s ease;
            text-align: center;
        }
        
        .shortcut-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        }
        
        .shortcut-icon {
            font-size: 36px;
            margin-bottom: 10px;
            color: #2196F3;
        }
        
        .edit-profile-btn {
            background-color: #2196F3;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 15px;
        }
        
        .edit-profile-btn:hover {
            background-color: #1e88e5;
        }

        @media (max-width: 768px) {
            .shortcut-card {
                min-width: 100%;
            }
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <a class="navbar-brand" href="/">ParkingHub</a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav"
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="/parking/search">주차장 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/map">지도 보기</a>
                    </li>
                </ul>
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link active" href="/mypage">마이페이지</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="javascript:void(0);" onclick="logout()">로그아웃</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- 메인 컨텐츠 -->
    <div class="mypage-container">
        <div class="mypage-header">
            <h1 class="mypage-title">마이페이지</h1>
        </div>
        
        <div class="mypage-tabs">
            <div class="mypage-tab active">내 정보</div>
            <div class="mypage-tab" onclick="location.href='/mypage/favorites'">즐겨찾기</div>
            <div class="mypage-tab" onclick="location.href='/mypage/history'">이용 내역</div>
        </div>
        
        <div class="user-profile-container">
            <div class="user-info-section">
                <h2>사용자 정보</h2>
                
                <div id="user-info-container">
                    <!-- 사용자 정보가 로딩 중입니다 -->
                    <p class="loading">사용자 정보 로딩 중...</p>
                </div>
                
                <button class="edit-profile-btn" onclick="location.href='/mypage/edit'">
                    프로필 수정
                </button>
            </div>
            
            <div class="shortcuts-section">
                <div class="shortcut-card" onclick="location.href='/mypage/favorites'">
                    <i class="fas fa-star shortcut-icon"></i>
                    <h3>즐겨찾기</h3>
                    <p>자주 이용하는 주차장을 관리하세요</p>
                </div>
                <div class="shortcut-card" onclick="location.href='/mypage/history'">
                    <i class="fas fa-history shortcut-icon"></i>
                    <h3>이용 내역</h3>
                    <p>과거 주차장 이용 기록을 확인하세요</p>
                </div>
                <div class="shortcut-card" onclick="location.href='/parking/search'">
                    <i class="fas fa-search shortcut-icon"></i>
                    <h3>주차장 찾기</h3>
                    <p>새로운 주차장을 검색하세요</p>
                </div>
            </div>
        </div>
    </div>

    <!-- 부트스트랩 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js"></script>
    
    <!-- 커스텀 JS -->
    <script src="/js/auth.js"></script>
    
    <script>
        // 페이지 로드 시 사용자 인증 확인 및 정보 로드
        document.addEventListener('DOMContentLoaded', function() {
            // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
            if (!isAuthenticated()) {
                window.location.href = '/login-page?redirect=/mypage';
                return;
            }
            
            // 사용자 정보 로드
            loadUserInfo();
        });
        
        // 사용자 정보 로드
        function loadUserInfo() {
            fetch('/api/users/me', {
                headers: {
                    'Authorization': 'Bearer ' + localStorage.getItem('token')
                }
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('사용자 정보를 불러오는데 실패했습니다');
                }
                return response.json();
            })
            .then(user => {
                displayUserInfo(user);
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('user-info-container').innerHTML = 
                    '<p class="error">사용자 정보를 불러오는데 실패했습니다. 다시 시도해주세요.</p>';
            });
        }
        
        // 사용자 정보 표시
        function displayUserInfo(user) {
            const userInfoContainer = document.getElementById('user-info-container');
            
            // 날짜 포맷팅
            let formattedDate = '정보 없음';
            if (user.createdAt) {
                const date = new Date(user.createdAt);
                formattedDate = date.getFullYear() + '년 ' + 
                               (date.getMonth() + 1) + '월 ' + 
                               date.getDate() + '일';
            }
            
            userInfoContainer.innerHTML = `
                <div class="user-info-item">
                    <span class="user-info-label">이메일</span>
                    <span class="user-info-value">${user.email}</span>
                </div>
                <div class="user-info-item">
                    <span class="user-info-label">사용자명</span>
                    <span class="user-info-value">${user.username}</span>
                </div>
                <div class="user-info-item">
                    <span class="user-info-label">연락처</span>
                    <span class="user-info-value">${user.phoneNumber || '미등록'}</span>
                </div>
                <div class="user-info-item">
                    <span class="user-info-label">가입일</span>
                    <span class="user-info-value">${formattedDate}</span>
                </div>
            `;
        }
        
        // 로그아웃 함수
        function logout() {
            removeAuthToken();
            window.location.href = '/';
        }
    </script>
</body>
</html> 