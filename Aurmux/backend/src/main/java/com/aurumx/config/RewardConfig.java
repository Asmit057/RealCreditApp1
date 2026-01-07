package com.aurumx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aurumx.reward")
@Data
public class RewardConfig {
    
    /**
     * Reward percentage for Regular customers
     * Default: 5%
     * Change this value in application.properties to modify reward calculation
     */
    private int regularPercentage;
    
    /**
     * Reward percentage for Premium customers
     * Default: 10%
     * Change this value in application.properties to modify reward calculation
     */
    private int premiumPercentage;
}
