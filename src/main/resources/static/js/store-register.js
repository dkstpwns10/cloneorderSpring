let menuCount = 0;

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
            <label>이미지 URL</label>
            <input type="text" name="menus[${menuCount}].imageUrl">
        </div>
        <button type="button" class="btn remove-btn" onclick="this.parentElement.remove()">삭제</button>
    `;
    menuList.appendChild(menuItem);
    menuCount++;
}

document.getElementById('storeForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const formData = new FormData(this);
    const storeData = {
        name: formData.get('name'),
        location: formData.get('location'),
        latitude: parseFloat(formData.get('latitude')),
        longitude: parseFloat(formData.get('longitude')),
        phone: formData.get('phone'),
        menus: []
    };

    // 메뉴 데이터 수집
    const menuItems = document.querySelectorAll('.menu-item');
    menuItems.forEach((item, index) => {
        storeData.menus.push({
            name: formData.get(`menus[${index}].name`),
            description: formData.get(`menus[${index}].description`),
            price: parseFloat(formData.get(`menus[${index}].price`)),
            imageUrl: formData.get(`menus[${index}].imageUrl`)
        });
    });

    try {
        const response = await fetch('/stores', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(storeData)
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error);
        }

        const result = await response.json();
        alert('가게와 메뉴 정보가 성공적으로 저장되었습니다.');
        location.reload();

    } catch (error) {
        alert(error.message);
    }
});