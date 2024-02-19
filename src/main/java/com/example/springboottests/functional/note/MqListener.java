package com.example.springboottests.functional.note;

import com.example.springboottests.functional.note.model.Note;
import jakarta.jms.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * JMS message listener component.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqListener {

    private final NoteProcessor noteProcessor;
    private final SessionErrorHandler errorHandler;

    /**
     * Listens for messages from the configured queue and processes them.
     *
     * @param message The received message in XML format, representing a {@link Note}.
     * @param session The JMS session associated with the message.
     */
    @JmsListener(destination = "${queue.name}")
    public void receiveMessage(String message, Session session) {

        log.debug("JMS session started. Received message from the queue:\n{}", message);

        try {
            noteProcessor.process(message);

            session.commit();

            log.debug("JMS session committed");
        } catch (Exception e) {
            errorHandler.handleError(e, message, session);
        }
    }
}
