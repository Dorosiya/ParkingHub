/**
 * 회원가입 페이지 관련 스크립트
 */
$(document).ready(function() {
    // 폼 제출 시 유효성 검사
    $('#registerForm').on('submit', function(e) {
        // 비밀번호 검증
        const password = $('#password').val();
        const confirmPassword = $('#confirmPassword').val();
        
        // 비밀번호 일치 확인
        if (password !== confirmPassword) {
            e.preventDefault();
            alert('비밀번호가 일치하지 않습니다.');
            return false;
        }
        
        // 비밀번호 유효성 검사 (8-20자, 영문, 숫자, 특수문자 포함)
        const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,20}$/;
        if (!passwordRegex.test(password)) {
            e.preventDefault();
            alert('비밀번호는 8-20자 사이의 영문, 숫자, 특수문자를 포함해야 합니다.');
            return false;
        }
        
        // 약관 동의 확인
        if (!$('#termsCheck').is(':checked') || !$('#privacyCheck').is(':checked')) {
            e.preventDefault();
            alert('서비스 이용약관과 개인정보 처리방침에 동의해야 합니다.');
            return false;
        }
        
        // 모든 유효성 검사 통과
        return true;
    });
}); 