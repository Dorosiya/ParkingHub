/**
 * 인증 관련 공통 JavaScript 함수
 */

/**
 * 토큰이 저장되어 있는지 확인하여 로그인 상태를 판단합니다.
 * @returns {boolean} 로그인 여부
 */
function isAuthenticated() {
    // localStorage 또는 쿠키 기반 인증 확인
    return localStorage.getItem('token') !== null || document.cookie.indexOf('logged_in=true') !== -1;
}

/**
 * 로컬 스토리지에서 인증 토큰을 가져옵니다.
 * @returns {string|null} JWT 토큰 또는 null
 */
function getToken() {
    return localStorage.getItem('token');
}

/**
 * 인증 헤더를 생성합니다.
 * @returns {object} 인증 헤더가 포함된 객체
 */
function getAuthHeaders() {
    const token = getToken();
    if (!token) {
        return {};
    }
    return {
        'Authorization': `Bearer ${token}`
    };
}

/**
 * 로그인을 처리합니다.
 * @param {string} email 이메일
 * @param {string} password 비밀번호
 * @returns {Promise} 로그인 결과 프로미스
 */
function login(email, password) {
    return fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ username: email, password })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('로그인에 실패했습니다');
        }
        return response.json();
    })
    .then(data => {
        // 응답에 토큰이 있으면 로컬 스토리지에 저장
        if (data && data.token) {
            localStorage.setItem('token', data.token);
            if (data.id && data.username) {
                localStorage.setItem('user', JSON.stringify({
                    id: data.id,
                    username: data.username,
                    email: data.email || data.username // email이 없으면 username 사용
                }));
            }
        }
        return data;
    });
}

/**
 * 로그아웃을 처리합니다.
 */
function logout() {
    // 서버에 로그아웃 요청 보내기 (쿠키 삭제용)
    fetch('/api/auth/logout', {
        method: 'POST',
        credentials: 'include'
    })
    .finally(() => {
        // 로컬 스토리지에서 토큰 제거
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        // 로그인 페이지로 리다이렉트
        window.location.href = '/login-page';
    });
}

/**
 * 현재 로그인한 사용자 정보를 가져옵니다.
 * @returns {object|null} 사용자 정보 또는 null
 */
function getCurrentUser() {
    const userJson = localStorage.getItem('user');
    if (!userJson) {
        return null;
    }
    try {
        return JSON.parse(userJson);
    } catch (e) {
        console.error('사용자 정보 파싱 오류:', e);
        return null;
    }
}

/**
 * API 요청을 보낼 때 사용할 기본 fetch 함수입니다.
 * 인증 헤더가 자동으로 포함됩니다.
 * 
 * @param {string} url 요청 URL
 * @param {object} options fetch 옵션
 * @returns {Promise} fetch 결과 프로미스
 */
function authFetch(url, options = {}) {
    const headers = {
        ...options.headers,
        ...getAuthHeaders()
    };
    
    return fetch(url, {
        ...options,
        headers,
        credentials: 'include'  // 쿠키도 함께 포함
    })
    .then(response => {
        // 401 Unauthorized 응답이 오면 로그아웃 처리
        if (response.status === 401) {
            logout();
            throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
        }
        return response;
    });
}

// 페이지 로드 시 인증 상태 확인
document.addEventListener('DOMContentLoaded', function() {
    if (isAuthenticated()) {
        // 인증된 사용자인 경우
        document.querySelectorAll('.auth-link').forEach(link => {
            link.style.display = 'block';
        });
        
        document.querySelectorAll('.no-auth-link').forEach(link => {
            link.style.display = 'none';
        });
    } else {
        // 인증되지 않은 사용자인 경우
        document.querySelectorAll('.auth-link').forEach(link => {
            link.style.display = 'none';
        });
        
        document.querySelectorAll('.no-auth-link').forEach(link => {
            link.style.display = 'block';
        });
    }
});

// 전역 스코프에 함수 등록
window.isAuthenticated = isAuthenticated;
window.login = login;
window.logout = logout;
window.getCurrentUser = getCurrentUser;
window.authFetch = authFetch; 