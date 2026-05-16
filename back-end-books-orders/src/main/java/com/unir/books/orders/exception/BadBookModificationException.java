package com.unir.books.orders.exception;

public class BadBookModificationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BadBookModificationException(String message) {
        super(message);
    }
    public BadBookModificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
