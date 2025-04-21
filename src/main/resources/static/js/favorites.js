// 페이지 로드 시 사용자 인증 확인
document.addEventListener("DOMContentLoaded", function () {
  // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
  if (!isAuthenticated()) {
    window.location.href = "/login?redirect=/mypage/favorites";
  }
});

// 로그아웃 함수
function logout() {
  // 전역 로그아웃 함수 호출
  window.logout();
}

// 사용자 인증 확인
function isAuthenticated() {
  // 전역 isAuthenticated 함수 호출
  return window.isAuthenticated();
} 