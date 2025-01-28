package com.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "menus")
public class Menu {
 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;
 
 @JsonIgnore
 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "store_id", nullable = false)
 private Store store;
 
 @Column(length = 100, nullable = false)
 private String name;
 
 private String description;
 
 @Column(nullable = false, precision = 10, scale = 2)
 private BigDecimal price;
 
 private String imageUrl;
 
 @CreationTimestamp
 @Column(nullable = false)
 private ZonedDateTime createdAt;
 
 @UpdateTimestamp
 @Column(nullable = false)
 private ZonedDateTime updatedAt;
}