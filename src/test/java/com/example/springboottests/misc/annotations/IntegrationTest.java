package com.example.springboottests.misc.annotations;

import com.example.springboottests.integration.config.IntegrationTestConfiguration;
import com.example.springboottests.misc.config.IbmMqContainerConfiguration;
import com.example.springboottests.misc.config.Oauth2JwtAuthorizationWireMockServerConfiguration;
import com.example.springboottests.misc.config.PostgreSqlContainerConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for integration tests.
 * <p>
 * This annotation configures integration tests with the necessary environment setup,
 * including WireMock server autoconfiguration, importing required configurations,
 * and activating the "integration-test" profile.
 *
 * @author Georgii Lvov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock
@Import({
        IntegrationTestConfiguration.class,
        Oauth2JwtAuthorizationWireMockServerConfiguration.class,
        IbmMqContainerConfiguration.class,
        PostgreSqlContainerConfiguration.class
})
@ActiveProfiles("integration-test")
public @interface IntegrationTest {
}
