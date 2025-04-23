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
    
    // 리뷰 목록 로드
    loadReviews();
    
    // 리뷰 체크 (이미 작성했는지)
    checkUserReview();
    
    // 리뷰 작성 이벤트 등록
    document.querySelector('#reviewModal .btn-primary').addEventListener('click', submitReview);
    
    // 주변 주차장 정보 로드
    loadNearbyParkings();
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
    
    // 인포윈도우 생성
    var iwContent = '<div style="padding:5px;">' + parkingName + '</div>';
    var infowindow = new kakao.maps.InfoWindow({
        content: iwContent
    });
    
    // 마커에 마우스오버 이벤트 등록
    kakao.maps.event.addListener(marker, 'mouseover', function() {
        infowindow.open(map, marker);
    });
    
    // 마커에 마우스아웃 이벤트 등록
    kakao.maps.event.addListener(marker, 'mouseout', function() {
        infowindow.close();
    });
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

// 주소 복사 기능
document.getElementById('copyAddressBtn').addEventListener('click', function() {
    var tempTextarea = document.createElement('textarea');
    tempTextarea.value = parkingAddress;
    document.body.appendChild(tempTextarea);
    tempTextarea.select();
    document.execCommand('copy');
    document.body.removeChild(tempTextarea);
    
    // 복사 알림
    alert('주소가 클립보드에 복사되었습니다.');
});

// 리뷰 관련 코드 =============================================

// 로그인 상태 확인
function isLoggedIn() {
    return isAuthenticated === 'true';
}

// 이미 리뷰를 작성했는지 확인
function checkUserReview() {
    if (!isLoggedIn()) return;
    
    fetch(`/api/reviews/check/${prkCenterId}`)
        .then(response => response.json())
        .then(data => {
            if (data.hasReviewed) {
                // 리뷰 작성 버튼 텍스트 변경
                const reviewBtn = document.querySelector('[data-bs-target="#reviewModal"]');
                if (reviewBtn) {
                    reviewBtn.innerHTML = '<i class="bi bi-pencil"></i> 리뷰 수정';
                }
            }
        })
        .catch(error => console.error('리뷰 체크 오류:', error));
}

// 리뷰 목록 로드
function loadReviews() {
    fetch(`/api/reviews/parking/${prkCenterId}`)
        .then(response => response.json())
        .then(data => {
            const reviewContainer = document.querySelector('.detail-card:nth-child(3)');
            const reviewsContent = reviewContainer.querySelector('p.text-muted.text-center.py-3');
            
            if (data.reviews && data.reviews.length > 0) {
                // 리뷰가 있을 경우
                displayReviews(reviewContainer, data.reviews, data.averageRating, data.reviewCount);
            } else {
                // 리뷰가 없을 경우 기본 메시지 유지
                reviewsContent.innerHTML = '<i class="bi bi-chat-dots"></i> 아직 작성된 리뷰가 없습니다.';
            }
        })
        .catch(error => console.error('리뷰 로드 오류:', error));
}

// 리뷰 목록 표시
function displayReviews(container, reviews, averageRating, reviewCount) {
    // 기존 메시지 제거
    const oldContent = container.querySelector('p.text-muted.text-center.py-3');
    if (oldContent) {
        oldContent.remove();
    }
    
    // 평균 평점 표시
    const ratingHtml = `
        <div class="mb-3 text-center">
            <div class="fs-1 text-warning">${averageRating.toFixed(1)}</div>
            <div class="text-muted small">평균 평점 (총 ${reviewCount}개)</div>
            <div class="mt-2">
                ${getStarRating(averageRating)}
            </div>
        </div>
    `;
    
    // 리뷰 목록 HTML 생성
    const reviewsHtml = `
        <div class="review-list mt-3">
            ${reviews.map(review => `
                <div class="review-item mb-3 p-3 border-bottom" data-review-id="${review.id}">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <div>
                            <span class="fw-bold">${review.username}</span>
                            <span class="ms-2 text-warning">${getStarRating(review.rating)}</span>
                        </div>
                        <div class="text-muted small">
                            ${formatDate(review.createdAt)}
                            ${isLoggedIn() && currentUserId === review.userId ? `
                                <div class="ms-2 d-inline-block">
                                    <button class="btn btn-sm btn-outline-secondary edit-review" title="수정">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <button class="btn btn-sm btn-outline-danger delete-review" title="삭제">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            ` : ''}
                        </div>
                    </div>
                    <div class="review-content">
                        ${review.content}
                    </div>
                </div>
            `).join('')}
        </div>
    `;
    
    // 컨테이너에 HTML 추가
    container.innerHTML += ratingHtml + reviewsHtml;
    
    // 이벤트 리스너 추가 (수정/삭제 버튼)
    setupReviewEventListeners();
}

