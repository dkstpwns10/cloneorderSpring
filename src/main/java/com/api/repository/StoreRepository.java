package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.model.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
}