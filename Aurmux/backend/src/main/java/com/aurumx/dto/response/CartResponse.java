package com.aurumx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    
    private Long id; // Dummy ID or user ID
    private Long customerId;
    private List<CartItemDto> items;
    private Integer totalPoints;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDto {
        private Long id;
        private Long rewardItemId;
        private String rewardItemName;
        private Integer quantity;
        private Integer pointsCost;
        private Integer totalPoints;
        private LocalDateTime addedAt;
    }
}
