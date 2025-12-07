package org.example_database.exception;

/** Thrown for any data access related exceptions */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
    }
}
