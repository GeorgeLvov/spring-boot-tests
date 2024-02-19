package com.example.springboottests.functional.net;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.function.Consumer;

import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;

/**
 * A general sender component for sending HTTP requests using WebClient.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeneralSender {

    private final WebClient webClient;

    /**
     * Sends an HTTP request with retry logic in case of certain exceptions.
     *
     * @param url           The URL to send the request to.
     * @param method        The HTTP method of the request.
     * @param message       The request body.
     * @param headers       The HTTP headers of the request.
     * @param responseType  The expected response type.
     * @return              The response body.
     * @throws SenderPermanentException if there is a permanent failure in sending the request.
     * @throws PreConditionException if the response status is 412.
     */
    @Retryable(interceptor = "preConditionExceptionInterceptor")
    public <T> T send(String url, HttpMethod method, Object message, Consumer<HttpHeaders> headers,
                      Class<T> responseType) {
        try {
            log.debug("Sending request: {} {}, body: {}", method, url, message);

            ResponseEntity<T> response = webClient
                    .method(method)
                    .uri(URI.create(url))
                    .headers(headers)
                    .body(Mono.justOrEmpty(message), Object.class)
                    .retrieve()
                    .toEntity(responseType)
                    .block();

            log.debug("Request was sent successfully! Response: {}", response);

            return (responseType != Void.class && response != null)
                    ? response.getBody()
                    : null;

        } catch (WebClientResponseException e) {
            throw wrapWebClientResponseException(e);
        }
        catch (Exception e) {
            throw new SenderPermanentException("Failed to send request!", e);
        }
    }

    private RuntimeException wrapWebClientResponseException(WebClientResponseException e) {
        HttpStatus statusCode = HttpStatus.valueOf(e.getStatusCode().value());

        HttpRequest request = e.getRequest();

        if (request != null && log.isWarnEnabled()) {
            log.warn("WebClientResponseException occurred when sending request! StatusCode: {}, {} {} ",
                    statusCode,
                    request.getMethod(),
                    request.getURI()
            );
        }

        return statusCode == PRECONDITION_FAILED
                ? new PreConditionException("Failed to update resource due to precondition failure!", e)
                : new SenderPermanentException("Failed to send request!", e);
    }
}
