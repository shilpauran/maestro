package com.sap.slh.tax.maestro.config;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.sap.slh.tax.destinationconfiguration.destinations.dto.DestinationResponse;
import com.sap.slh.tax.maestro.tax.models.DestinationCacheKey;

@Configuration
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    private static final Integer DEFAULT_VALIDITY_IN_SECONDS = 86400;

    @Bean
    public Cache<DestinationCacheKey, DestinationResponse> getCache() {
        return Caffeine.newBuilder().expireAfter(new Expiry<DestinationCacheKey, DestinationResponse>() {

            @Override
            public long expireAfterCreate(DestinationCacheKey key, DestinationResponse value, long currentTime) {
                Integer validity = DEFAULT_VALIDITY_IN_SECONDS;
                if (value.getValidity() != null)
                    validity = value.getValidity();

                logger.info("{} put in destination cache with validity of {} seconds", key, validity);
                return TimeUnit.SECONDS.toNanos(validity);
            }

            @Override
            public long expireAfterUpdate(DestinationCacheKey key, DestinationResponse value, long currentTime,
                    long currentDuration) {
                return currentDuration;
            }

            @Override
            public long expireAfterRead(DestinationCacheKey key, DestinationResponse value, long currentTime,
                    long currentDuration) {
                return currentDuration;
            }
        }).build();
    }

}