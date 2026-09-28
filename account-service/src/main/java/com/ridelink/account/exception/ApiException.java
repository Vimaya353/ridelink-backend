package com.ridelink.account.exception;

import org.springframework.http.HttpStatus;

// Carries an HTTP status along with the error message for API failures.
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    // Lets the exception handler choose the response status.
    public HttpStatus getStatus() {
        return status;
    }
}