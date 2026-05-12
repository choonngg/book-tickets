package com.project.ticket.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "Email is already in use."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Email or password is invalid."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication is required."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access is forbidden."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User was not found."),
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "Concert was not found."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "Seat was not found."),
    TICKET_NOT_FOUND(HttpStatus.NOT_FOUND, "Ticket was not found."),
    SEAT_NOT_AVAILABLE(HttpStatus.CONFLICT, "Seat is not available."),
    SEAT_LOCK_FAILED(HttpStatus.CONFLICT, "Seat lock acquisition failed."),
    IDEMPOTENCY_KEY_REQUIRED(HttpStatus.BAD_REQUEST, "Idempotency-Key header is required."),
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, "Payment failed."),
    ALREADY_CANCELLED(HttpStatus.CONFLICT, "Resource is already cancelled.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String message() {
        return message;
    }
}
