package com.rsl.leavemanagement.exception;

/**
 * Thrown when leave date validation fails (e.g., endDate is before startDate).
 */
public class InvalidLeaveDateException extends RuntimeException {

    public InvalidLeaveDateException(String message) {
        super(message);
    }
}
