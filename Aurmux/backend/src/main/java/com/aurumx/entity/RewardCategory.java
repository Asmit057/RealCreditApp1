package com.aurumx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reward_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardCategory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}
