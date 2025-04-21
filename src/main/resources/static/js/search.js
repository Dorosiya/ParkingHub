$(document).ready(function() {
    // 서버 측 인증 상태 체크 (JSP 표현식을 문자열로 변환하기 위해 숨겨진 입력값 사용)
    var isServerAuthenticated = $("#isAuthenticated").val() === "true";
    
    // 서버 인증이 되지 않았지만 클라이언트에 로그인 쿠키가 있는 경우에만 처리
    var isClientLoggedIn = document.cookie.split(';').some(item => item.trim().startsWith('logged_in='));
    
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
                }
            })
            .done(function(user) {
                // 사용자 정보로 UI 업데이트
                var userHtml =
                    '<div class="d-flex logged-in-menu">' +
                    '<span class="navbar-text me-3">' +
                    '<i class="bi bi-person-circle"></i> ' +
                    user.username +
                    "님" +
                    "</span>" +
                    '<button id="logoutBtn" class="btn btn-outline-light btn-sm">로그아웃</button>' +
                    "</div>";

                $("#navbarNav .navbar-nav").after(userHtml);

                // 로그아웃 버튼 이벤트
                $("#logoutBtn").click(function() {
                    $.ajax({
                        url: "/api/auth/logout",
                        type: "POST",
                        xhrFields: {
                            withCredentials: true,
                        }
                    }).always(function() {
                        window.location.reload();
                    });
                });
            })
            .fail(function() {
                // 토큰이 유효하지 않은 경우 쿠키 삭제
                document.cookie = "logged_in=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT";
            });
        }
    }
}); 