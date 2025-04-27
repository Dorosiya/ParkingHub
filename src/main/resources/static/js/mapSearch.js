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
  console.log("지도 초기화 시작");
  try {
    // 카카오맵 API가 로드되었는지 확인
    if (typeof kakao === 'undefined' || !kakao.maps) {
      console.error("Kakao Maps API is not available");
      return;
    }

    // 서울 시청을 기본 중심점으로 설정
    var defaultCenter = new kakao.maps.LatLng(37.5666805, 126.9784147);
    var container = document.getElementById("map");
    
    if (!container) {
      console.error("Map container not found");
      return;
    }
    
    var options = {
      center: defaultCenter,
      level: 3,
    };

    map = new kakao.maps.Map(container, options);
    console.log("지도 객체 생성 완료");

    // 지도 로드 완료 이벤트
    kakao.maps.event.addListener(map, "tilesloaded", function () {
      // 초기 로드 완료 후 현재 위치 버튼 클릭 이벤트 발생
      if (!window.mapInitialized) {
        window.mapInitialized = true;
        console.log("지도 타일 로드 완료, 초기 데이터 로드");
        // 초기 데이터 로드
        searchNearbyParking(37.5666805, 126.9784147); // 서울 시청 주변으로 초기화
      }
    });

    // 지도가 확대 또는 축소될 때도 다시 검색하지 않도록 수정
    // 지도 확대/축소 완료 이벤트는 제거하고 드래그 이벤트만 유지
    kakao.maps.event.addListener(map, "dragend", function () {
      var center = map.getCenter();
      searchNearbyParking(center.getLat(), center.getLng());
    });
    
    console.log("지도 초기화 완료");
  } catch (error) {
    console.error("지도 초기화 오류:", error);
  }
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
      console.log("주변 주차장 데이터 받음:", response);
      
      // 응답이 배열인지 확인
      if (!Array.isArray(response)) {
        console.error("API 응답이 배열이 아닙니다:", response);
        document.getElementById("parkingList").innerHTML = "<p class='p-3 text-danger'>주차장 정보 형식이 올바르지 않습니다.</p>";
        return;
      }
      
      if (response.length === 0) {
        console.log("주변에 주차장이 없습니다.");
        document.getElementById("parkingList").innerHTML = "<p class='p-3'>주변에 주차장이 없습니다. 다른 위치를 검색해보세요.</p>";
        return;
      }
      
      // 필드명 매핑 확인 및 데이터 보정
      parkingData = response.map(function(item) {
        // 필드가 null인 경우 기본값 설정
        if (!item) return null;
        
        return {
          prkCenterId: item.prkCenterId || "",
          prkPlceNm: item.prkPlceNm || "이름 없음",
          prkPlceAdres: item.prkPlceAdres || "주소 정보 없음",
          prkCmprtCo: item.prkCmprtCo || 0,
          latitude: parseFloat(item.prkPlceEntrcLa) || 0,
          longitude: parseFloat(item.prkPlceEntrcLo) || 0,
          parkingFee: 1 // 기본값은 유료로 설정 (정보 없음)
        };
      }).filter(item => item !== null); // null 항목 제거
      
      // 유효한 데이터가 있는지 다시 확인
      if (parkingData.length === 0) {
        document.getElementById("parkingList").innerHTML = "<p class='p-3'>유효한 주차장 정보가 없습니다. 다른 위치를 검색해보세요.</p>";
        return;
      }

      // 마커 초기화
      clearAllMarkers();

      // 마커 및 정보창 생성
      createMarkersAndInfowindows(parkingData);

      // 목록 업데이트
      updateParkingList();
    },
    error: function (xhr, status, error) {
      console.error("API 호출 중 오류:", error);
      document.getElementById("parkingList").innerHTML = "<p class='p-3 text-danger'>주차장 정보를 불러오는데 실패했습니다.</p>";
    }
  });
}

// 마커 초기화
function clearAllMarkers() {
  // 기존 마커와 인포윈도우 제거
  for (var i = 0; i < markers.length; i++) {
    markers[i].setMap(null);
    if (infowindows[i]) {
      infowindows[i].close();
    }
  }
  // 배열 초기화
  markers = [];
  infowindows = [];
}

