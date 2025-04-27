<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>지도로 주차장 찾기 - Parking Hub</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
    />
    <link rel="stylesheet" href="/css/common.css" />
    <link rel="stylesheet" href="/css/mapSearch.css" />
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script
      type="text/javascript"
      src="//dapi.kakao.com/v2/maps/sdk.js?appkey=7de22b580589f48eae26aed074b09419&libraries=services"
    ></script>
  </head>
  <body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
      <div class="container">
        <a class="navbar-brand" href="/">Parking Hub</a>
        <button
          class="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
          <ul class="navbar-nav me-auto">
            <li class="nav-item">
              <a class="nav-link" href="/">홈</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/parking/search">주차장 찾기</a>
            </li>
            <li class="nav-item">
              <a class="nav-link active" href="/map">지도로 찾기</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/mypage">마이페이지</a>
            </li>
          </ul>
          <div class="d-flex">
            <c:choose>
              <c:when test="${isAuthenticated}">
                <span class="navbar-text me-3">
                  <i class="bi bi-person-circle"></i> ${username}님
                </span>
                <a href="/logout" class="btn btn-outline-light btn-sm"
                  >로그아웃</a
                >
              </c:when>
              <c:otherwise>
                <a href="/login" class="btn btn-outline-light btn-sm me-2"
                  >로그인</a
                >
                <a href="/register" class="btn btn-light btn-sm">회원가입</a>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </nav>

    <div class="main-content">
      <div class="container-fluid">
        <div class="row">
          <!-- 왼쪽 사이드바 - 검색 및 목록 -->
          <div class="col-md-3 p-3">
            <div class="search-panel mb-4">
              <h5 class="mb-3">주차장 검색</h5>
              <div class="mb-3">
                <div class="input-group">
                  <input
                    type="text"
                    id="searchAddress"
                    class="form-control"
                    placeholder="주소 검색"
                  />
                  <button class="btn btn-primary" type="button" id="searchBtn">
                    <i class="bi bi-search"></i>
                  </button>
                </div>
              </div>
              <div class="mb-3">
                <button
                  class="btn btn-outline-primary w-100"
                  id="currentLocationBtn"
                >
                  <i class="bi bi-geo-alt-fill"></i> 현재 위치로 검색
                </button>
              </div>
              <div class="d-flex justify-content-between mb-3">
                <div class="form-check form-check-inline">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="showPublic"
                    checked
                  />
                  <label class="form-check-label" for="showPublic">공영</label>
                </div>
                <div class="form-check form-check-inline">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="showPrivate"
                    checked
                  />
                  <label class="form-check-label" for="showPrivate">민영</label>
                </div>
                <div class="form-check form-check-inline">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="showFree"
                    checked
                  />
                  <label class="form-check-label" for="showFree">무료</label>
                </div>
                <div class="form-check form-check-inline">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    id="showPaid"
                    checked
                  />
                  <label class="form-check-label" for="showPaid">유료</label>
                </div>
              </div>
            </div>

            <!-- 주차장 목록 -->
            <div class="parking-list" id="parkingList">
              <p class="p-3">위치를 검색하거나, 현재 위치로 검색해주세요.</p>
            </div>
          </div>

          <!-- 오른쪽 지도 영역 -->
          <div class="col-md-9 p-3">
            <div id="map" class="mb-3"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 푸터 -->
    <footer class="footer mt-3">
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
    <script src="/js/auth.js"></script>
    <script src="/js/mapSearch.js"></script>
    <script>
      // 페이지 로드 후 지도 초기화
      document.addEventListener("DOMContentLoaded", function() {
        // Kakao 지도 API가 로드된 후에 초기화
        if (typeof kakao !== 'undefined' && kakao.maps) {
          initMap();
        } else {
          // Kakao 지도 API가 아직 로드되지 않은 경우
          console.error("Kakao Maps API is not loaded yet");
          
          // API 로드 확인 인터벌 설정
          let checkInterval = setInterval(function() {
            if (typeof kakao !== 'undefined' && kakao.maps) {
              clearInterval(checkInterval);
              initMap();
            }
          }, 500);
        }
      });
    </script>
  </body>
</html>
