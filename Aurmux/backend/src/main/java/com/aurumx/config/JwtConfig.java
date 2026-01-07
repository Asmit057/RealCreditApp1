package com.aurumx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    
    /**
     * JWT secret key for token signing
     * IMPORTANT: Change this in production via environment variable
     */
    private String secret;
    
    /**
     * JWT token expiration time in milliseconds
     * Default: 86400000 (24 hours)
     */
    private long expiration;
}
