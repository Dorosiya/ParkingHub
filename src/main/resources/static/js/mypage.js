/**
 * 마이페이지 - 즐겨찾기 목록 관리 자바스크립트
 */

document.addEventListener('DOMContentLoaded', function() {
    // 초기화
    loadFavorites();

    // 이벤트 리스너 등록
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
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
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
        method: 'DELETE',
        headers: {
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
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
    alertBox.className = 'alert alert-error';
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
    document.body.appendChild(alertBox);
    
    return alertBox;
} 