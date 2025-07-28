package dev.kreaker.kjk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration for proper JSON handling.
 * Configures ObjectMapper with JavaTimeModule for LocalDateTime serialization
 * and disables timestamp serialization to use ISO format.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures ObjectMapper with proper settings for JSON handling.
     * - Registers JavaTimeModule for LocalDateTime support
     * - Disables timestamp serialization to use ISO format
     * 
     * @return configured ObjectMapper instance
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for LocalDateTime serialization
        mapper.registerModule(new JavaTimeModule());
        
        // Disable timestamp serialization to use ISO format
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        return mapper;
    }
}