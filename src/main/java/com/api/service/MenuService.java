package com.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.model.Menu;
import com.api.repository.MenuRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepository;
    
    @Transactional(readOnly = true)
    public List<Menu> getMenusByStoreId(Long storeId) {
        return menuRepository.findByStoreId(storeId);
    }
    
    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new IllegalArgumentException("메뉴를 찾을 수 없습니다."));
        
        menuRepository.delete(menu);
    }
}