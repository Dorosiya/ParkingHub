<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ taglib prefix="c"
uri="http://java.sun.com/jsp/jstl/core" %> <%@ taglib prefix="fmt"
uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>${parking.info.prkingnm} - Parking Hub</title>
    <link
      href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
      rel="stylesheet"
    />
    <link
      rel="stylesheet"
      href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css"
    />
    <script
      type="text/javascript"
      src="//dapi.kakao.com/v2/maps/sdk.js?appkey=7de22b580589f48eae26aed074b09419"
    ></script>
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

      .parking-header {
        background: linear-gradient(
          135deg,
          var(--primary-color),
          var(--secondary-color)
        );
        color: white;
        padding: 40px 0;
        margin-bottom: 30px;
      }

      .detail-card {
        background-color: white;
        border-radius: 10px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
        padding: 25px;
        margin-bottom: 30px;
      }

      .status-badge {
        font-size: 14px;
        padding: 6px 12px;
        border-radius: 50px;
      }

      .map-container {
        height: 350px;
        border-radius: 10px;
        overflow: hidden;
      }

      #map {
        width: 100%;
        height: 100%;
      }

      .info-list {
        margin-bottom: 0;
      }

      .info-list li {
        padding: 10px 0;
        border-bottom: 1px solid #eee;
      }

      .info-list li:last-child {
        border-bottom: none;
      }

      .info-icon {
        color: var(--primary-color);
        width: 24px;
        text-align: center;
        margin-right: 10px;
      }

      .footer {
        background-color: var(--dark-color);
        color: var(--light-color);
        padding: 50px 0 20px;
        margin-top: 50px;
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

      .action-buttons {
        position: fixed;
        bottom: 20px;
        right: 20px;
        z-index: 1000;
      }

      .action-button {
        width: 60px;
        height: 60px;
        border-radius: 50%;
        display: flex;
        justify-content: center;
        align-items: center;
        margin-top: 10px;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.2);
      }

      .action-button i {
        font-size: 24px;
      }
    </style>
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
              <a class="nav-link" href="/search">주차장 찾기</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="/mapSearch">지도로 찾기</a>
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

    <!-- 주차장 헤더 -->
    <header class="parking-header">
      <div class="container">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h2 mb-2">${parking.info.prkingnm}</h1>
            <p class="mb-0">
              <i class="bi bi-geo-alt"></i> ${parking.info.rdnmadr}
            </p>
          </div>
          <div>
            <c:choose>
              <c:when
                test="${parking.realtime != null && parking.realtime.nrmlparkingcnt > 10}"
              >
                <span class="badge bg-success status-badge">
                  <i class="bi bi-check-circle"></i> 주차가능
                </span>
              </c:when>
              <c:when
                test="${parking.realtime != null && parking.realtime.nrmlparkingcnt > 0}"
              >
                <span class="badge bg-warning status-badge">
                  <i class="bi bi-exclamation-circle"></i> 주차공간 부족
                </span>
              </c:when>
              <c:when
                test="${parking.realtime != null && parking.realtime.nrmlparkingcnt == 0}"
              >
                <span class="badge bg-danger status-badge">
                  <i class="bi bi-x-circle"></i> 만차
                </span>
              </c:when>
              <c:otherwise>
                <span class="badge bg-secondary status-badge">
                  <i class="bi bi-question-circle"></i> 정보 없음
                </span>
              </c:otherwise>
            </c:choose>
          </div>
        </div>
      </div>
    </header>

    <div class="container">
      <div class="row">
        <!-- 좌측 주차장 정보 -->
        <div class="col-lg-8">
          <!-- 기본 정보 -->
          <div class="detail-card">
            <h4 class="mb-4">주차장 정보</h4>
            <ul class="list-unstyled info-list">
              <li class="d-flex">
                <div class="info-icon"><i class="bi bi-p-circle-fill"></i></div>
                <div>
                  <strong>주차 공간:</strong>
                  <span>${parking.info.parkingcapacity}대</span>
                  <c:if test="${parking.realtime != null}">
                    <span class="ms-2 text-success">
                      (현재 ${parking.realtime.nrmlparkingcnt}대 가능)
                    </span>
                  </c:if>
                </div>
              </li>
              <li class="d-flex">
                <div class="info-icon"><i class="bi bi-cash"></i></div>
                <div>
                  <strong>주차 요금:</strong>
                  <span>${parking.info.parkingchrgeinfo}</span>
                </div>
              </li>
              <li class="d-flex">
                <div class="info-icon"><i class="bi bi-building"></i></div>
                <div>
                  <strong>주차장 유형:</strong>
                  <span
                    >${parking.info.opratorcontent == '공영' ? '공영주차장' :
                    '민영주차장'}</span
                  >
                </div>
              </li>
              <li class="d-flex">
                <div class="info-icon"><i class="bi bi-telephone"></i></div>
                <div>
                  <strong>연락처:</strong>
                  <span
                    >${empty parking.info.phonenumber ? '연락처 정보 없음' :
                    parking.info.phonenumber}</span
                  >
                </div>
              </li>
              <c:if test="${parking.operation != null}">
                <li class="d-flex">
                  <div class="info-icon"><i class="bi bi-clock"></i></div>
                  <div>
                    <strong>운영 시간:</strong>
                    <span>${parking.operation.operatingtime}</span>
                  </div>
                </li>
              </c:if>
            </ul>
          </div>

          <!-- 주차장 위치 맵 -->
          <div class="detail-card">
            <h4 class="mb-3">위치</h4>
            <div class="map-container" id="map"></div>
            <div class="mt-3">
              <button class="btn btn-outline-primary" id="copyAddressBtn">
                <i class="bi bi-clipboard"></i> 주소 복사
              </button>
              <a
                href="https://map.kakao.com/link/to/${parking.info.prkingnm},${latitude},${longitude}"
                target="_blank"
                class="btn btn-outline-success ms-2"
              >
                <i class="bi bi-signpost-2"></i> 길 찾기
              </a>
            </div>
          </div>

          <!-- 추가 정보 -->
          <c:if test="${parking.operation != null}">
            <div class="detail-card">
              <h4 class="mb-4">추가 정보</h4>
              <ul class="list-unstyled info-list">
                <c:if test="${not empty parking.operation.nightoperationinfo}">
                  <li class="d-flex">
                    <div class="info-icon"><i class="bi bi-moon"></i></div>
                    <div>
                      <strong>야간 운영:</strong>
                      <span>${parking.operation.nightoperationinfo}</span>
                    </div>
                  </li>
                </c:if>
                <c:if test="${not empty parking.operation.satoperationinfo}">
                  <li class="d-flex">
                    <div class="info-icon">
                      <i class="bi bi-calendar-check"></i>
                    </div>
                    <div>
                      <strong>토요일 운영:</strong>
                      <span>${parking.operation.satoperationinfo}</span>
                    </div>
                  </li>
                </c:if>
                <c:if
                  test="${not empty parking.operation.holidayoperationinfo}"
                >
                  <li class="d-flex">
                    <div class="info-icon">
                      <i class="bi bi-calendar2-heart"></i>
                    </div>
                    <div>
                      <strong>공휴일 운영:</strong>
                      <span>${parking.operation.holidayoperationinfo}</span>
                    </div>
                  </li>
                </c:if>
                <c:if test="${parking.operation.specialnote != null}">
                  <li class="d-flex">
                    <div class="info-icon">
                      <i class="bi bi-info-circle"></i>
                    </div>
                    <div>
                      <strong>특이사항:</strong>
                      <span>${parking.operation.specialnote}</span>
                    </div>
                  </li>
                </c:if>
                <c:if test="${parking.info.updatedate != null}">
                  <li class="d-flex">
                    <div class="info-icon">
                      <i class="bi bi-calendar-date"></i>
                    </div>
                    <div>
                      <strong>정보 업데이트:</strong>
                      <span>${parking.info.updatedate}</span>
                    </div>
                  </li>
                </c:if>
              </ul>
            </div>
          </c:if>
        </div>

        <!-- 우측 사이드바 -->
        <div class="col-lg-4">
          <!-- 실시간 정보 -->
          <div class="detail-card">
            <h4 class="mb-3">실시간 정보</h4>
            <c:choose>
              <c:when test="${parking.realtime != null}">
                <div class="text-center mb-4">
                  <div
                    class="display-4 mb-2 ${parking.realtime.nrmlparkingcnt > 10 ? 'text-success' : (parking.realtime.nrmlparkingcnt > 0 ? 'text-warning' : 'text-danger')}"
                  >
                    ${parking.realtime.nrmlparkingcnt}
                  </div>
                  <div class="text-muted">주차 가능 대수</div>
                  <small class="d-block mt-2 text-muted">
                    전체 ${parking.info.parkingcapacity}대 중
                  </small>
                </div>
                <c:set
                  var="occupancy"
                  value="${(parking.info.parkingcapacity - parking.realtime.nrmlparkingcnt) * 100 / parking.info.parkingcapacity}"
                />
                <div class="progress mb-3" style="height: 25px">
                  <div
                    class="progress-bar ${occupancy > 90 ? 'bg-danger' : (occupancy > 70 ? 'bg-warning' : 'bg-success')}"
                    role="progressbar"
                    style="width: ${occupancy}%;"
                    aria-valuenow="${occupancy}"
                    aria-valuemin="0"
                    aria-valuemax="100"
                  >
                    <fmt:formatNumber value="${occupancy}" pattern="#,##0" />%
                  </div>
                </div>
                <div class="text-muted text-center">
                  <small>마지막 업데이트: ${parking.realtime.updatedate}</small>
                </div>
              </c:when>
              <c:otherwise>
                <div class="text-center py-4">
                  <div class="text-muted">
                    <i class="bi bi-info-circle fs-1"></i>
                    <p class="mt-3">
                      실시간 정보가 제공되지 않는 주차장입니다.
                    </p>
                  </div>
                </div>
              </c:otherwise>
            </c:choose>
          </div>

          <!-- 주변 추천 주차장 (나중에 추가할 기능) -->
          <div class="detail-card">
            <h4 class="mb-3">주변 주차장</h4>
            <p class="text-muted text-center py-3">
              <i class="bi bi-geo-alt-fill"></i>
              주변 2km 이내 주차장 정보를 불러오는 중입니다...
            </p>
          </div>

          <!-- 리뷰 (나중에 추가할 기능) -->
          <div class="detail-card">
            <div class="d-flex justify-content-between align-items-center mb-3">
              <h4 class="mb-0">리뷰</h4>
              <button
                class="btn btn-sm btn-outline-primary"
                data-bs-toggle="modal"
                data-bs-target="#reviewModal"
              >
                <i class="bi bi-pencil"></i> 리뷰 작성
              </button>
            </div>
            <p class="text-muted text-center py-3">
              <i class="bi bi-chat-dots"></i>
              아직 작성된 리뷰가 없습니다.
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- 액션 버튼 -->
    <div class="action-buttons">
      <button
        class="btn btn-primary action-button"
        title="길 안내"
        onclick="window.open('https://map.kakao.com/link/to/${parking.info.prkingnm},${latitude},${longitude}', '_blank')"
      >
        <i class="bi bi-signpost-2"></i>
      </button>
      <c:if test="${not empty parking.info.phonenumber}">
        <a
          href="tel:${parking.info.phonenumber}"
          class="btn btn-success action-button"
          title="전화하기"
        >
          <i class="bi bi-telephone-fill"></i>
        </a>
      </c:if>
    </div>

    <!-- 푸터 -->
    <footer class="footer mt-auto">
      <div class="container">
        <div class="row">
          <div class="col-md-4">
            <h5>Parking Hub</h5>
            <p class="text-muted">더 쉽고 빠른 주차 경험을 제공하는 서비스</p>
          </div>
          <div class="col-md-4">
            <h5>바로가기</h5>
            <ul class="list-unstyled">
              <li>
                <a href="/" class="text-decoration-none footer-link">홈</a>
              </li>
              <li>
                <a href="/search" class="text-decoration-none footer-link"
                  >주차장 찾기</a
                >
              </li>
              <li>
                <a href="/dashboard" class="text-decoration-none footer-link"
                  >마이페이지</a
                >
              </li>
              <li>
                <a href="/community" class="text-decoration-none footer-link"
                  >커뮤니티</a
                >
              </li>
            </ul>
          </div>
          <div class="col-md-4">
            <h5>고객센터</h5>
            <p class="text-muted">문의: support@parkinghub.co.kr</p>
            <p class="text-muted">전화: 1234-5678 (평일 9:00-18:00)</p>
          </div>
        </div>
        <div class="text-center footer-bottom">
          <p>&copy; 2025 Parking Hub. All rights reserved.</p>
        </div>
      </div>
    </footer>

    <!-- 리뷰 작성 모달 -->
    <div
      class="modal fade"
      id="reviewModal"
      tabindex="-1"
      aria-labelledby="reviewModalLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="reviewModalLabel">리뷰 작성</h5>
            <button
              type="button"
              class="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label for="reviewRating" class="form-label">평점</label>
                <select class="form-select" id="reviewRating">
                  <option value="5">★★★★★ 아주 좋음</option>
                  <option value="4">★★★★☆ 좋음</option>
                  <option value="3">★★★☆☆ 보통</option>
                  <option value="2">★★☆☆☆ 별로</option>
                  <option value="1">★☆☆☆☆ 나쁨</option>
                </select>
              </div>
              <div class="mb-3">
                <label for="reviewContent" class="form-label">내용</label>
                <textarea
                  class="form-control"
                  id="reviewContent"
                  rows="4"
                  placeholder="주차장에 대한 경험을 공유해주세요."
                ></textarea>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button
              type="button"
              class="btn btn-secondary"
              data-bs-dismiss="modal"
            >
              취소
            </button>
            <button type="button" class="btn btn-primary">등록하기</button>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="/js/auth.js"></script>
    <script>
      // 지도 초기화
      window.onload = function () {
        // 지도 생성
        const container = document.getElementById("map");
        const options = {
          center: new kakao.maps.LatLng("${latitude}", "${longitude}"),
          level: 3,
        };

        const map = new kakao.maps.Map(container, options);

        // 마커 생성
        const marker = new kakao.maps.Marker({
          position: new kakao.maps.LatLng("${latitude}", "${longitude}"),
          map: map,
        });

        // 인포윈도우 생성
        const infowindow = new kakao.maps.InfoWindow({
          content:
            '<div style="padding:5px;font-size:12px;width:150px;text-align:center;">${parking.info.prkingnm}</div>',
        });

        infowindow.open(map, marker);

        // 주소 복사 버튼
        document
          .getElementById("copyAddressBtn")
          .addEventListener("click", function () {
            const address = "${parking.info.rdnmadr}";

            // 클립보드에 복사
            navigator.clipboard
              .writeText(address)
              .then(() => {
                alert("주소가 클립보드에 복사되었습니다.");
              })
              .catch((err) => {
                console.error("주소 복사 실패:", err);
                alert("주소 복사에 실패했습니다.");
              });
          });
      };

      // 로그인 상태 처리
      $(document).ready(function () {
        // 서버 측 인증 상태 체크 (JSP 표현식을 문자열로 변환)
        var isServerAuthenticated =
          "<c:out value='${isAuthenticated}'/>" === "true";

        // 서버 인증이 되지 않았지만 클라이언트에 로그인 쿠키가 있는 경우에만 처리
        var isClientLoggedIn = document.cookie
          .split(";")
          .some((item) => item.trim().startsWith("logged_in="));

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
                  '<button id="logoutBtn" class="btn btn-outline-light btn-sm d-flex align-items-center justify-content-center" style="min-height: 31px;">로그아웃</button>' +
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
      });
    </script>
  </body>
</html>
