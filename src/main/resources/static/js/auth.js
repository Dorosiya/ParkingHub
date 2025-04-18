/**
 * 인증 관련 공통 JavaScript 함수
 */

// 인증 관리 함수들
const AuthService = {
  // 토큰은 이제 HTTP 전용 쿠키로 관리되므로 JavaScript에서 직접 관리하지 않음

  // 로그인 여부 확인 (간접적으로 확인)
  isAuthenticated: function () {
    return document.cookie
      .split(";")
      .some((item) => item.trim().startsWith("logged_in="));
  },

  // API 요청 시 인증 헤더 추가 (쿠키가 자동으로 전송되므로 필요 없음)
  getAuthHeader: function () {
    return {};
  },

  // 로그인 요청
  login: function (username, password) {
    return $.ajax({
      url: "/api/auth/login",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify({
        username: username,
        password: password,
      }),
      xhrFields: {
        withCredentials: true, // 쿠키를 포함하도록 설정
      },
    }).done(function (response) {
      // 로그인 성공 시 logged_in 쿠키 생성 (JavaScript에서 접근 가능)
      document.cookie = "logged_in=true; path=/;";
    });
  },

  // 로그아웃 요청
  logout: function () {
    return $.ajax({
      url: "/api/auth/logout",
      type: "POST",
      xhrFields: {
        withCredentials: true,
      },
    }).always(function () {
      // 쿠키 삭제
      document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
      // 페이지 새로고침
      window.location.reload();
    });
  },

  // 현재 사용자 정보 요청
  getCurrentUser: function () {
    return $.ajax({
      url: "/api/user/current",
      type: "GET",
      xhrFields: {
        withCredentials: true,
      },
    });
  },

  // 회원가입 요청
  register: function (userData) {
    return $.ajax({
      url: "/api/auth/register",
      type: "POST",
      contentType: "application/json",
      data: JSON.stringify(userData),
    });
  },

  // 검증된 API 요청 (withCredentials 옵션 포함)
  apiRequest: function (url, method = "GET", data = null) {
    const options = {
      url: url,
      type: method,
      xhrFields: {
        withCredentials: true,
      },
    };

    if (data) {
      options.contentType = "application/json";
      options.data = JSON.stringify(data);
    }

    return $.ajax(options).fail(function (xhr) {
      // 401 (Unauthorized) 응답 시 로그인 페이지로 리다이렉트
      if (xhr.status === 401) {
        // 쿠키 삭제
        document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
        window.location.href = "/login";
      }
    });
  },
};

// 페이지 로드 시 인증 상태 확인 및 UI 업데이트
$(document).ready(function () {
  if (AuthService.isAuthenticated()) {
    // 인증된 경우 인증 관련 UI 보임
    $(".auth-only").show();
    $(".no-auth-only").hide();

    // 사용자 정보 가져오기
    AuthService.getCurrentUser()
      .done(function (user) {
        // 사용자 정보로 UI 업데이트
        if (user && user.username) {
          $(".user-name").text(user.username);
        }
      })
      .fail(function () {
        // 사용자 정보 가져오기 실패 시 로그아웃 처리
        AuthService.logout();
      });
  } else {
    // 인증되지 않은 경우 비인증 관련 UI 보임
    $(".auth-only").hide();
    $(".no-auth-only").show();
  }

  // 로그아웃 버튼 클릭 이벤트
  $(document).on("click", "#logoutBtn", function (e) {
    e.preventDefault();
    AuthService.logout();
  });
});

// 전역 객체에 등록
window.AuthService = AuthService;