// 마커 및 정보창 생성
function createMarkersAndInfowindows(data) {
  // 데이터가 배열이 아니면 처리하지 않음
  if (!Array.isArray(data)) {
    console.error("마커 생성 실패: 데이터가 배열이 아닙니다", data);
    return;
  }
  
  console.log("마커 생성 시작, 데이터 개수:", data.length);
  
  data.forEach(function (parking) {
    try {
      // 필수 데이터 검증
      if (!parking || !parking.latitude || !parking.longitude) {
        console.warn("위치 정보가 없는 주차장이 있습니다:", parking);
        return;
      }
      
      var position = new kakao.maps.LatLng(parking.latitude, parking.longitude);
      
      // 마커 생성
      var marker = new kakao.maps.Marker({
        position: position,
        map: map,
      });
      
      markers.push(marker);
      
      // 주차장 정보 필드 검증 및 기본값 설정
      var parkingName = parking.prkPlceNm || "이름 없음";
      var parkingAddr = parking.prkPlceAdres || "주소 정보 없음";
      var parkingSpaces = parking.prkCmprtCo || "정보 없음";
      var parkingId = parking.prkCenterId || "";
      
      // 정보창 콘텐츠 - 스타일 개선
      var iwContent = '<div class="parking-info-window" style="padding: 15px; max-width: 250px; overflow: hidden;">' +
        '<h5 style="margin-bottom: 10px; color: #333; font-weight: bold;">' + parkingName + '</h5>' +
        '<p style="margin-bottom: 8px; font-size: 13px; color: #666;"><i class="bi bi-geo-alt-fill"></i> ' + parkingAddr + '</p>' +
        '<p style="margin-bottom: 12px; font-size: 13px; color: #666;"><i class="bi bi-p-circle"></i> 주차 공간: ' + parkingSpaces + '대</p>';
      
      // ID가 있는 경우만 상세 정보 링크 추가
      if (parkingId) {
        iwContent += '<div style="text-align: right;"><a href="/parking/' + parkingId + '" class="btn btn-sm btn-primary" style="font-size: 12px; padding: 5px 10px;">상세 정보</a></div>';
      }
      
      iwContent += '</div>';
      
      // 정보창 생성
      var infowindow = new kakao.maps.InfoWindow({
        content: iwContent,
        removable: true
      });
      
      infowindows.push(infowindow);
      
      // 마커 클릭 이벤트
      kakao.maps.event.addListener(marker, 'click', function() {
        // 다른 모든 정보창 닫기
        closeAllInfoWindows();
        
        // 현재 정보창 열기
        infowindow.open(map, marker);
      });
    } catch (error) {
      console.error("마커 생성 중 오류:", error, parking);
    }
  });
  
  console.log("마커 생성 완료, 총 마커 수:", markers.length);
}

// 모든 인포윈도우 닫기
function closeAllInfoWindows() {
  for (var i = 0; i < infowindows.length; i++) {
    infowindows[i].close();
  }
}

// 필터링된 주차장 목록 업데이트
function updateParkingList() {
  // 체크된 필터 가져오기
  var showPublic = document.getElementById("showPublic").checked;
  var showPrivate = document.getElementById("showPrivate").checked;
  var showFree = document.getElementById("showFree").checked;
  var showPaid = document.getElementById("showPaid").checked;
  
  // 필터링 - API에서 유형 정보를 제공하지 않으므로 유형 필터링은 제거하고 요금만 필터링
  var filteredData = parkingData.filter(function(parking) {
    var feeCondition = true;
    
    // 요금 필터
    if (showFree && !showPaid) {
      feeCondition = parking.parkingFee === 0;
    } else if (!showFree && showPaid) {
      feeCondition = parking.parkingFee > 0;
    } else if (!showFree && !showPaid) {
      feeCondition = false;
    }
    
    return feeCondition;
  });
  
  // 마커 필터링
  for (var i = 0; i < markers.length; i++) {
    var found = false;
    
    for (var j = 0; j < filteredData.length; j++) {
      if (
        Math.abs(markers[i].getPosition().getLat() - filteredData[j].latitude) < 0.00001 &&
        Math.abs(markers[i].getPosition().getLng() - filteredData[j].longitude) < 0.00001
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
    // 각 주차장 항목을 data-lat, data-lng 속성을 사용하여 생성
    listHTML += '<div class="parking-item" data-lat="' + parking.latitude + '" data-lng="' + parking.longitude + '">' +
      '<h6>' + (parking.prkPlceNm || "이름 없음") + '</h6>' +
      '<p>' + (parking.prkPlceAdres || "주소 정보 없음") + '</p>' +
      '<div class="d-flex justify-content-between">' +
      // 주차장 유형 정보 제거 (API에서 제공하지 않음)
      '<span>' + (parking.parkingFee === 0 ? '무료' : '유료') + '</span>' +
      '</div>' +
      '</div>';
  });
  
  parkingListElement.innerHTML = listHTML;
  
  // 목록 아이템 클릭 이벤트 추가
  var parkingItems = document.querySelectorAll('.parking-item');
  parkingItems.forEach(function(item) {
    item.addEventListener('click', function() {
      var lat = parseFloat(this.getAttribute('data-lat'));
      var lng = parseFloat(this.getAttribute('data-lng'));
      
      if (isNaN(lat) || isNaN(lng)) {
        console.error('유효하지 않은 위치 정보:', lat, lng);
        return;
      }
      
      selectParking(lat, lng);
    });
  });
}

// 주차장 선택 함수
function selectParking(lat, lng) {
  if (!lat || !lng || isNaN(lat) || isNaN(lng)) {
    console.error('유효하지 않은 위치 정보:', lat, lng);
    return;
  }
  
  // 해당 위치로 지도 이동
  var position = new kakao.maps.LatLng(lat, lng);
  map.setCenter(position);
  
  // 해당 마커의 정보창 열기
  var markerFound = false;
  for (var i = 0; i < markers.length; i++) {
    var markerPosition = markers[i].getPosition();
    
    // 위치 비교 시 소수점 오차를 고려하여 근사값 비교
    if (Math.abs(markerPosition.getLat() - lat) < 0.00001 && 
        Math.abs(markerPosition.getLng() - lng) < 0.00001) {
      // 모든 인포윈도우 닫기
      closeAllInfoWindows();
      
      // 해당 마커의 인포윈도우 열기
      infowindows[i].open(map, markers[i]);
      markerFound = true;
      break;
    }
  }
  
  if (!markerFound) {
    console.warn('선택한 위치에 해당하는 마커를 찾을 수 없습니다:', lat, lng);
  }
}

// 지도 초기화
window.onload = initMap; 