package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.model.Store;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByName(String name);
    
    @Query(value = "SELECT currval('stores_id_seq')", nativeQuery = true)
    Long getCurrentId();

    @Query(value = "SELECT nextval('stores_id_seq')", nativeQuery = true)
    Long getNextId();
}

