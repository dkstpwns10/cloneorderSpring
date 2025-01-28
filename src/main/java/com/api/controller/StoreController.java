package com.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.api.model.Store;
import com.api.service.StoreService;

@Controller
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("store", new Store());
        return "store/register";
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createStore(@RequestBody Store store) {
        try {
            Store savedStore = storeService.createStore(store);
            return ResponseEntity.ok(savedStore);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}