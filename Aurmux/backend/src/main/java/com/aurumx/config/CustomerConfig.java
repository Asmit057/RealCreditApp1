package com.aurumx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aurumx.customer")
@Data
public class CustomerConfig {
    
    /**
     * Number of years of association required for Premium customer status
     * Default: 3 years
     * Change this value in application.properties to modify the threshold
     */
    private int premiumAssociationYears;
}
