package com.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.model.Store;
import com.api.repository.StoreRepository;
import com.api.model.Menu;
import com.api.service.MenuService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final MenuService menuService;
    
    @Transactional
    public Store createStoreWithMenus(Store store, List<Menu> menus) {
        // 1. 가게 정보 저장
        Store savedStore = createStore(store);
        
        // 2. 메뉴 정보 설정 및 저장
        if (menus != null && !menus.isEmpty()) {
            menus.forEach(menu -> menu.setStore(savedStore));
            menuService.saveMenus(menus);
        }
        
        return savedStore;
    }
    
    @Transactional
    public Store createStore(Store store) {
        if (storeRepository.existsByName(store.getName())) {
            throw new IllegalArgumentException("이미 존재하는 가게명입니다.");
        }
        return storeRepository.save(store);
    }
    
    @Transactional(readOnly = true)
    public Store getStore(Long id) {
        return storeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
    }

    @Transactional
    public Store updateStore(Store store) {
        return storeRepository.save(store);
    }
}