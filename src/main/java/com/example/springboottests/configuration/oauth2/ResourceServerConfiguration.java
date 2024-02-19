package com.example.springboottests.configuration.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Objects;

/**
 * Configuration class for the resource server, responsible for handling JWT authentication and authorization.
 * This class configures security filters and adds custom token validation for incoming requests.
 *
 * @author Georgii Lvov
 */
@Configuration
public class ResourceServerConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http HttpSecurity object
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http.authorizeHttpRequests(configurer -> configurer
                        .requestMatchers("/actuator/**", "/error")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(configurer -> configurer.jwt(Customizer.withDefaults()))
                .build();
    }

    /**
     * Bean of {@link JwtDecoder} type with two additional {@link JwtClaimValidator} validators
     * to validate custom operation claim.
     *
     * @param request HttpServletRequest
     * @return JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder(HttpServletRequest request) {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        List<OAuth2TokenValidator<Jwt>> validators = List.of(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                new JwtClaimValidator<>(
                        "operation",
                        claimValue -> Objects.equals(claimValue, request.getMethod())
                )
        );

        jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(validators));

        return jwtDecoder;
    }
}
