package com.example.exceptions;

/**
 * Exception type that is thrown if attempt to parse the file failed.
 */
public class ParseException extends RuntimeException {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}
