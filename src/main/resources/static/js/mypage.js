/**
 * 마이페이지 통합 자바스크립트
 * - 프로필 관리
 * - 비밀번호 변경
 * - 즐겨찾기 관리
 */

document.addEventListener('DOMContentLoaded', function() {
    // 인증 확인
    checkAuthentication();
    
    // 즐겨찾기 기능 초기화
    if (document.getElementById('favorites-list')) {
        loadFavorites();
    }
    
    // 프로필 편집 기능 초기화
    if (document.querySelector('.profile-form')) {
        loadUserInfo();
        setupProfileForm();
    }
    
    // 비밀번호 변경 기능 초기화
    if (document.querySelector('.password-form')) {
        setupPasswordForm();
    }

    // 이벤트 리스너 등록 - 즐겨찾기 관련
    document.addEventListener('click', function(e) {
        // 메모 수정 버튼
        if (e.target.classList.contains('edit-memo-btn')) {
            const favoriteId = e.target.getAttribute('data-id');
            showEditMemoForm(favoriteId);
        }
        
        // 즐겨찾기 삭제 버튼
        if (e.target.classList.contains('delete-favorite-btn')) {
            const favoriteId = e.target.getAttribute('data-id');
            deleteFavorite(favoriteId);
        }
        
        // 주차장 상세보기 버튼
        if (e.target.classList.contains('view-parking-btn')) {
            const prkCenterId = e.target.getAttribute('data-center-id');
            window.location.href = `/parking/detail/${prkCenterId}`;
        }
    });
});

/**
 * 인증 상태 확인 및 처리
 */
function checkAuthentication() {
    // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
    if (typeof isAuthenticated === 'function' && !isAuthenticated()) {
        const currentPath = window.location.pathname;
        window.location.href = '/login?redirect=' + currentPath;
        return;
    }
}

/**
 * 프로필 편집 폼 설정
 */
function setupProfileForm() {
    const profileForm = document.getElementById('profileForm');
    if (profileForm) {
        profileForm.addEventListener('submit', function(e) {
            e.preventDefault();
            updateProfile();
        });
    }
}

/**
 * 사용자 정보 로드
 */
function loadUserInfo() {
    // jQuery가 있고 AuthService도 정의되어 있으면 그것 사용
    if (typeof $ !== 'undefined' && typeof AuthService !== 'undefined') {
        $.ajax({
            url: '/api/auth/me',
            type: 'GET',
            success: function(user) {
                // 폼에 사용자 정보 채우기
                if (document.getElementById('username')) {
                    document.getElementById('username').value = user.username || '';
                }
                if (document.getElementById('email')) {
                    document.getElementById('email').value = user.email || '';
                }
                if (document.getElementById('phone')) {
                    document.getElementById('phone').value = user.phone || '';
                }
                
                // 사용자 이름 표시
                const usernameElements = document.querySelectorAll('.user-name');
                usernameElements.forEach(function(element) {
                    element.textContent = user.username || '사용자';
                });
            },
            error: function(xhr) {
                console.error('Error:', xhr);
                showErrorMessage('사용자 정보를 가져오는데 실패했습니다.');
            }
        });
    } else {
        // jQuery나 AuthService가 없으면 fetch API 사용
        fetch('/api/auth/me')
            .then(response => {
                if (!response.ok) {
                    throw new Error('사용자 정보를 불러오는데 실패했습니다');
                }
                return response.json();
            })
            .then(user => {
                // 폼에 사용자 정보 채우기
                if (document.getElementById('username')) {
                    document.getElementById('username').value = user.username || '';
                }
                if (document.getElementById('email')) {
                    document.getElementById('email').value = user.email || '';
                }
                if (document.getElementById('phone')) {
                    document.getElementById('phone').value = user.phone || '';
                }
                
                // 사용자 이름 표시
                const usernameElements = document.querySelectorAll('.user-name');
                usernameElements.forEach(function(element) {
                    element.textContent = user.username || '사용자';
                });
            })
            .catch(error => {
                console.error('Error:', error);
                showErrorMessage('사용자 정보를 가져오는데 실패했습니다.');
            });
    }
}

