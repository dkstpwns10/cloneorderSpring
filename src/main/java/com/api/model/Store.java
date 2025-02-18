package com.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name = "stores")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 100, nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false, precision = 17, scale = 14)
    private BigDecimal latitude;
    
    @Column(nullable = false, precision = 17, scale = 14)
    private BigDecimal longitude;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(length = 15)
    private String phone;
    
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();
    
    @CreationTimestamp
    @Column(nullable = false)
    private ZonedDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "image_url")
    private String imageUrl;
}