// 리뷰 이벤트 리스너 설정 (수정/삭제 버튼)
function setupReviewEventListeners() {
    // 수정 버튼
    document.querySelectorAll('.edit-review').forEach(button => {
        button.addEventListener('click', function() {
            const reviewItem = this.closest('.review-item');
            const reviewId = reviewItem.dataset.reviewId;
            editReview(reviewId);
        });
    });
    
    // 삭제 버튼
    document.querySelectorAll('.delete-review').forEach(button => {
        button.addEventListener('click', function() {
            const reviewItem = this.closest('.review-item');
            const reviewId = reviewItem.dataset.reviewId;
            deleteReview(reviewId);
        });
    });
}

// 리뷰 작성/수정 제출
function submitReview() {
    if (!isLoggedIn()) {
        alert('로그인이 필요한 서비스입니다.');
        return;
    }
    
    const rating = document.getElementById('reviewRating').value;
    const content = document.getElementById('reviewContent').value;
    
    // 유효성 검사
    if (!rating || !content) {
        alert('평점과 내용을 모두 입력해주세요.');
        return;
    }
    
    if (content.length > 500) {
        alert('리뷰 내용은 500자 이내로 작성해주세요.');
        return;
    }
    
    const reviewData = {
        prkCenterId: prkCenterId,
        rating: parseInt(rating),
        content: content
    };
    
    // 리뷰 작성 API 호출
    fetch('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(reviewData)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || '리뷰 등록에 실패했습니다.'); });
        }
        return response.json();
    })
    .then(data => {
        // 모달 닫기
        const modalElement = document.getElementById('reviewModal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        modal.hide();
        
        // 폼 초기화
        document.getElementById('reviewRating').value = '5';
        document.getElementById('reviewContent').value = '';
        
        // 리뷰 목록 새로고침
        loadReviews();
        
        alert('리뷰가 등록되었습니다.');
    })
    .catch(error => {
        alert(error.message);
    });
}

// 리뷰 수정
function editReview(reviewId) {
    // 리뷰 데이터 가져오기
    fetch(`/api/reviews/${reviewId}`)
        .then(response => response.json())
        .then(review => {
            // 모달에 데이터 세팅
            document.getElementById('reviewRating').value = review.rating;
            document.getElementById('reviewContent').value = review.content;
            
            // 모달 타이틀 및 버튼 수정
            document.querySelector('#reviewModal .modal-title').textContent = '리뷰 수정';
            const submitButton = document.querySelector('#reviewModal .btn-primary');
            submitButton.textContent = '수정하기';
            
            // 기존 이벤트 리스너 제거
            const newSubmitButton = submitButton.cloneNode(true);
            submitButton.parentNode.replaceChild(newSubmitButton, submitButton);
            
            // 수정용 이벤트 리스너 추가
            newSubmitButton.addEventListener('click', function() {
                updateReview(reviewId);
            });
            
            // 모달 표시
            const modal = new bootstrap.Modal(document.getElementById('reviewModal'));
            modal.show();
        })
        .catch(error => console.error('리뷰 조회 오류:', error));
}

