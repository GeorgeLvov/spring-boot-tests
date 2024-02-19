package com.example.springboottests.misc.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Configuration class for setting up a PostgreSQL container for integration tests.
 * <p>
 * This class provides a bean for creating and configuring a PostgreSQL Docker container
 * using Testcontainers, allowing integration tests to interact with a PostgreSQL database.
 *
 * @author Georgii Lvov
 */
@TestConfiguration(proxyBeanMethods = false)
public class PostgreSqlContainerConfiguration {

    /**
     * Creates a {@link PostgreSQLContainer} bean for running a PostgreSQL Docker container.
     * <p>
     * The PostgreSQL Docker container is configured with the specified image version.
     * No additional dynamic properties need to be specified as this container utilizes
     * the {@link ServiceConnection} annotation.
     *
     * @return a {@link PostgreSQLContainer} instance representing the PostgreSQL container
     * @see ServiceConnection
     */
    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgreSQLContainer() {
        return new PostgreSQLContainer<>("postgres:latest");
    }
}
