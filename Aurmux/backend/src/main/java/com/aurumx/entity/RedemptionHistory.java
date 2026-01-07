package com.aurumx.entity;

import com.aurumx.enums.RedemptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "redemption_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.ToString.Exclude
    private Customer customer;
    
    @Column(name = "total_points_used", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPointsUsed;
    
    @CreationTimestamp
    @Column(name = "redeemed_at", updatable = false)
    private LocalDateTime redeemedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RedemptionStatus status = RedemptionStatus.COMPLETED;
    
    @OneToMany(mappedBy = "redemption", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RedemptionItem> items = new ArrayList<>();
}