// 리뷰 업데이트 API 호출
function updateReview(reviewId) {
    const rating = document.getElementById('reviewRating').value;
    const content = document.getElementById('reviewContent').value;
    
    // 유효성 검사
    if (!rating || !content) {
        alert('평점과 내용을 모두 입력해주세요.');
        return;
    }
    
    if (content.length > 500) {
        alert('리뷰 내용은 500자 이내로 작성해주세요.');
        return;
    }
    
    const reviewData = {
        rating: parseInt(rating),
        content: content
    };
    
    // 리뷰 수정 API 호출
    fetch(`/api/reviews/${reviewId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(reviewData)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || '리뷰 수정에 실패했습니다.'); });
        }
        return response.json();
    })
    .then(data => {
        // 모달 닫기
        const modalElement = document.getElementById('reviewModal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        modal.hide();
        
        // 리뷰 목록 새로고침
        loadReviews();
        
        // 모달 초기화 (작성 모드로 되돌리기)
        resetReviewModal();
        
        alert('리뷰가 수정되었습니다.');
    })
    .catch(error => {
        alert(error.message);
    });
}

// 리뷰 삭제
function deleteReview(reviewId) {
    if (!confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
        return;
    }
    
    // 리뷰 삭제 API 호출
    fetch(`/api/reviews/${reviewId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.message || '리뷰 삭제에 실패했습니다.'); });
        }
        return response.json();
    })
    .then(data => {
        // 리뷰 목록 새로고침
        loadReviews();
        
        alert('리뷰가 삭제되었습니다.');
    })
    .catch(error => {
        alert(error.message);
    });
}

// 모달 초기화 (작성 모드로 설정)
function resetReviewModal() {
    document.querySelector('#reviewModal .modal-title').textContent = '리뷰 작성';
    const submitButton = document.querySelector('#reviewModal .btn-primary');
    submitButton.textContent = '등록하기';
    
    // 기존 이벤트 리스너 제거
    const newSubmitButton = submitButton.cloneNode(true);
    submitButton.parentNode.replaceChild(newSubmitButton, submitButton);
    
    // 등록용 이벤트 리스너 추가
    newSubmitButton.addEventListener('click', submitReview);
    
    // 폼 초기화
    document.getElementById('reviewRating').value = '5';
    document.getElementById('reviewContent').value = '';
}

// 모달 닫힐 때 초기화 이벤트
document.getElementById('reviewModal').addEventListener('hidden.bs.modal', function () {
    resetReviewModal();
});

// 별점 표시 생성
function getStarRating(rating) {
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5;
    const emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
    
    return `
        ${Array(fullStars).fill('<i class="bi bi-star-fill text-warning"></i>').join('')}
        ${halfStar ? '<i class="bi bi-star-half text-warning"></i>' : ''}
        ${Array(emptyStars).fill('<i class="bi bi-star text-warning"></i>').join('')}
    `;
}

// 날짜 포맷팅
function formatDate(dateStr) {
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    
    return `${year}.${month}.${day}`;
}

// 주변 주차장 관련 코드 =============================================

// 주변 주차장 정보 로드
function loadNearbyParkings() {
    // 현재 주차장 위치 정보 가져오기
    const latitude = parseFloat(document.getElementById('latitude').value);
    const longitude = parseFloat(document.getElementById('longitude').value);
    
    if (isNaN(latitude) || isNaN(longitude)) return;
    
    // API 호출 (2km 반경 내 주차장 검색)
    fetch(`/api/parking/search/location?lat=${latitude}&lng=${longitude}&radius=2`)
        .then(response => response.json())
        .then(data => {
            if (data && data.length > 0) {
                // 현재 주차장을 제외한 주변 주차장만 표시
                const nearbyParkings = data.filter(parking => parking.prkCenterId !== parkingId);
                
                // 최대 3개만 표시
                const limitedParkings = nearbyParkings.slice(0, 3);
                
                if (limitedParkings.length > 0) {
                    displayNearbyParkings(limitedParkings);
                } else {
                    showNoParkingsMessage();
                }
            } else {
                showNoParkingsMessage();
            }
        })
        .catch(error => {
            console.error('주변 주차장 정보 로드 오류:', error);
            showNoParkingsMessage();
        });
}

