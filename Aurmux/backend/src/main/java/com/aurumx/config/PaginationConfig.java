package com.aurumx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "pagination")
@Data
public class PaginationConfig {
    
    /**
     * Default page size for paginated queries
     * Default: 20
     */
    private int defaultPageSize;
    
    /**
     * Maximum allowed page size
     * Default: 100
     */
    private int maxPageSize;
}
