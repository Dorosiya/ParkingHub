// Kakao Map 관련 코드
let map;
let marker;

// DOM이 로드된 후 실행
document.addEventListener('DOMContentLoaded', function() {
    // 인증 상태 확인
    const isServerAuthenticated = document.getElementById('isAuthenticated') ? 
        document.getElementById('isAuthenticated').value === 'true' : false;
    
    const isClientLoggedIn = document.cookie.split(';').some(cookie => 
        cookie.trim().startsWith('logged_in=true'));
    
    // 지도 초기화
    initMap();
    
    // 인증 상태에 따른 UI 업데이트
    updateAuthUI(isServerAuthenticated, isClientLoggedIn);
    
    // 즐겨찾기 버튼 이벤트 리스너
    setupFavoriteButton();
});

// Kakao 지도 초기화
function initMap() {
    const mapContainer = document.getElementById('map');
    if (!mapContainer) return;
    
    const latitude = parseFloat(document.getElementById('latitude').value);
    const longitude = parseFloat(document.getElementById('longitude').value);
    
    if (isNaN(latitude) || isNaN(longitude)) return;
    
    const options = {
        center: new kakao.maps.LatLng(latitude, longitude),
        level: 3
    };
    
    map = new kakao.maps.Map(mapContainer, options);
    
    // 마커 생성
    marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(latitude, longitude),
        map: map
    });
    
    // 지도 크기 조정
    setTimeout(function() {
        map.relayout();
    }, 200);
}

// 인증 상태에 따른 UI 업데이트
function updateAuthUI(isServerAuthenticated, isClientLoggedIn) {
    // 이미 서버에서 인증된 경우
    if (isServerAuthenticated) {
        // 서버가 제공한 정보로 UI 이미 업데이트되어 있음
        // 아무 작업 필요 없음
    }
    // 클라이언트 측 쿠키로 로그인된 경우
    else if (isClientLoggedIn) {
        // 로그인/회원가입 버튼 숨기기
        $("#navbarNav").find(".d-flex:not(.logged-in-menu)").hide();

        // 이미 로그인 상태 UI가 있는지 확인
        if ($(".logged-in-menu").length === 0) {
            // 서버에 현재 사용자 정보 요청
            $.ajax({
                url: "/api/user/current",
                type: "GET",
                xhrFields: {
                    withCredentials: true,
                }
            })
            .done(function(user) {
                // 사용자 정보로 UI 업데이트
                var userHtml =
                    '<div class="d-flex logged-in-menu">' +
                    '<span class="navbar-text me-3">' +
                    '<i class="bi bi-person-circle"></i> ' +
                    user.username +
                    "님" +
                    "</span>" +
                    '<button id="logoutBtn" class="btn btn-outline-light btn-sm d-flex align-items-center justify-content-center" style="min-height: 31px;">로그아웃</button>' +
                    "</div>";

                $("#navbarNav .navbar-nav").after(userHtml);

                // 로그아웃 버튼 이벤트
                $("#logoutBtn").click(function() {
                    $.ajax({
                        url: "/api/auth/logout",
                        type: "POST",
                        xhrFields: {
                            withCredentials: true,
                        }
                    }).always(function() {
                        window.location.reload();
                    });
                });
            })
            .fail(function() {
                // 토큰이 유효하지 않은 경우 쿠키 삭제
                document.cookie =
                    "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
            });
        }
    }
}

// 즐겨찾기 버튼 설정
function setupFavoriteButton() {
    const favoriteBtn = document.getElementById('favoriteBtn');
    if (favoriteBtn) {
        favoriteBtn.addEventListener('click', function() {
            const parkingId = this.getAttribute('data-parking-id');
            const isFavorite = this.classList.contains('btn-favorite-active');
            
            // 즐겨찾기 상태 토글
            $.ajax({
                url: `/api/favorites/${isFavorite ? 'remove' : 'add'}/${parkingId}`,
                type: "POST",
                xhrFields: {
                    withCredentials: true,
                }
            })
            .done(function() {
                // UI 업데이트
                if (isFavorite) {
                    favoriteBtn.classList.remove('btn-favorite-active');
                    favoriteBtn.innerHTML = '<i class="bi bi-star"></i> 즐겨찾기';
                } else {
                    favoriteBtn.classList.add('btn-favorite-active');
                    favoriteBtn.innerHTML = '<i class="bi bi-star-fill"></i> 즐겨찾기됨';
                }
            });
        });
    }
} 