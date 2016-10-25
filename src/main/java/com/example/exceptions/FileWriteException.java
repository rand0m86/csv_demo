package com.example.exceptions;

/**
 * Exception type that is thrown if attempt to write the file failed.
 */
public class FileWriteException extends RuntimeException {

    public FileWriteException(Throwable cause) {
        super(cause);
    }
}
