/**
 * 회원가입 페이지 관련 스크립트
 */
function validateForm() {
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return false;
    }
    
    return true;
} 