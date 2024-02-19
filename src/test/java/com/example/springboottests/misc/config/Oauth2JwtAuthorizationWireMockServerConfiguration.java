package com.example.springboottests.misc.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.annotation.PostConstruct;
import org.jose4j.jwk.JsonWebKeySet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.example.springboottests.misc.utils.SecurityTestsUtils.RSA_JSON_WEB_KEY;
import static com.example.springboottests.misc.utils.TestUtils.loadFile;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

/**
 * Configuration class for setting up a WireMock server to mock OAuth2 JWT authorization server endpoints.
 * <p>
 * This class provides configuration to set up WireMock stubs for endpoints related to OAuth2 JWT authorization,
 * such as OpenID Connect configuration and JSON Web Key (JWK) retrieval.
 *
 * @author Georgii Lvov
 */
@TestConfiguration
public class Oauth2JwtAuthorizationWireMockServerConfiguration {

    private static final String OPEN_ID_CONNECT_WELL_KNOWN_CONFIGURATION_URI =
            "/auth/realms/default/.well-known/openid-configuration";

    private static final String JSON_WEB_KEY_URI  =
            "/auth/realms/default/protocol/openid-connect/certs";

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    /**
     * Configures WireMock stubs for OAuth2 JWT authorization server endpoints.
     * <p>
     * This method sets up stubs for OpenID Connect configuration
     * and JSON Web Key (JWK) retrieval endpoints.
     */
    @PostConstruct
    public void wireMockConfiguration() {
        String openidConfig = loadFile("/data/json/openIdConfig.json")
                .replace("${issuer-uri}", issuerUri);

        WireMock.stubFor(WireMock.get(urlPathEqualTo(OPEN_ID_CONNECT_WELL_KNOWN_CONFIGURATION_URI))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(openidConfig)
                )
        );

        WireMock.stubFor(WireMock.get(urlPathEqualTo(JSON_WEB_KEY_URI))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(new JsonWebKeySet(RSA_JSON_WEB_KEY).toJson())
                )
        );
    }
}