/**
 * 프로필 업데이트
 */
function updateProfile() {
    var userData = {
        phone: document.getElementById('phone').value
    };
    
    // jQuery 사용하여 API 호출
    if (typeof $ !== 'undefined') {
        $.ajax({
            url: '/api/users/profile',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(userData),
            success: function(response) {
                showSuccessMessage('프로필이 성공적으로 업데이트되었습니다.');
            },
            error: function(xhr) {
                console.error('Error:', xhr);
                showErrorMessage('프로필 업데이트에 실패했습니다.');
            }
        });
    } else {
        // Fetch API 사용
        fetch('/api/users/profile', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('프로필 업데이트에 실패했습니다');
            }
            return response.json();
        })
        .then(data => {
            showSuccessMessage('프로필이 성공적으로 업데이트되었습니다.');
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorMessage('프로필 업데이트에 실패했습니다.');
        });
    }
}

/**
 * 비밀번호 변경 폼 설정
 */
function setupPasswordForm() {
    const passwordForm = document.getElementById('passwordForm');
    if (!passwordForm) return;
    
    // 폼 제출 이벤트
    passwordForm.addEventListener('submit', function(e) {
        e.preventDefault();
        changePassword();
    });
    
    // 비밀번호 강도 체크
    const newPasswordInput = document.getElementById('newPassword');
    if (newPasswordInput) {
        newPasswordInput.addEventListener('input', checkPasswordStrength);
    }
    
    // 비밀번호 일치 확인
    const confirmPasswordInput = document.getElementById('confirmPassword');
    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', checkPasswordMatch);
    }
}

/**
 * 비밀번호 강도 확인
 */
function checkPasswordStrength() {
    const password = document.getElementById('newPassword').value;
    const strengthBar = document.querySelector('.password-strength');
    const feedback = document.querySelector('.password-feedback');
    
    if (!strengthBar || !feedback) return;
    
    // 비밀번호 강도 계산
    let strength = 0;
    if (password.length >= 8) strength += 1;
    if (password.match(/[a-z]+/)) strength += 1;
    if (password.match(/[A-Z]+/)) strength += 1;
    if (password.match(/[0-9]+/)) strength += 1;
    if (password.match(/[^a-zA-Z0-9]+/)) strength += 1;
    
    // 강도에 따른 색상 및 메시지
    switch (strength) {
        case 0:
        case 1:
            strengthBar.style.width = '20%';
            strengthBar.className = 'password-strength bg-danger';
            feedback.textContent = '매우 약한 비밀번호입니다.';
            break;
        case 2:
            strengthBar.style.width = '40%';
            strengthBar.className = 'password-strength bg-warning';
            feedback.textContent = '약한 비밀번호입니다.';
            break;
        case 3:
            strengthBar.style.width = '60%';
            strengthBar.className = 'password-strength bg-info';
            feedback.textContent = '보통 강도의 비밀번호입니다.';
            break;
        case 4:
            strengthBar.style.width = '80%';
            strengthBar.className = 'password-strength bg-primary';
            feedback.textContent = '강한 비밀번호입니다.';
            break;
        case 5:
            strengthBar.style.width = '100%';
            strengthBar.className = 'password-strength bg-success';
            feedback.textContent = '매우 강한 비밀번호입니다.';
            break;
    }
}

/**
 * 비밀번호 일치 확인
 */
function checkPasswordMatch() {
    const password = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const feedback = document.querySelector('.confirm-feedback');
    
    if (!feedback) return;
    
    if (confirmPassword === '') {
        feedback.textContent = '';
        return;
    }
    
    if (password === confirmPassword) {
        feedback.className = 'confirm-feedback text-success';
        feedback.textContent = '비밀번호가 일치합니다.';
    } else {
        feedback.className = 'confirm-feedback text-danger';
        feedback.textContent = '비밀번호가 일치하지 않습니다.';
    }
}

