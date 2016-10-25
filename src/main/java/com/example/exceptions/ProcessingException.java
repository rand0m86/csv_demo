package com.example.exceptions;

/**
 * Exception type that is thrown if processing of some logic failed due to
 * some reason.
 */
public class ProcessingException extends RuntimeException {

    public ProcessingException(String message) {
        super(message);
    }
}
