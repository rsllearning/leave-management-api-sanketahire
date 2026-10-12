package com.rsl.leavemanagement.exception;

/**
 * Thrown when a requested leave record cannot be found by its ID.
 */
public class LeaveNotFoundException extends RuntimeException {

    public LeaveNotFoundException(Long id) {
        super("Leave request with ID " + id + " not found");
    }

    public LeaveNotFoundException(String message) {
        super(message);
    }
}
