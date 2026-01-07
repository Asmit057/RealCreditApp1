package com.aurumx.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionResponse {
    private Long redemptionId;
    private Long customerId;
    private BigDecimal totalPointsUsed;
    private LocalDateTime redeemedAt;
    private List<RedemptionItemDto> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedemptionItemDto {
        private String rewardItemName;
        private Integer quantity;
        private Integer pointsCost;
        private Integer totalPoints;
    }
}
