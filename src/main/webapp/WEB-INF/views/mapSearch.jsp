<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>지도로 주차장 찾기 - Parking Hub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
    <script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=7de22b580589f48eae26aed074b09419&libraries=services"></script>
    <style>
        :root {
            --primary-color: #3498db;
            --secondary-color: #2980b9;
            --accent-color: #f39c12;
            --light-color: #ecf0f1;
            --dark-color: #2c3e50;
        }

        body {
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f8f9fa;
            color: #333;
            height: 100vh;
            display: flex;
            flex-direction: column;
        }

        .navbar {
            background-color: var(--primary-color);
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

        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        #map {
            width: 100%;
            flex: 1;
            min-height: 500px;
        }

        .search-panel {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            padding: 20px;
            margin: 20px 0;
        }

        .parking-info-window {
            padding: 5px;
            max-width: 300px;
        }

        .parking-info-window h5 {
            margin-bottom: 5px;
            font-size: 16px;
        }

        .parking-info-window p {
            margin-bottom: 5px;
            font-size: 13px;
        }

        .parking-list {
            height: 300px;
            overflow-y: auto;
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
        }

        .parking-item {
            padding: 10px 15px;
            border-bottom: 1px solid #f0f0f0;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .parking-item:hover {
            background-color: #f8f9fa;
        }

        .parking-item h6 {
            margin-bottom: 5px;
            font-size: 15px;
        }

        .parking-item p {
            margin-bottom: 0;
            font-size: 12px;
            color: #6c757d;
        }

        .footer {
            background-color: var(--dark-color);
            color: var(--light-color);
            padding: 30px 0;
        }

        .footer-link {
            color: var(--light-color);
            opacity: 0.8;
            transition: opacity 0.3s;
        }

        .footer-link:hover {
            opacity: 1;
            color: white;
        }
    </style>
