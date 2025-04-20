/**
 * 인증 관련 공통 JavaScript 함수
 */

/**
 * 인증 관련 기능을 처리하는 AuthService
 */
const AuthService = (function () {
  return {
    /**
     * 로그인 여부 확인
     * 쿠키나 세션 스토리지를 확인하여 로그인 상태를 판단
     * @returns {boolean} 로그인 여부
     */
    isAuthenticated: function () {
      // 쿠키에서 로그인 상태 확인
      return document.cookie.split(";").some(function (cookie) {
        return cookie.trim().startsWith("logged_in=true");
      });
    },

    /**
     * 사용자 로그인
     * @param {Object} loginData - 로그인 정보 (username, password)
     * @returns {Promise} jQuery Promise 객체
     */
    login: function (loginData) {
      return $.ajax({
        url: "/api/auth/login",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(loginData),
      });
    },

    /**
     * 사용자 회원가입
     * @param {Object} registerData - 회원가입 정보 (email, username, password 등)
     * @returns {Promise} jQuery Promise 객체
     */
    register: function (registerData) {
      return $.ajax({
        url: "/api/auth/register",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(registerData),
      });
    },

    /**
     * 사용자 로그아웃
     * @returns {Promise} jQuery Promise 객체
     */
    logout: function () {
      return $.ajax({
        url: "/api/auth/logout",
        type: "POST",
      }).always(function () {
        // 쿠키 삭제
        document.cookie =
          "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
        // 페이지 새로고침 또는 리다이렉트
        window.location.href = "/";
      });
    },

    /**
     * 현재 로그인된 사용자 정보 조회
     * @returns {Promise} jQuery Promise 객체
     */
    getCurrentUser: function () {
      return $.ajax({
        url: "/api/auth/me",
        type: "GET",
      });
    },

    /**
     * 로그인 상태를 확인하고 UI를 업데이트
     * @param {Function} onAuthenticated - 인증된 경우 콜백
     * @param {Function} onUnauthenticated - 인증되지 않은 경우 콜백
     */
    updateAuthUI: function (onAuthenticated, onUnauthenticated) {
      this.getCurrentUser()
        .done(function (user) {
          if (onAuthenticated && typeof onAuthenticated === "function") {
            onAuthenticated(user);
          }
        })
        .fail(function () {
          if (onUnauthenticated && typeof onUnauthenticated === "function") {
            onUnauthenticated();
          }
        });
    },
  };
})();

// 전역 객체에 등록
window.AuthService = AuthService;

// jQuery가 로드된 경우에만 실행하는 코드
document.addEventListener("DOMContentLoaded", function () {
  if (typeof jQuery !== "undefined") {
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
  }
});

// jQuery 비의존적인 isAuthenticated 함수 전역 노출
window.isAuthenticated = function () {
  return AuthService.isAuthenticated();
};

// jQuery 비의존적인 로그아웃 함수 전역 노출
window.logout = function () {
  // API 호출 없이 쿠키 삭제 및 홈으로 리다이렉트
  document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
  window.location.href = "/";
};
