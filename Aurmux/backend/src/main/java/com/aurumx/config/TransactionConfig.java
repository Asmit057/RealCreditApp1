package com.aurumx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aurumx.transaction")
@Data
public class TransactionConfig {
    
    /**
     * Minimum transaction amount in rupees
     * Default: 500
     */
    private int minAmount;
    
    /**
     * Maximum transaction amount in rupees
     * Default: 50000
     */
    private int maxAmount;
    
    /**
     * Number of transactions to generate per request
     * Default: 50
     * Change this value in application.properties to generate more/fewer transactions
     */
    private int generationCount;
}
