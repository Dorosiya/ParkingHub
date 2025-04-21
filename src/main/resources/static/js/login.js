/**
 * 로그인 페이지 관련 스크립트
 */
$(document).ready(function () {
  // 로그인 버튼 클릭 이벤트
  $("#loginButton").click(function () {
    const username = $("#username").val();
    const password = $("#password").val();

    // 입력 검증
    if (!username || !password) {
      $("#error-message").text("아이디와 비밀번호를 모두 입력해주세요.").show();
      return;
    }

    // 로그인 요청
    AuthService.login(username, password)
      .done(function (response) {
        // 로그인 성공 - 쿠키는 서버에서 자동으로 설정됨
        window.location.href = "/";
      })
      .fail(function (xhr, status, error) {
        // 로그인 실패
        const errorMsg =
          xhr.responseJSON && xhr.responseJSON.message
            ? xhr.responseJSON.message
            : "아이디 또는 비밀번호가 올바르지 않습니다.";
        $("#error-message").text(errorMsg).show();
      });
  });

  // 엔터키 처리
  $("#loginForm input").keypress(function (e) {
    if (e.which === 13) {
      // 엔터키
      $("#loginButton").click();
      return false;
    }
  });

  $('#loginForm').on('submit', function(e) {
    e.preventDefault();
    
    var userData = {
      username: $('#username').val(),
      password: $('#password').val()
    };
    
    $.ajax({
      url: '/api/auth/login',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(userData),
      success: function(response) {
        // 로그인 성공 시 홈페이지로 이동
        window.location.href = '/';
      },
      error: function(xhr, status, error) {
        // 로그인 실패 시 오류 메시지 표시
        var errorMessage = '로그인에 실패했습니다. 아이디와 비밀번호를 확인해주세요.';
        
        try {
          var response = JSON.parse(xhr.responseText);
          if (response && response.message) {
            errorMessage = response.message;
          }
        } catch (e) {
          console.error('Error parsing error response:', e);
        }
        
        $('#loginAlert').text(errorMessage).show();
      }
    });
  });
});
