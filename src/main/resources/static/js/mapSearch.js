// 전역 변수
let map;
let markers = [];
let infowindows = [];
let parkingData = [];

// DOM이 로드된 후 실행
document.addEventListener("DOMContentLoaded", function () {
  // 서버 측 인증 상태 체크 (JSP 표현식을 문자열로 변환하기 위해 서버에서 제공한 값 사용)
  var isServerAuthenticated = document.getElementById("isAuthenticated") 
    ? document.getElementById("isAuthenticated").value === "true" 
    : false;

  // 서버 인증이 되지 않았지만 클라이언트에 로그인 쿠키가 있는 경우에만 처리
  var isClientLoggedIn = document.cookie
    .split(";")
    .some((item) => item.trim().startsWith("logged_in="));

  // 이미 서버에서 인증된 경우
  if (isServerAuthenticated) {
    // 서버가 제공한 정보로 UI 이미 업데이트되어 있음
    // 아무 작업 필요 없음
  } else if (isClientLoggedIn) {
    // 클라이언트 측 쿠키로 로그인된 경우
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
        },
      })
        .done(function (user) {
          // 사용자 정보로 UI 업데이트
          var userHtml =
            '<div class="d-flex logged-in-menu">' +
            '<span class="navbar-text me-3">' +
            '<i class="bi bi-person-circle"></i> ' +
            user.username +
            "님" +
            "</span>" +
            '<button id="logoutBtn" class="btn btn-outline-light btn-sm">로그아웃</button>' +
            "</div>";

          $("#navbarNav .navbar-nav").after(userHtml);

          // 로그아웃 버튼 이벤트
          $("#logoutBtn").click(function () {
            $.ajax({
              url: "/api/auth/logout",
              type: "POST",
              xhrFields: {
                withCredentials: true,
              },
            }).always(function () {
              window.location.reload();
            });
          });
        })
        .fail(function () {
          // 토큰이 유효하지 않은 경우 쿠키 삭제
          document.cookie =
            "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
        });
    }
  }

  // 검색 버튼 이벤트 리스너
  document
    .getElementById("searchBtn")
    .addEventListener("click", searchAddressToCoordinate);

  // 엔터키 이벤트 리스너
  document
    .getElementById("searchAddress")
    .addEventListener("keyup", function (event) {
      if (event.key === "Enter") {
        searchAddressToCoordinate();
      }
    });

  // 현재 위치 버튼 이벤트 리스너
  document
    .getElementById("currentLocationBtn")
    .addEventListener("click", function () {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          function (position) {
            var lat = position.coords.latitude;
            var lng = position.coords.longitude;
            var currentPosition = new kakao.maps.LatLng(lat, lng);

            // 지도 중심 이동
            map.setCenter(currentPosition);

            // 현재 위치 마커 표시
            var currentMarker = new kakao.maps.Marker({
              position: currentPosition,
              map: map,
              image: new kakao.maps.MarkerImage(
                "https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png",
                new kakao.maps.Size(24, 35)
              ),
            });

            // 주변 주차장 검색
            searchNearbyParking(lat, lng);
          },
          function (error) {
            console.error("위치 정보를 가져오는데 실패했습니다.", error);
            alert(
              "위치 정보를 가져오는데 실패했습니다. 권한을 확인해주세요."
            );
          }
        );
      } else {
        alert("현재 위치를 가져올 수 없습니다.");
      }
    });

  // 주차장 타입 체크박스 이벤트 리스너
  document
    .getElementById("showPublic")
    .addEventListener("change", updateParkingList);
  document
    .getElementById("showPrivate")
    .addEventListener("change", updateParkingList);
  document
    .getElementById("showFree")
    .addEventListener("change", updateParkingList);
  document
    .getElementById("showPaid")
    .addEventListener("change", updateParkingList);
});