// 주변 주차장 정보 표시
function displayNearbyParkings(parkings) {
    const container = document.querySelector('.detail-card:nth-child(2)');
    if (!container) return;
    
    // 기존 로딩 메시지 제거
    const loadingMessage = container.querySelector('p.text-muted.text-center');
    if (loadingMessage) {
        loadingMessage.remove();
    }
    
    // 주변 주차장 목록 생성
    const parkingListHtml = document.createElement('div');
    parkingListHtml.className = 'nearby-parking-list';
    
    parkings.forEach(parking => {
        // 거리 계산 (미터 단위로 표시)
        const distance = calculateDistance(
            parseFloat(document.getElementById('latitude').value),
            parseFloat(document.getElementById('longitude').value),
            parking.prkPlceEntrcLa,
            parking.prkPlceEntrcLo
        );
        
        const parkingItem = document.createElement('div');
        parkingItem.className = 'nearby-parking-item p-3 border-bottom';
        
        // 주차 상태 클래스 (실시간 정보가 없으면 '정보 없음')
        const statusClass = 
            parking.realTimeInfo ? 
            (parking.realTimeInfo.nrmlparkingcnt > 10 ? 'text-success' : 
             (parking.realTimeInfo.nrmlparkingcnt > 0 ? 'text-warning' : 'text-danger')) : 
            'text-secondary';
        
        // 주차 상태 텍스트
        const statusText = 
            parking.realTimeInfo ? 
            (parking.realTimeInfo.nrmlparkingcnt > 10 ? '주차가능' : 
             (parking.realTimeInfo.nrmlparkingcnt > 0 ? '주차공간 부족' : '만차')) : 
            '정보 없음';
        
        parkingItem.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h6 class="mb-0">
                    <a href="/parking/detail/${parking.prkCenterId}" class="text-decoration-none text-dark">
                        ${parking.prkPlceNm}
                    </a>
                </h6>
                <span class="badge ${statusClass} rounded-pill">${statusText}</span>
            </div>
            <div class="text-muted small mb-2">
                <i class="bi bi-geo-alt"></i> ${parking.prkPlceAdres}
            </div>
            <div class="d-flex justify-content-between align-items-center">
                <div class="text-muted small">
                    <i class="bi bi-rulers"></i> ${formatDistance(distance)}
                </div>
                <a href="/parking/detail/${parking.prkCenterId}" class="btn btn-sm btn-outline-primary">
                    자세히 보기
                </a>
            </div>
        `;
        
        parkingListHtml.appendChild(parkingItem);
    });
    
    container.appendChild(parkingListHtml);
}

// 주변 주차장 정보가 없을 때 메시지 표시
function showNoParkingsMessage() {
    const container = document.querySelector('.detail-card:nth-child(2)');
    if (!container) return;
    
    // 기존 로딩 메시지 제거
    const loadingMessage = container.querySelector('p.text-muted.text-center');
    if (loadingMessage) {
        loadingMessage.innerHTML = `
            <i class="bi bi-exclamation-circle"></i>
            주변 2km 이내에 다른 주차장 정보가 없습니다.
        `;
    }
}

// 두 지점 간의 거리 계산 (Haversine 공식)
function calculateDistance(lat1, lon1, lat2, lon2) {
    const R = 6371000; // 지구 반지름 (미터)
    const dLat = degToRad(lat2 - lat1);
    const dLon = degToRad(lon2 - lon1);
    
    const a = 
        Math.sin(dLat/2) * Math.sin(dLat/2) +
        Math.cos(degToRad(lat1)) * Math.cos(degToRad(lat2)) * 
        Math.sin(dLon/2) * Math.sin(dLon/2);
    
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return R * c; // 미터 단위 거리
}

// 각도를 라디안으로 변환
function degToRad(deg) {
    return deg * (Math.PI/180);
}

// 거리 포맷팅
function formatDistance(meters) {
    if (meters < 1000) {
        return `약 ${Math.round(meters)}m`;
    } else {
        return `약 ${(meters/1000).toFixed(1)}km`;
    }
} 