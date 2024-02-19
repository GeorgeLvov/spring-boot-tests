package com.example.springboottests.configuration.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for OAuth2 client settings.
 * This class provides configuration for OAuth2 authentication when making requests to external services.
 *
 * @author Georgii Lvov
 */
@Configuration
public class ClientConfiguration {
    private static final String CLIENT_REGISTRATION_ID = "tardis";

    /**
     * Configures a WebClient with OAuth2 authentication capabilities.
     *
     * @param builder                  WebClient.Builder object for building WebClient instances
     * @param authorizedClientManager OAuth2AuthorizedClientManager for managing authorized clients
     * @param authorizedClientService OAuth2AuthorizedClientService for managing authorized client state
     * @return Configured WebClient instance
     */
    @Bean
    public WebClient oauthWebClient(WebClient.Builder builder,
                                    OAuth2AuthorizedClientManager authorizedClientManager,
                                    OAuth2AuthorizedClientService authorizedClientService) {
        var oauthFilterFunction = new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauthFilterFunction.setDefaultOAuth2AuthorizedClient(true);
        oauthFilterFunction.setDefaultClientRegistrationId(CLIENT_REGISTRATION_ID);
        oauthFilterFunction.setAuthorizationFailureHandler((exception, principal, attributes) -> {
            String registrationId = ((ClientAuthorizationException) exception).getClientRegistrationId();
            authorizedClientService.removeAuthorizedClient(registrationId, principal.getName());
        });

        return builder
                .filter(oauthFilterFunction)
                .apply(oauthFilterFunction.oauth2Configuration())
                .build();
    }

    /**
     * Configures an OAuth2AuthorizedClientManager for managing authorized clients.
     *
     * @param clientRegistrationRepository ClientRegistrationRepository for accessing client registrations
     * @param clientService                OAuth2AuthorizedClientService for managing authorized client state
     * @return Configured OAuth2AuthorizedClientManager instance
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService clientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .refreshToken()
                        .clientCredentials()
                        .build();

        var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, clientService);

        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
