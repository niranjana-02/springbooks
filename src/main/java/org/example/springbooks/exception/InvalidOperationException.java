package org.example.springbooks.exception;

public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String msg) {
        super(msg);
    }
}
