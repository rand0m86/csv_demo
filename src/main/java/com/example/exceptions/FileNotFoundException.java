package com.example.exceptions;

/**
 * Exception type that is thrown if attempt to open the file failed.
 */
public class FileNotFoundException extends RuntimeException {

    public FileNotFoundException(String message) {
        super(message);
    }

}
