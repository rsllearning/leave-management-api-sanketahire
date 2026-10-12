package com.rsl.leavemanagement.repository;

import com.rsl.leavemanagement.model.LeaveRequest;

import java.util.List;
import java.util.Optional;

/**
 * Data access contract for LeaveRequest persistence.
 * Adheres to the Dependency Inversion Principle.
 */
public interface LeaveRepository {

    /**
     * Persists or updates a LeaveRequest record.
     *
     * @param leaveRequest the entity to save
     * @return the saved entity with generated ID
     */
    LeaveRequest save(LeaveRequest leaveRequest);

    /**
     * Finds a LeaveRequest by its unique identifier.
     *
     * @param id the unique ID
     * @return an Optional containing the entity if found, or empty
     */
    Optional<LeaveRequest> findById(Long id);

    /**
     * Retrieves all LeaveRequest records.
     *
     * @return list of all leave requests
     */
    List<LeaveRequest> findAll();

    /**
     * Checks if a LeaveRequest exists with the given ID.
     *
     * @param id the unique ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Deletes a LeaveRequest by its ID.
     *
     * @param id the unique ID
     * @return true if deleted, false if not found
     */
    boolean deleteById(Long id);

    /**
     * Removes all LeaveRequest records (used for test isolation / reset).
     */
    void deleteAll();
}