// 지도 초기화
function initMap() {
  // 서울 시청을 기본 중심점으로 설정
  var defaultCenter = new kakao.maps.LatLng(37.5666805, 126.9784147);
  var container = document.getElementById("map");
  var options = {
    center: defaultCenter,
    level: 3,
  };

  map = new kakao.maps.Map(container, options);

  // 지도 로드 완료 이벤트
  kakao.maps.event.addListener(map, "tilesloaded", function () {
    // 초기 로드 완료 후 현재 위치 버튼 클릭 이벤트 발생
    if (!window.mapInitialized) {
      window.mapInitialized = true;
      // 초기 데이터 로드
      searchNearbyParking(37.5666805, 126.9784147); // 서울 시청 주변으로 초기화
    }
  });

  // 지도 드래그 완료 이벤트
  kakao.maps.event.addListener(map, "dragend", function () {
    var center = map.getCenter();
    searchNearbyParking(center.getLat(), center.getLng());
  });
}

// 주소/키워드 검색
function searchAddressToCoordinate() {
  var keyword = document.getElementById("searchAddress").value;

  if (!keyword) {
    alert("검색할 주소나 키워드를 입력해주세요.");
    return;
  }

  var geocoder = new kakao.maps.services.Geocoder();
  var places = new kakao.maps.services.Places();

  // 먼저 키워드로 검색 시도
  places.keywordSearch(keyword, function (result, status) {
    if (status === kakao.maps.services.Status.OK && result.length > 0) {
      // 키워드 검색 성공
      var place = result[0]; // 첫 번째 결과 사용
      var coords = new kakao.maps.LatLng(place.y, place.x);

      // 지도 중심 이동
      map.setCenter(coords);

      // 주변 주차장 검색
      searchNearbyParking(place.y, place.x);
    } else {
      // 키워드 검색 실패 시 주소 검색 시도
      geocoder.addressSearch(keyword, function (result, status) {
        if (
          status === kakao.maps.services.Status.OK &&
          result.length > 0
        ) {
          // 주소 검색 성공
          var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

          // 지도 중심 이동
          map.setCenter(coords);

          // 주변 주차장 검색
          searchNearbyParking(result[0].y, result[0].x);
        } else {
          alert(
            "검색 결과가 없습니다. 다른 키워드나 주소를 입력해보세요."
          );
        }
      });
    }
  });
}

// 주변 주차장 검색 (API 호출)
function searchNearbyParking(lat, lng) {
  // 로딩 표시
  document.getElementById("parkingList").innerHTML = "<p class='p-3'>주변 주차장을 검색 중입니다...</p>";

  // API 호출
  $.ajax({
    url: "/api/parking/nearby",
    data: {
      lat: lat,
      lng: lng,
      radius: 1000 // 1km 반경
    },
    method: "GET",
    success: function (response) {
      // 데이터 저장
      parkingData = response;

      // 마커 초기화
      clearAllMarkers();

      // 마커 및 정보창 생성
      createMarkersAndInfowindows(response);

      // 목록 업데이트
      updateParkingList();
    },
    error: function (xhr, status, error) {
      console.error("API 호출 중 오류:", error);
      document.getElementById("parkingList").innerHTML = "<p class='p-3 text-danger'>주차장 정보를 불러오는데 실패했습니다.</p>";
    }
  });
}

// 모든 마커 초기화
function clearAllMarkers() {
  for (var i = 0; i < markers.length; i++) {
    markers[i].setMap(null);
  }
  markers = [];

  for (var i = 0; i < infowindows.length; i++) {
    infowindows[i].close();
  }
  infowindows = [];
}

