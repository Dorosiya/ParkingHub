/**
 * 인증 관련 기능을 담당하는 JavaScript 파일
 */

// 인증 관리 함수들
const AuthService = {
    // 토큰은 이제 HTTP 전용 쿠키로 관리되므로 JavaScript에서 직접 관리하지 않음
    
    // 로그인 여부 확인 (간접적으로 확인)
    isAuthenticated: function() {
        return document.cookie.split(';').some(item => item.trim().startsWith('logged_in='));
    },
    
    // API 요청 시 인증 헤더 추가 (쿠키가 자동으로 전송되므로 필요 없음)
    getAuthHeader: function() {
        return {};
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
            }),
            xhrFields: {
                withCredentials: true  // 쿠키를 포함하도록 설정
            }
        }).done(function(response) {
            // 로그인 성공 시 logged_in 쿠키 생성 (JavaScript에서 접근 가능)
            document.cookie = "logged_in=true; path=/;";
        });
    },
    
    // 로그아웃
    logout: function() {
        // 인증 상태 쿠키 제거
        document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
        
        // 서버에 로그아웃 요청 (서버에서 jwt_token 쿠키 제거)
        return $.ajax({
            url: '/api/auth/logout',
            type: 'POST',
            xhrFields: {
                withCredentials: true
            }
        });
    },
    
    // 현재 로그인 사용자 정보 가져오기
    getCurrentUser: function() {
        return $.ajax({
            url: '/api/user/current',
            type: 'GET',
            xhrFields: {
                withCredentials: true
            }
        });
    }
};

// 페이지 로드 시 실행되는 인증 초기화 코드
$(document).ready(function() {
    // 로그인 상태에 따라 UI 업데이트
    if (AuthService.isAuthenticated()) {
        $('.not-logged-in').hide();
        $('.logged-in').show();
        
        // 사용자 정보 가져오기
        AuthService.getCurrentUser()
            .done(function(user) {
                $('.user-name').text(user.username);
            })
            .fail(function() {
                // 사용자 정보를 가져오지 못한 경우 로그인 상태 초기화
                document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
                $('.not-logged-in').show();
                $('.logged-in').hide();
            });
    } else {
        $('.not-logged-in').show();
        $('.logged-in').hide();
    }
}); 