package com.rsl.leavemanagement.service;

import com.rsl.leavemanagement.dto.request.CreateLeaveRequestDto;
import com.rsl.leavemanagement.dto.request.UpdateLeaveRequestDto;
import com.rsl.leavemanagement.dto.response.LeaveResponseDto;

import java.util.List;

/**
 * Service contract for Leave Management business operations.
 */
public interface LeaveService {

    /**
     * Submits and creates a new leave request.
     *
     * @param request the creation payload
     * @return the created leave representation
     */
    LeaveResponseDto createLeave(CreateLeaveRequestDto request);

    /**
     * Retrieves a leave request by its unique identifier.
     *
     * @param id the unique ID
     * @return the leave representation
     */
    LeaveResponseDto getLeaveById(Long id);

    /**
     * Retrieves all leave requests in the system.
     *
     * @return list of leave representations
     */
    List<LeaveResponseDto> getAllLeaves();

    /**
     * Updates an existing leave request.
     *
     * @param id      the unique ID
     * @param request the update payload
     * @return the updated leave representation
     */
    LeaveResponseDto updateLeave(Long id, UpdateLeaveRequestDto request);

    /**
     * Deletes a leave request by its ID.
     *
     * @param id the unique ID
     */
    void deleteLeave(Long id);
}
