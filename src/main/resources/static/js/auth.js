/**
 * 인증 관련 기능을 담당하는 JavaScript 파일
 */

// JWT 토큰 관리 함수들
const AuthService = {
    // 로컬 스토리지에 토큰 저장
    setToken: function(token) {
        localStorage.setItem('jwt_token', token);
    },
    
    // 토큰 가져오기
    getToken: function() {
        return localStorage.getItem('jwt_token');
    },
    
    // 토큰 삭제 (로그아웃 시)
    removeToken: function() {
        localStorage.removeItem('jwt_token');
    },
    
    // 토큰 유효성 확인 (간단한 체크)
    isAuthenticated: function() {
        const token = this.getToken();
        return token !== null && token !== "";
    },
    
    // API 요청 시 인증 헤더 추가
    getAuthHeader: function() {
        return {
            'Authorization': 'Bearer ' + this.getToken()
        };
    },
    
    // 로그인 요청
    login: function(username, password) {
        return $.ajax({
            url: '/api/auth/login',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                username: username,
                password: password
            })
        });
    },
    
    // 로그아웃
    logout: function() {
        this.removeToken();
        // 필요하다면 서버에도 로그아웃 요청을 보낼 수 있음
        return $.ajax({
            url: '/api/auth/logout',
            type: 'POST',
            headers: this.getAuthHeader()
        });
    }
};

// 페이지 로드 시 실행되는 인증 초기화 코드
$(document).ready(function() {
    // 로그인 상태에 따라 UI 업데이트
    if (AuthService.isAuthenticated()) {
        $('.not-logged-in').hide();
        $('.logged-in').show();
    } else {
        $('.not-logged-in').show();
        $('.logged-in').hide();
    }
    
    // 모든 AJAX 요청에 인증 헤더 자동 추가
    $.ajaxSetup({
        beforeSend: function(xhr) {
            if (AuthService.isAuthenticated()) {
                const token = AuthService.getToken();
                xhr.setRequestHeader('Authorization', 'Bearer ' + token);
            }
        }
    });
}); 