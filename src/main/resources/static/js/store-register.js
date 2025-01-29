// 전역 변수로 선언
let menuCount = 0;
let storeId = null;

// 주소 검색 함수
window.searchAddress = function() {
    new daum.Postcode({
        oncomplete: function(data) {
            const address = data.address;
            document.getElementById('address').value = address;
            document.getElementById('detailAddress').focus();
            
            // 주소로 좌표 변환
            const geocoder = new kakao.maps.services.Geocoder();
            geocoder.addressSearch(address, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    // 위도(y)와 경도(x)를 15자리 소수점까지 저장
                    const latitude = parseFloat(result[0].y).toFixed(14);  // 위도
                    const longitude = parseFloat(result[0].x).toFixed(14); // 경도
                    
                    document.getElementById('latitude').value = latitude;
                    document.getElementById('longitude').value = longitude;
                    
                    console.log('변환된 좌표:', {
                        latitude: latitude,
                        longitude: longitude
                    });
                } else {
                    alert('주소를 좌표로 변환하는데 실패했습니다.');
                    document.getElementById('latitude').value = '';
                    document.getElementById('longitude').value = '';
                }
            });
        }
    }).open();
};

// 상세주소 입력 시 전체 주소 업데이트
document.getElementById('detailAddress').addEventListener('input', function() {
    const address = document.getElementById('address').value;
    const detailAddress = this.value;
    document.getElementById('location').value = address + ' ' + detailAddress;
});

// 메뉴 추가 함수 (onclick 이벤트로 직접 호출)
function addMenuItem() {
    const menuList = document.getElementById('menuList');
    const menuItem = document.createElement('div');
    menuItem.className = 'menu-item';
    menuItem.innerHTML = `
        <div class="form-group">
            <label>메뉴명 *</label>
            <input type="text" name="menus[${menuCount}].name" required>
        </div>
        <div class="form-group">
            <label>설명</label>
            <textarea name="menus[${menuCount}].description"></textarea>
        </div>
        <div class="form-group">
            <label>가격 *</label>
            <input type="number" name="menus[${menuCount}].price" min="0" step="100" required>
        </div>
        <div class="form-group">
            <label>이미지</label>
            <input type="file" class="menu-image" accept="image/*">
            <input type="hidden" name="menus[${menuCount}].imageUrl">
        </div>
        <button type="button" class="btn remove-btn" onclick="this.parentElement.remove()">삭제</button>
    `;
    menuList.appendChild(menuItem);
    menuCount++;
}

// 가게 이미지 업로드
async function storeImageUpload(input) {
    const file = input.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('/stores/upload/store', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('이미지 업로드에 실패했습니다.');
        }

        const imageUrl = await response.text();
        document.getElementById('storeImageUrl').value = imageUrl;
    } catch (error) {
        alert(error.message);
    }
}

// 메뉴 이미지 업로드
async function menuImageUpload(input) {
    const file = input.files[0];
    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch(`/stores/upload/menu/${storeId}`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('이미지 업로드에 실패했습니다.');
        }

        const imageUrl = await response.text();
        input.closest('.menu-item').querySelector('input[name$="].imageUrl"]').value = imageUrl;
    } catch (error) {
        alert(error.message);
        input.value = '';
    }
}

// 폼 제출 처리
async function handleSubmit(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const storeData = {
        name: formData.get('name'),
        location: formData.get('location'),
        latitude: parseFloat(document.getElementById('latitude').value),
        longitude: parseFloat(document.getElementById('longitude').value),
        phone: formData.get('phone'),
        imageUrl: formData.get('imageUrl'),
        menus: []
    };
    
    // 위도/경도 값이 있는지 확인
    if (!storeData.latitude || !storeData.longitude) {
        alert('주소 검색을 통해 위치 정보를 입력해주세요.');
        return;
    }

    try {
        // 먼저 가게 정보를 저장하고 ID를 받아옴
        const response = await fetch('/stores', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(storeData)
        });

        if (!response.ok) {
            throw new Error(await response.text());
        }

        const result = await response.json();
        storeId = result.id; // 저장된 가게의 ID를 저장

        // 이제 메뉴 이미지 업로드 처리
        const menuItems = document.querySelectorAll('.menu-item');
        for (const item of menuItems) {
            const fileInput = item.querySelector('input[type="file"]');
            if (fileInput.files.length > 0) {
                await menuImageUpload(fileInput);
            }
        }

        // 메뉴 정보 수집 및 업데이트
        const menuData = [];
        menuItems.forEach((item, index) => {
            const price = parseFloat(formData.get(`menus[${index}].price`));
            if (isNaN(price)) {
                throw new Error('메뉴 가격을 올바르게 입력해주세요.');
            }
            
            menuData.push({
                name: formData.get(`menus[${index}].name`),
                description: formData.get(`menus[${index}].description`),
                price: price,
                imageUrl: item.querySelector('input[name$="].imageUrl"]').value
            });
        });

        // 메뉴 정보 업데이트 요청
        const updateResponse = await fetch(`/stores/${storeId}/menus`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(menuData)
        });

        if (!updateResponse.ok) {
            const errorText = await updateResponse.text();
            throw new Error(`메뉴 정보 저장 실패: ${errorText}`);
        }

        const updatedStore = await updateResponse.json();
        console.log('저장된 가게 정보:', updatedStore);

        alert('가게와 메뉴 정보가 성공적으로 저장되었습니다.');
        location.reload();

    } catch (error) {
        alert(error.message);
    }
}

// DOM이 로드된 후 이벤트 리스너 등록
document.addEventListener('DOMContentLoaded', function() {
    // 주소 검색 버튼 이벤트 리스너
    document.getElementById('searchAddressBtn').addEventListener('click', searchAddress);
    
    // 폼 제출 이벤트 리스너
    document.getElementById('storeForm').addEventListener('submit', handleSubmit);
});