package com.example.springboottests.misc.annotations;

import com.example.springboottests.misc.config.Oauth2JwtAuthorizationWireMockServerConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for autoconfiguring a WireMock server to mock OAuth2 authorization server endpoints.
 * <p>
 * This annotation is used to automatically configure WireMock server to mock endpoints related to OAuth2
 * authorization server, such as OpenID Connect configuration and JSON Web Key (JWK) retrieval
 *
 * @author Georgii Lvov
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureWireMock
@Import(Oauth2JwtAuthorizationWireMockServerConfiguration.class)
@ActiveProfiles("wiremock-auth")
public @interface AutoConfigureWiremockAuthorizationServer {
}