/**
 * 비밀번호 변경 처리
 */
function changePassword() {
    const currentPassword = document.getElementById('currentPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // 비밀번호 유효성 검사
    if (newPassword.length < 8) {
        showErrorMessage('비밀번호는 최소 8자 이상이어야 합니다.');
        return;
    }
    
    const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!passwordRegex.test(newPassword)) {
        showErrorMessage('비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.');
        return;
    }
    
    // 비밀번호 일치 확인
    if (newPassword !== confirmPassword) {
        showErrorMessage('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
        return;
    }
    
    const passwordData = {
        currentPassword: currentPassword,
        newPassword: newPassword
    };
    
    // API 호출
    if (typeof $ !== 'undefined') {
        $.ajax({
            url: '/api/users/password',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(passwordData),
            success: function(response) {
                showSuccessMessage('비밀번호가 성공적으로 변경되었습니다.');
                document.getElementById('passwordForm').reset();
            },
            error: function(xhr) {
                let errorMsg = '비밀번호 변경에 실패했습니다.';
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    errorMsg = xhr.responseJSON.message;
                }
                showErrorMessage(errorMsg);
            }
        });
    } else {
        // Fetch API 사용
        fetch('/api/users/password', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(passwordData)
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.message || '비밀번호 변경에 실패했습니다.');
                });
            }
            return response.json();
        })
        .then(data => {
            showSuccessMessage('비밀번호가 성공적으로 변경되었습니다.');
            document.getElementById('passwordForm').reset();
        })
        .catch(error => {
            showErrorMessage(error.message);
        });
    }
}

/**
 * 즐겨찾기 목록 로드
 */
function loadFavorites() {
    fetch('/api/favorites')
        .then(response => {
            if (!response.ok) {
                throw new Error('즐겨찾기 목록을 불러오는데 실패했습니다');
            }
            return response.json();
        })
        .then(favorites => {
            displayFavorites(favorites);
        })
        .catch(error => {
            console.error('Error:', error);
            showErrorMessage('즐겨찾기 목록을 불러오는데 실패했습니다. 다시 시도해주세요.');
        });
}

/**
 * 즐겨찾기 목록 표시
 */
function displayFavorites(favorites) {
    const favoritesList = document.getElementById('favorites-list');
    favoritesList.innerHTML = '';
    
    if (favorites.length === 0) {
        favoritesList.innerHTML = '<p class="no-favorites">즐겨찾기한 주차장이 없습니다.</p>';
        return;
    }
    
    favorites.forEach(favorite => {
        const favoriteItem = document.createElement('div');
        favoriteItem.className = 'favorite-item';
        favoriteItem.id = `favorite-${favorite.id}`;
        
        // 주차장 이름이 없는 경우 "주차장"으로 표시
        const parkingName = favorite.prkPlceNm || '주차장';
        
        favoriteItem.innerHTML = `
            <div class="favorite-header">
                <h3>${parkingName}</h3>
                <div class="favorite-actions">
                    <button class="btn edit-memo-btn" data-id="${favorite.id}">메모 수정</button>
                    <button class="btn delete-favorite-btn" data-id="${favorite.id}">삭제</button>
                    <button class="btn view-parking-btn" data-center-id="${favorite.prkCenterId}">상세보기</button>
                </div>
            </div>
            <p class="favorite-memo">${favorite.memo || '메모 없음'}</p>
            <div class="memo-edit-form" id="memo-form-${favorite.id}" style="display: none;">
                <textarea id="memo-text-${favorite.id}">${favorite.memo || ''}</textarea>
                <div class="memo-form-actions">
                    <button class="btn save-memo-btn" onclick="saveMemo(${favorite.id})">저장</button>
                    <button class="btn cancel-memo-btn" onclick="hideEditMemoForm(${favorite.id})">취소</button>
                </div>
            </div>
        `;
        
        favoritesList.appendChild(favoriteItem);
    });
}

