package com.aurumx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardBalanceResponse {
    private Long customerId;
    private String customerName;
    private BigDecimal pointsBalance; // Total points
    private BigDecimal lifetimeEarned; // Total lifetime
    private java.util.List<CardRewardDto> cardRewards;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardRewardDto {
        private Long creditCardId;
        private String cardNumber;
        private BigDecimal points;
    }
}
