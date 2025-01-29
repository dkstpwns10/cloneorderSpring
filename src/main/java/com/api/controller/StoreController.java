package com.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.api.model.Menu;
import com.api.model.Store;
import com.api.service.StoreService;
import com.api.service.S3Service;
import com.api.repository.StoreRepository;
import com.api.service.MenuService;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final S3Service s3Service;
    private final StoreRepository storeRepository;
    private final MenuService menuService;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("store", new Store());
        return "store/register";
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createStore(@RequestBody Store store) {
        try {
            // ID 값을 null로 설정하여 새로운 시퀀스 값이 자동 생성되도록 함
            store.setId(null);
            Store savedStore = storeService.createStore(store);
            return ResponseEntity.ok(savedStore);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> getStore(@PathVariable Long id) {
        try {
            Store store = storeService.getStore(id);
            return ResponseEntity.ok(store);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload/store")
    @ResponseBody
    public ResponseEntity<String> uploadStoreImage(@RequestParam("file") MultipartFile file) {
        try {
            // 임시 ID로 1을 사용 (실제 저장 시 자동 생성된 ID로 덮어씀)
            String imageUrl = s3Service.uploadStoreImage(file, 1L);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
    }

    @PostMapping("/upload/menu/{storeId}")
    @ResponseBody
    public ResponseEntity<String> uploadMenuImage(
        @RequestParam("file") MultipartFile file,
        @PathVariable Long storeId
    ) {
        try {
            String imageUrl = s3Service.uploadMenuImage(file, storeId);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 업로드에 실패했습니다.");
        }
    }

    @PostMapping("/{storeId}/menus")
    @ResponseBody
    public ResponseEntity<?> updateMenus(@PathVariable Long storeId, @RequestBody List<Menu> menus) {
        try {
            if (menus == null || menus.isEmpty()) {
                return ResponseEntity.badRequest().body("메뉴 정보가 없습니다.");
            }

            Store store = storeService.getStore(storeId);
            
            // 메뉴 데이터 유효성 검사
            for (Menu menu : menus) {
                if (menu.getName() == null || menu.getName().trim().isEmpty()) {
                    return ResponseEntity.badRequest().body("메뉴명은 필수입니다.");
                }
                if (menu.getPrice() == null) {
                    return ResponseEntity.badRequest().body("메뉴 가격은 필수입니다.");
                }
                menu.setStore(store);  // 각 메뉴에 store 설정
            }

            // 메뉴 저장
            List<Menu> savedMenus = menuService.saveMenus(menus);
            store.setMenus(savedMenus);
            
            return ResponseEntity.ok(store);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("메뉴 저장 실패: " + e.getMessage());
        }
    }
}