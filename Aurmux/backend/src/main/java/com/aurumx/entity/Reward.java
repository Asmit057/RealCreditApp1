package com.aurumx.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.aurumx.entity.CreditCard;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_id", nullable = false, unique = true)
    private CreditCard creditCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @Column(name = "points_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal pointsBalance = BigDecimal.ZERO;
    
    @Column(name = "lifetime_earned", nullable = false, precision = 10, scale = 2)
    private BigDecimal lifetimeEarned = BigDecimal.ZERO;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
