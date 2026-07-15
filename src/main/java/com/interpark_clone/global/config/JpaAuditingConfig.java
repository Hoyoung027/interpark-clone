package com.interpark_clone.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Configuration
public class JpaAuditingConfig {

    private static final ZoneId KST_ZONE_ID = ZoneId.of("Asia/Seoul");

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(KST_ZONE_ID));
    }
}
