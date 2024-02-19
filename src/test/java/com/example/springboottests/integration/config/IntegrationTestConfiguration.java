package com.example.springboottests.integration.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import reactor.core.publisher.Mono;

@Slf4j
@TestConfiguration
public class IntegrationTestConfiguration {

    /**
     * Creates a bean for {@link WiremockStubsLoader}, allowing the loading of additional WireMock stubs if needed.
     *
     * @param wireMockServer the WireMock server instance
     * @return an instance of {@link WiremockStubsLoader}
     */
    @Bean
    public WiremockStubsLoader wiremockStubsLoader(WireMockServer wireMockServer) {
        return new WiremockStubsLoader(wireMockServer);
    }

    /**
     * Creates a {@link WebTestClientBuilderCustomizer} bean to customize the WebTestClient.
     * <p>
     * This bean adds a filter to log request details before sending requests.
     *
     * @return a {@link WebTestClientBuilderCustomizer} instance
     */
    @Bean
    public WebTestClientBuilderCustomizer webTestClientBuilderCustomizer() {
        return (builder) -> builder.filter(logRequestFilter());
    }

    private static ExchangeFilterFunction logRequestFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }
}