</head>
<body>
    <!-- 네비게이션 바 -->
    <nav class="navbar navbar-expand-lg navbar-dark sticky-top">
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
                        <a class="nav-link active" href="/mapSearch">지도로 찾기</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="/dashboard">마이페이지</a>
                    </li>
                </ul>
                <div class="d-flex">
                    <c:choose>
                        <c:when test="${isAuthenticated}">
                            <span class="navbar-text me-3">
                                <i class="bi bi-person-circle"></i> ${username}님
                            </span>
                            <a href="/logout" class="btn btn-outline-light btn-sm">로그아웃</a>
                        </c:when>
                        <c:otherwise>
                            <a href="/login" class="btn btn-outline-light btn-sm me-2">로그인</a>
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
                    <div class="search-panel">
                        <h5 class="mb-3">주차장 검색</h5>
                        <div class="mb-3">
                            <div class="input-group">
                                <input type="text" id="searchAddress" class="form-control" placeholder="주소 검색">
                                <button class="btn btn-primary" type="button" id="searchBtn">
                                    <i class="bi bi-search"></i>
                                </button>
                            </div>
                        </div>
                        <div class="mb-3">
                            <button class="btn btn-outline-primary w-100" id="currentLocationBtn">
                                <i class="bi bi-geo-alt-fill"></i> 현재 위치로 검색
                            </button>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="showPublic" checked>
                                <label class="form-check-label" for="showPublic">공영</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="showPrivate" checked>
                                <label class="form-check-label" for="showPrivate">민영</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="showFree" checked>
                                <label class="form-check-label" for="showFree">무료</label>
                            </div>
                            <div class="form-check form-check-inline">
                                <input class="form-check-input" type="checkbox" id="showPaid" checked>
                                <label class="form-check-label" for="showPaid">유료</label>
                            </div>
                        </div>
                    </div>
                    
                    <h5 class="mb-2">주변 주차장</h5>
                    <div class="parking-list" id="parkingList">
                        <!-- 주차장 목록이 여기에 동적으로 추가됩니다 -->
                        <div class="p-3 text-center text-muted">
                            지도에서 위치를 지정하면<br>주변 주차장이 표시됩니다.
                        </div>
                    </div>
                </div>
                
                <!-- 오른쪽 - 지도 -->
                <div class="col-md-9 p-0">
                    <div id="map"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 푸터 -->
    <footer class="footer">
        <div class="container">
            <div class="row">
                <div class="col-md-6">
                    <h5>Parking Hub</h5>
                    <p class="text-muted">더 쉽고 빠른 주차 경험을 제공하는 서비스</p>
                </div>
                <div class="col-md-6 text-md-end">
                    <p>&copy; 2023 Parking Hub. All rights reserved.</p>
                </div>
            </div>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/auth.js"></script>
    <script>
        $(document).ready(function() {
            // 서버 측 인증 상태 체크 (JSP 표현식을 문자열로 변환)
            var isServerAuthenticated = "<c:out value='${isAuthenticated}'/>" === "true";
            
            // 서버 인증이 되지 않았지만 클라이언트에 로그인 쿠키가 있는 경우에만 처리
            var isClientLoggedIn = document.cookie.split(';').some(item => item.trim().startsWith('logged_in='));
            
            // 이미 서버에서 인증된 경우
            if (isServerAuthenticated) {
                // 서버가 제공한 정보로 UI 이미 업데이트되어 있음
                // 아무 작업 필요 없음
            }
            // 클라이언트 측 쿠키로 로그인된 경우
            else if (isClientLoggedIn) {
                // 로그인/회원가입 버튼 숨기기
                $('#navbarNav').find('.d-flex:not(.logged-in-menu)').hide();
                
                // 이미 로그인 상태 UI가 있는지 확인
                if ($('.logged-in-menu').length === 0) {
                    // 서버에 현재 사용자 정보 요청
                    $.ajax({
                        url: '/api/user/current',
                        type: 'GET',
                        xhrFields: {
                            withCredentials: true
                        }
                    }).done(function(user) {
                        // 사용자 정보로 UI 업데이트
                        var userHtml = '<div class="d-flex logged-in-menu">' +
                            '<span class="navbar-text me-3">' +
                            '<i class="bi bi-person-circle"></i> ' + user.username + '님' +
                            '</span>' +
                            '<button id="logoutBtn" class="btn btn-outline-light btn-sm d-flex align-items-center justify-content-center" style="min-height: 31px;">로그아웃</button>' +
                            '</div>';
                        
                        $('#navbarNav .navbar-nav').after(userHtml);
                        
                        // 로그아웃 버튼 이벤트
                        $('#logoutBtn').click(function() {
                            $.ajax({
                                url: '/api/auth/logout',
                                type: 'POST',
                                xhrFields: {
                                    withCredentials: true
                                }
                            }).always(function() {
                                window.location.reload();
                            });
                        });
                    }).fail(function() {
                        // 토큰이 유효하지 않은 경우 쿠키 삭제
                        document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
                    });
                }
            }
        });

        // 지도 관련 JavaScript 코드
        var map;
        var markers = [];
        var parkingInfos = [];
        
        function initMap() {
            // 기본 위치 (서울시청)
            var defaultPosition = new kakao.maps.LatLng(37.5666805, 126.9784147);
            
            // 지도 초기화
            var container = document.getElementById('map');
            var options = {
                center: defaultPosition,
                level: 3
            };
            
            map = new kakao.maps.Map(container, options);
            
            // 처음 로드 시 현재 위치 확인
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var lat = position.coords.latitude;
                    var lng = position.coords.longitude;
                    var currentPosition = new kakao.maps.LatLng(lat, lng);
                    
                    // 지도 중심 이동
                    map.setCenter(currentPosition);
                    
                    // 현재 위치 마커 표시
                    var marker = new kakao.maps.Marker({
                        position: currentPosition,
                        map: map
                    });
                    
                    // 주변 주차장 검색
                    searchNearbyParking(lat, lng);
                });
            }
            
            // 지도 클릭 이벤트
            kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                var latlng = mouseEvent.latLng;
                searchNearbyParking(latlng.getLat(), latlng.getLng());
            });
            
            // 주소 검색 버튼 클릭 이벤트
            document.getElementById('searchBtn').addEventListener('click', function() {
                searchAddressToCoordinate();
            });
            
            // 현재 위치 버튼 클릭 이벤트
            document.getElementById('currentLocationBtn').addEventListener('click', function() {
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(function(position) {
                        var lat = position.coords.latitude;
                        var lng = position.coords.longitude;
                        var currentPosition = new kakao.maps.LatLng(lat, lng);
                        
                        // 지도 중심 이동
                        map.setCenter(currentPosition);
                        
                        // 주변 주차장 검색
                        searchNearbyParking(lat, lng);
                    });
                } else {
                    alert('현재 위치를 가져올 수 없습니다.');
                }
            });
            
            // 검색창 엔터키 이벤트
            document.getElementById('searchAddress').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    searchAddressToCoordinate();
                }
            });
        }
        
        // 주소로 좌표 검색
        function searchAddressToCoordinate() {
            var address = document.getElementById('searchAddress').value;
            
            if (!address) {
                alert('검색할 주소를 입력해주세요.');
                return;
            }
            
            var geocoder = new kakao.maps.services.Geocoder();
            
            geocoder.addressSearch(address, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                    
                    // 지도 중심 이동
                    map.setCenter(coords);
                    
                    // 주변 주차장 검색
                    searchNearbyParking(result[0].y, result[0].x);
                } else {
                    alert('주소를 찾을 수 없습니다.');
                }
            });
        }
        
        // 주변 주차장 검색 (API 호출)
        function searchNearbyParking(lat, lng) {
            // TODO: 실제 API 연동 구현
            // 임시 데이터 사용
            var testData = [
                {
                    id: "P0001",
                    name: "시청 공영주차장",
                    address: "서울시 중구 세종대로 110",
                    latitude: lat + 0.001,
                    longitude: lng + 0.001,
                    capacity: 200,
                    available: 50,
                    isPublic: true,
                    isFree: false,
                    fee: "시간당 1,000원"
                },
                {
                    id: "P0002",
                    name: "광화문 지하주차장",
                    address: "서울시 종로구 세종로 172",
                    latitude: lat - 0.001,
                    longitude: lng + 0.0005,
                    capacity: 300,
                    available: 120,
                    isPublic: true,
                    isFree: false,
                    fee: "시간당 2,000원"
                },
                {
                    id: "P0003",
                    name: "명동 민영주차장",
                    address: "서울시 중구 명동길 74",
                    latitude: lat - 0.0005,
                    longitude: lng - 0.001,
                    capacity: 100,
                    available: 10,
                    isPublic: false,
                    isFree: false,
                    fee: "시간당 3,000원"
                },
                {
                    id: "P0004",
                    name: "종로 무료주차장",
                    address: "서울시 종로구 종로 123",
                    latitude: lat + 0.0007,
                    longitude: lng - 0.0007,
                    capacity: 50,
                    available: 5,
                    isPublic: true,
                    isFree: true,
                    fee: "무료"
                }
            ];
            
            // 마커 초기화
            clearMarkers();
            
            // 주차장 목록 저장
            parkingInfos = testData;
            
            // 마커 및 목록 표시
            updateParkingList();
        }
        
        // 마커 초기화
        function clearMarkers() {
            for (var i = 0; i < markers.length; i++) {
                markers[i].setMap(null);
            }
            markers = [];
        }
        
        // 주차장 목록 및 마커 업데이트
        function updateParkingList() {
            var listContainer = document.getElementById('parkingList');
            var showPublic = document.getElementById('showPublic').checked;
            var showPrivate = document.getElementById('showPrivate').checked;
            var showFree = document.getElementById('showFree').checked;
            var showPaid = document.getElementById('showPaid').checked;
            
            // 목록 초기화
            listContainer.innerHTML = '';
            
            // 필터링된 주차장 표시
            var filteredParking = parkingInfos.filter(function(parking) {
                if (!showPublic && parking.isPublic) return false;
                if (!showPrivate && !parking.isPublic) return false;
                if (!showFree && parking.isFree) return false;
                if (!showPaid && !parking.isFree) return false;
                return true;
            });
            
            if (filteredParking.length === 0) {
                listContainer.innerHTML = '<div class="p-3 text-center text-muted">조건에 맞는 주차장이 없습니다.</div>';
                return;
            }
            
            filteredParking.forEach(function(parking) {
                // 마커 생성
                var markerPosition = new kakao.maps.LatLng(parking.latitude, parking.longitude);
                var marker = new kakao.maps.Marker({
                    position: markerPosition,
                    map: map
                });
                
                markers.push(marker);
                
                // 인포윈도우 생성
                var infoContent = '<div class="parking-info-window">' +
                    '<h5>' + parking.name + '</h5>' +
                    '<p>' + parking.address + '</p>' +
                    '<p>주차 가능: ' + parking.available + '/' + parking.capacity + '대</p>' +
                    '<p>요금: ' + parking.fee + '</p>' +
                    '<p><a href="/parking/' + parking.id + '" class="btn btn-sm btn-primary">상세 정보</a></p>' +
                    '</div>';
                
                var infoWindow = new kakao.maps.InfoWindow({
                    content: infoContent
                });
                
                // 마커 클릭 시 인포윈도우 표시
                kakao.maps.event.addListener(marker, 'click', function() {
                    infoWindow.open(map, marker);
                });
                
                // 목록에 추가
                var listItem = document.createElement('div');
                listItem.className = 'parking-item';
                listItem.innerHTML = 
                    '<h6>' + parking.name + '</h6>' +
                    '<p class="mb-1">주차 가능: ' + parking.available + '/' + parking.capacity + '대</p>' +
                    '<p>' + parking.fee + ' | ' + (parking.isPublic ? '공영' : '민영') + '</p>';
                
                // 목록 아이템 클릭 시 해당 마커로 이동
                listItem.addEventListener('click', function() {
                    map.setCenter(markerPosition);
                    infoWindow.open(map, marker);
                });
                
                listContainer.appendChild(listItem);
            });
        }
        
        // 필터 변경 시 목록 업데이트
        document.getElementById('showPublic').addEventListener('change', updateParkingList);
        document.getElementById('showPrivate').addEventListener('change', updateParkingList);
        document.getElementById('showFree').addEventListener('change', updateParkingList);
        document.getElementById('showPaid').addEventListener('change', updateParkingList);
        
        // 지도 초기화
        window.onload = initMap;
    </script>
</body>
</html> 