// 마커 및 정보창 생성
function createMarkersAndInfowindows(data) {
  data.forEach(function (parking) {
    var position = new kakao.maps.LatLng(parking.latitude, parking.longitude);
    
    // 마커 생성
    var marker = new kakao.maps.Marker({
      position: position,
      map: map,
    });
    
    markers.push(marker);
    
    // 정보창 콘텐츠
    var iwContent = '<div class="parking-info-window">' +
      '<h5>' + parking.prkPlaceNm + '</h5>' +
      '<p>' + parking.prkPlceAdres + '</p>' +
      '<p>주차 공간: ' + (parking.prkCmprt || '정보 없음') + '</p>' +
      '<div class="mt-2"><a href="/parking/' + parking.prkPlaceNo + '" class="btn btn-sm btn-primary">상세 정보</a></div>' +
      '</div>';
    
    // 정보창 생성
    var infowindow = new kakao.maps.InfoWindow({
      content: iwContent,
      removable: true
    });
    
    infowindows.push(infowindow);
    
    // 마커 클릭 이벤트
    kakao.maps.event.addListener(marker, 'click', function() {
      // 다른 모든 정보창 닫기
      for (var i = 0; i < infowindows.length; i++) {
        infowindows[i].close();
      }
      
      // 현재 정보창 열기
      infowindow.open(map, marker);
    });
  });
}

// 필터링된 주차장 목록 업데이트
function updateParkingList() {
  // 체크된 필터 가져오기
  var showPublic = document.getElementById("showPublic").checked;
  var showPrivate = document.getElementById("showPrivate").checked;
  var showFree = document.getElementById("showFree").checked;
  var showPaid = document.getElementById("showPaid").checked;
  
  // 필터링
  var filteredData = parkingData.filter(function(parking) {
    var typeCondition = true;
    var feeCondition = true;
    
    // 주차장 유형 필터
    if (showPublic && !showPrivate) {
      typeCondition = parking.prkPlaceType === '공영';
    } else if (!showPublic && showPrivate) {
      typeCondition = parking.prkPlaceType === '민영';
    } else if (!showPublic && !showPrivate) {
      typeCondition = false;
    }
    
    // 요금 필터
    if (showFree && !showPaid) {
      feeCondition = parking.parkingFee === 0 || parking.parkingFee === '0';
    } else if (!showFree && showPaid) {
      feeCondition = parking.parkingFee > 0;
    } else if (!showFree && !showPaid) {
      feeCondition = false;
    }
    
    return typeCondition && feeCondition;
  });
  
  // 마커 필터링
  for (var i = 0; i < markers.length; i++) {
    var found = false;
    
    for (var j = 0; j < filteredData.length; j++) {
      if (
        markers[i].getPosition().getLat() === filteredData[j].latitude &&
        markers[i].getPosition().getLng() === filteredData[j].longitude
      ) {
        found = true;
        break;
      }
    }
    
    markers[i].setVisible(found);
  }
  
  // 목록 업데이트
  var parkingListElement = document.getElementById("parkingList");
  
  if (filteredData.length === 0) {
    parkingListElement.innerHTML = "<p class='p-3'>검색 결과가 없습니다.</p>";
    return;
  }
  
  var listHTML = "";
  
  filteredData.forEach(function(parking) {
    listHTML += '<div class="parking-item" onclick="selectParking(' + parking.latitude + ', ' + parking.longitude + ')">' +
      '<h6>' + parking.prkPlaceNm + '</h6>' +
      '<p>' + parking.prkPlceAdres + '</p>' +
      '<div class="d-flex justify-content-between">' +
      '<span>' + (parking.prkPlaceType || '정보 없음') + '</span>' +
      '<span>' + (parking.parkingFee === 0 ? '무료' : '유료') + '</span>' +
      '</div>' +
      '</div>';
  });
  
  parkingListElement.innerHTML = listHTML;
}

// 주차장 선택 함수
function selectParking(lat, lng) {
  // 해당 위치로 지도 이동
  var position = new kakao.maps.LatLng(lat, lng);
  map.setCenter(position);
  
  // 해당 마커의 정보창 열기
  for (var i = 0; i < markers.length; i++) {
    var markerPosition = markers[i].getPosition();
    
    if (markerPosition.getLat() === lat && markerPosition.getLng() === lng) {
      kakao.maps.event.trigger(markers[i], 'click');
      break;
    }
  }
}

// 지도 초기화
window.onload = initMap; 