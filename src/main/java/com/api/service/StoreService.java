package com.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.model.Store;
import com.api.repository.StoreRepository;

@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    
    @Transactional
    public Store createStore(Store store) {
        if (storeRepository.existsByName(store.getName())) {
            throw new IllegalArgumentException("이미 존재하는 가게명입니다.");
        }
        
        // 연관된 메뉴들에 store 설정
        if (store.getMenus() != null) {
            store.getMenus().forEach(menu -> menu.setStore(store));
        }
        
        return storeRepository.save(store);
    }
    
    @Transactional(readOnly = true)
    public Store getStore(Long id) {
        return storeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
    }
}