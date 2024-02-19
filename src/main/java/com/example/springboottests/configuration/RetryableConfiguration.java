package com.example.springboottests.configuration;

import com.example.springboottests.functional.net.PreConditionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

/**
 * Configuration class for retry functionality.
 *
 * @author Georgii Lvov
 */
@Configuration
@EnableRetry
public class RetryableConfiguration {

    @Value("${retry.max-attempts}")
    private int maxAttempts;

    /**
     * Creates a RetryOperationsInterceptor for handling retry logic specifically for PreConditionException.
     *
     * @return RetryOperationsInterceptor.
     */
    @Bean
    public RetryOperationsInterceptor preConditionExceptionInterceptor() {
        RetryTemplate retryTemplate = RetryTemplate.builder()
                .maxAttempts(maxAttempts)
                .retryOn(PreConditionException.class)
                .traversingCauses()
                .build();

        return RetryInterceptorBuilder
                .stateless()
                .retryOperations(retryTemplate)
                .build();
    }
}
