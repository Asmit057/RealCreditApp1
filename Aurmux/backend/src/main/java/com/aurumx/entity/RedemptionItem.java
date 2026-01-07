package com.aurumx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "redemption_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redemption_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private RedemptionHistory redemption;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reward_item_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "category"})
    private RewardItem rewardItem;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "points_cost", nullable = false)
    private Integer pointsCost;  // Snapshot of cost at time of redemption
}