/**
 * 메모 수정 폼 표시
 */
function showEditMemoForm(favoriteId) {
    document.getElementById(`memo-form-${favoriteId}`).style.display = 'block';
    document.querySelector(`#favorite-${favoriteId} .favorite-memo`).style.display = 'none';
}

/**
 * 메모 수정 폼 숨김
 */
function hideEditMemoForm(favoriteId) {
    document.getElementById(`memo-form-${favoriteId}`).style.display = 'none';
    document.querySelector(`#favorite-${favoriteId} .favorite-memo`).style.display = 'block';
}

/**
 * 메모 저장
 */
function saveMemo(favoriteId) {
    const memo = document.getElementById(`memo-text-${favoriteId}`).value;
    
    fetch(`/api/favorites/${favoriteId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ memo })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('메모 저장에 실패했습니다');
        }
        return response.json();
    })
    .then(updatedFavorite => {
        // UI 업데이트
        document.querySelector(`#favorite-${favoriteId} .favorite-memo`).textContent = memo || '메모 없음';
        hideEditMemoForm(favoriteId);
        showSuccessMessage('메모가 저장되었습니다');
    })
    .catch(error => {
        console.error('Error:', error);
        showErrorMessage('메모 저장에 실패했습니다. 다시 시도해주세요.');
    });
}

/**
 * 즐겨찾기 삭제
 */
function deleteFavorite(favoriteId) {
    if (!confirm('정말로 이 주차장을 즐겨찾기에서 삭제하시겠습니까?')) {
        return;
    }
    
    fetch(`/api/favorites/${favoriteId}`, {
        method: 'DELETE'
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('즐겨찾기 삭제에 실패했습니다');
        }
        
        // UI에서 삭제
        const favoriteItem = document.getElementById(`favorite-${favoriteId}`);
        favoriteItem.remove();
        
        // 즐겨찾기가 하나도 없는 경우 메시지 표시
        const favoritesList = document.getElementById('favorites-list');
        if (favoritesList.children.length === 0) {
            favoritesList.innerHTML = '<p class="no-favorites">즐겨찾기한 주차장이 없습니다.</p>';
        }
        
        showSuccessMessage('즐겨찾기가 삭제되었습니다');
    })
    .catch(error => {
        console.error('Error:', error);
        showErrorMessage('즐겨찾기 삭제에 실패했습니다. 다시 시도해주세요.');
    });
}

/**
 * 성공 메시지 표시
 */
function showSuccessMessage(message) {
    const alertBox = document.getElementById('alert-box') || createAlertBox();
    alertBox.className = 'alert alert-success';
    alertBox.textContent = message;
    alertBox.style.display = 'block';
    
    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 3000);
}

/**
 * 오류 메시지 표시
 */
function showErrorMessage(message) {
    const alertBox = document.getElementById('alert-box') || createAlertBox();
    alertBox.className = 'alert alert-danger';
    alertBox.textContent = message;
    alertBox.style.display = 'block';
    
    setTimeout(() => {
        alertBox.style.display = 'none';
    }, 3000);
}

/**
 * 알림 상자 생성
 */
function createAlertBox() {
    const alertBox = document.createElement('div');
    alertBox.id = 'alert-box';
    alertBox.className = 'alert';
    alertBox.style.display = 'none';
    alertBox.style.position = 'fixed';
    alertBox.style.top = '20px';
    alertBox.style.left = '50%';
    alertBox.style.transform = 'translateX(-50%)';
    alertBox.style.zIndex = '9999';
    alertBox.style.padding = '10px 20px';
    document.body.appendChild(alertBox);
    
    return alertBox;
} 