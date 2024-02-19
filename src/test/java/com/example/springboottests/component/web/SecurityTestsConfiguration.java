package com.example.springboottests.component.web;

import com.example.springboottests.configuration.oauth2.ResourceServerConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Configuration class for setting up security tests.
 * <p>
 * It imports the {@link ResourceServerConfiguration} to include real security configurations.
 * Additionally, it defines a test controller.
 *
 * @author Georgii Lvov
 */
@TestConfiguration
@Import(ResourceServerConfiguration.class)
public class SecurityTestsConfiguration {

    @RestController
    static class TestController {

        @GetMapping("/auth/test")
        @ResponseStatus(HttpStatus.OK)
        void authTest() {
        }
    }
}
