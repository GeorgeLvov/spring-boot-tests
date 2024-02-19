package com.example.springboottests.misc.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Utility class for test-related operations.
 *
 * @author Georgii Lvov
 */
public final class TestUtils {

    private TestUtils() {
    }

    /**
     * Load the content of a file as a String.
     *
     * @param fileName the name of the file to load
     * @return the content of the file as a String
     * @throws TestsUtilsException if an I/O error occurs while reading the file
     */
    public static String loadFile(String fileName) {
        try {
            return new String(
                    Objects.requireNonNull(TestUtils.class.getResourceAsStream(fileName)).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        } catch (IOException e) {
            throw new TestsUtilsException(e);
        }
    }

    private static class TestsUtilsException extends RuntimeException {
        private TestsUtilsException(Throwable cause) {
            super(cause);
        }
    }
}
