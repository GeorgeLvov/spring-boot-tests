package com.example.springboottests.functional.note;

import jakarta.jms.JMSException;
import jakarta.jms.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Component responsible for handling session errors during JMS message processing.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Component
public class SessionErrorHandler {

    /**
     * Handles errors that occur during message processing in a JMS session.
     * This method logs the error, determines whether the session should be rolled back or committed,
     * and performs the necessary action accordingly.
     *
     * @param throwable The error or exception that occurred during message processing.
     * @param message   The XML-message being processed.
     * @param session   The JMS session in which the error occurred.
     */
    public void handleError(Throwable throwable, String message, Session session) {
        log.error(throwable.getMessage(), throwable);

        boolean mustRollback = throwable.getClass() == JMSException.class;

        finishSession(session, message, mustRollback);
    }

    private void finishSession(Session session, String message, boolean mustRollback) {
        try {
            if (mustRollback) {
                log.info("JMS session will be rolled back and message redelivered");
                session.rollback();
            } else {
                log.error("JMS session will be committed. The following message, processed with an error, " +
                        "will not be redelivered:\n{}", message);
                session.commit();
            }
        } catch (JMSException e) {
            log.error("Failed to finalize a JMS session");
            log.error(e.getMessage(), e);
        }
    }
}
