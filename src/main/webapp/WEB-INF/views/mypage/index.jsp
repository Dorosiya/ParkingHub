<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>마이페이지 | Parking Hub</title>

    <!-- 부트스트랩 CSS -->
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />

    <!-- FontAwesome -->
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
    />

    <!-- 커스텀 CSS -->
    <link rel="stylesheet" href="/css/common.css" />
    <link rel="stylesheet" href="/css/mypage.css" />

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
        color: var(--primary-color);
      }

      .edit-profile-btn {
        background-color: var(--primary-color);
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 4px;
        cursor: pointer;
        margin-top: 15px;
      }

      .edit-profile-btn:hover {
        background-color: var(--secondary-color);
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
    <nav class="navbar navbar-expand-lg navbar-dark">
      <div class="container">
        <a class="navbar-brand" href="/">Parking Hub</a>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
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
              <a
                href="javascript:void(0);"
                onclick="logout()"
                class="btn btn-outline-light btn-sm"
                >로그아웃</a
              >
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
          <h1 class="mb-4">마이페이지</h1>

          <ul class="nav nav-tabs mb-4">
            <li class="nav-item">
              <a class="nav-link active text-muted" href="/mypage">내 정보</a>
            </li>
            <li class="nav-item">
              <a class="nav-link text-muted" href="/mypage/favorites"
                >즐겨찾기</a
              >
            </li>
            <li class="nav-item">
              <a class="nav-link text-muted" href="/mypage/history"
                >이용 내역</a
              >
            </li>
          </ul>

          <div class="user-info-section">
            <h2>사용자 정보</h2>

            <div id="user-info-container">
              <!-- 사용자 정보가 로딩 중입니다 -->
              <p class="loading">사용자 정보 로딩 중...</p>
            </div>

            <button
              class="edit-profile-btn"
              onclick="location.href='/mypage/edit'"
            >
              프로필 수정
            </button>
          </div>

          <div class="shortcuts-section">
            <div
              class="shortcut-card"
              onclick="location.href='/mypage/favorites'"
            >
              <i class="fas fa-star shortcut-icon"></i>
              <h3>즐겨찾기</h3>
              <p>자주 이용하는 주차장을 관리하세요</p>
            </div>
            <div
              class="shortcut-card"
              onclick="location.href='/mypage/history'"
            >
              <i class="fas fa-history shortcut-icon"></i>
              <h3>이용 내역</h3>
              <p>과거 주차장 이용 기록을 확인하세요</p>
            </div>
            <div class="shortcut-card" onclick="location.href='/search'">
              <i class="fas fa-search shortcut-icon"></i>
              <h3>주차장 찾기</h3>
              <p>새로운 주차장을 검색하세요</p>
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
              <li>
                <a href="/mapSearch" class="text-white-50">지도로 찾기</a>
              </li>
              <li><a href="/mypage" class="text-white-50">마이페이지</a></li>
            </ul>
          </div>
          <div class="col-md-4">
            <h5>연락처</h5>
            <p class="text-white-50">
              <i class="fas fa-envelope"></i> contact@parkinghub.com<br />
              <i class="fas fa-phone"></i> 02-123-4567
            </p>
          </div>
        </div>
        <hr />
        <p class="text-center mb-0">
          &copy; 2025 Parking Hub. All rights reserved.
        </p>
      </div>
    </footer>

    <!-- jQuery 먼저 로드 -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <!-- 부트스트랩 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- 인증 스크립트 -->
    <script src="/js/auth.js"></script>

    <script>
      // 페이지 로드 시 사용자 인증 확인 및 정보 로드
      document.addEventListener("DOMContentLoaded", function () {
        // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
        if (!isAuthenticated()) {
          window.location.href = "/login?redirect=/mypage";
          return;
        }

        // 사용자 정보 로드
        loadUserInfo();
      });

      // 사용자 정보 로드
      function loadUserInfo() {
        // 통합된 로직으로 중복 요청 제거
        var apiCall = null;

        // jQuery가 있고 AuthService도 정의되어 있으면 그것 사용
        if (typeof $ !== "undefined" && typeof AuthService !== "undefined") {
          apiCall = AuthService.getCurrentUser();

          apiCall
            .done(function (user) {
              displayUserInfo(user);
            })
            .fail(function (xhr) {
              console.error("Error:", xhr);
              document.getElementById("user-info-container").innerHTML =
                '<p class="error">사용자 정보를 불러오는데 실패했습니다. 다시 시도해주세요.</p>';
            });
        } else {
          // jQuery나 AuthService가 없으면 fetch API 사용
          fetch("/api/auth/me")
            .then((response) => {
              if (!response.ok) {
                throw new Error("사용자 정보를 불러오는데 실패했습니다");
              }
              return response.json();
            })
            .then((user) => {
              displayUserInfo(user);
            })
            .catch((error) => {
              console.error("Error:", error);
              document.getElementById("user-info-container").innerHTML =
                '<p class="error">사용자 정보를 불러오는데 실패했습니다. 다시 시도해주세요.</p>';
            });
        }
      }

      // 사용자 정보 표시
      function displayUserInfo(user) {
        const userInfoContainer = document.getElementById(
          "user-info-container"
        );

        // 날짜 포맷팅
        let formattedDate = "정보 없음";
        if (user.createdAt) {
          const date = new Date(user.createdAt);
          formattedDate =
            date.getFullYear() +
            "년 " +
            (date.getMonth() + 1) +
            "월 " +
            date.getDate() +
            "일";
        }

        // 템플릿 리터럴이 아닌 DOM 방식으로 정보 표시
        var html =
          '<div class="user-info-item">' +
          '<span class="user-info-label">이메일</span>' +
          '<span class="user-info-value">' +
          (user.email || "정보 없음") +
          "</span>" +
          "</div>" +
          '<div class="user-info-item">' +
          '<span class="user-info-label">사용자명</span>' +
          '<span class="user-info-value">' +
          (user.username || "정보 없음") +
          "</span>" +
          "</div>" +
          '<div class="user-info-item">' +
          '<span class="user-info-label">연락처</span>' +
          '<span class="user-info-value">' +
          (user.phone || "미등록") +
          "</span>" +
          "</div>" +
          '<div class="user-info-item">' +
          '<span class="user-info-label">가입일</span>' +
          '<span class="user-info-value">' +
          formattedDate +
          "</span>" +
          "</div>";

        userInfoContainer.innerHTML = html;
      }
    </script>
  </body>
</html>
