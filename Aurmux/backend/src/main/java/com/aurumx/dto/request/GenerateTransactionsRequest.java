package com.aurumx.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GenerateTransactionsRequest {
    
    @NotNull(message = "Credit card ID is required")
    private Long creditCardId;
}
