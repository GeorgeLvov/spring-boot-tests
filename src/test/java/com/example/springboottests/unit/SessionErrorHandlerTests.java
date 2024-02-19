package com.example.springboottests.unit;

import com.example.springboottests.functional.note.SessionErrorHandler;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link SessionErrorHandler}.
 * <p>
 * These tests focus on verifying the behavior of the {@link SessionErrorHandler} class in isolation.
 * The {@code @ExtendWith(MockitoExtension.class)} annotation is used to integrate with the Mockito test framework.
 *
 * @author Georgii Lvov
 */
@ExtendWith(MockitoExtension.class)
class SessionErrorHandlerTests {

    private final SessionErrorHandler sessionErrorHandler = new SessionErrorHandler();

    @Mock
    private Session session;

    /**
     * Test case to verify that when a {@link JMSException} occurs, the session is rolled back.
     *
     */
    @Test
    void testWhenJmsExceptionThenSessionIsRollback() throws Exception {
        sessionErrorHandler.handleError(new JMSException("reason"), "<xml>message</xml>", session);

        verify(session).rollback();
    }

    /**
     * Parameterized test case to verify that when an exception other than {@link JMSException} occurs,
     * the session is committed.
     *
     * @param exception the exception to be tested
     */
    @ParameterizedTest
    @MethodSource
    void testWhenOtherExceptionThenSessionIsCommitted(Exception exception) throws Exception {
        sessionErrorHandler.handleError(exception, "<xml>message</xml>", session);

        verify(session).commit();
    }

    /**
     * Provides a stream of exceptions for parameterized testing.
     *
     * @return a stream of exceptions
     */
    static Stream<Exception> testWhenOtherExceptionThenSessionIsCommitted() {
        return Stream.of(new IllegalArgumentException(), new NullPointerException());
    }
}
