package com.example.springboottests.integration;

import com.example.springboottests.integration.config.WiremockStubsLoader;
import com.example.springboottests.misc.annotations.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;

import static com.example.springboottests.misc.utils.TestUtils.loadFile;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

/**
 * Integration test class for verifying the processing of notes.
 * These tests validate the behavior of note processing in a complete environment,
 * including interactions with IBM MQ, database operations, and WireMock-stubbed requests to dependent services.
 *
 * @author Georgii Lvov
 */
@IntegrationTest
class NoteProcessingIT {

    private static final int AWAIT_TIMEOUT_SECONDS = 5;

    @Value("${queue.name}")
    private String requestQueue;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private WiremockStubsLoader wiremockStubsLoader;

    /**
     * Integration test method to verify the successful processing of notes.
     * This test ensures that notes are successfully processed, and the expected requests are made to dependent services.
     * It validates that the expected request body is sent to the note service.
     */
    @Test
    void testNoteSuccessfullyProcessed() {
        wiremockStubsLoader.loadStubs(
                loadFile("/data/json/wiremock/stubs/oauth2ClientToken.json"),
                loadFile("/data/json/wiremock/stubs/noteServiceCreated.json")
        );

        String incomingNoteXml = loadFile("/data/xml/note.xml");
        String expectedNotesRequestBody = loadFile("/data/json/noteInfo.json");

        sendMessageToQueue(incomingNoteXml);

        await().atMost(AWAIT_TIMEOUT_SECONDS, SECONDS)
                .untilAsserted(() ->
                        verify(
                                postRequestedFor(urlPathEqualTo("/notes"))
                                        .withRequestBody(equalToJson(expectedNotesRequestBody))
                        )
                );
    }

    private void sendMessageToQueue(String message) {
        jmsTemplate.convertAndSend(requestQueue, message);
    }
}
