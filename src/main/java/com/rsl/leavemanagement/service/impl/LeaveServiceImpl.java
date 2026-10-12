package com.rsl.leavemanagement.service.impl;

import com.rsl.leavemanagement.dto.request.CreateLeaveRequestDto;
import com.rsl.leavemanagement.dto.request.UpdateLeaveRequestDto;
import com.rsl.leavemanagement.dto.response.LeaveResponseDto;
import com.rsl.leavemanagement.exception.InvalidLeaveDateException;
import com.rsl.leavemanagement.exception.LeaveNotFoundException;
import com.rsl.leavemanagement.model.LeaveRequest;
import com.rsl.leavemanagement.model.LeaveStatus;
import com.rsl.leavemanagement.repository.LeaveRepository;
import com.rsl.leavemanagement.service.LeaveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Leave Management operations.
 * Enforces business rules, date validity, and state management.
 */
@Service
public class LeaveServiceImpl implements LeaveService {

    private static final Logger log = LoggerFactory.getLogger(LeaveServiceImpl.class);

    private final LeaveRepository leaveRepository;

    /**
     * Constructor injection ensures explicit collaborators and immutability.
     */
    public LeaveServiceImpl(LeaveRepository leaveRepository) {
        this.leaveRepository = leaveRepository;
    }

    @Override
    public LeaveResponseDto createLeave(CreateLeaveRequestDto request) {
        log.info("Creating leave request for employeeId: {}", request.getEmployeeId());

        validateDateRange(request.getStartDate(), request.getEndDate());

        LeaveRequest entity = new LeaveRequest();
        entity.setEmployeeId(request.getEmployeeId());
        entity.setLeaveType(request.getLeaveType());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        entity.setReason(request.getReason());
        entity.setStatus(LeaveStatus.PENDING); // Explicitly enforce initial PENDING status

        LeaveRequest saved = leaveRepository.save(entity);
        log.info("Successfully created leave request with ID: {}", saved.getId());
        return LeaveResponseDto.fromEntity(saved);
    }

    @Override
    public LeaveResponseDto getLeaveById(Long id) {
        log.debug("Fetching leave request with ID: {}", id);
        LeaveRequest entity = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException(id));
        return LeaveResponseDto.fromEntity(entity);
    }

    @Override
    public List<LeaveResponseDto> getAllLeaves() {
        log.debug("Fetching all leave requests");
        return leaveRepository.findAll().stream()
                .map(LeaveResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponseDto updateLeave(Long id, UpdateLeaveRequestDto request) {
        log.info("Updating leave request with ID: {}", id);

        LeaveRequest existing = leaveRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException(id));

        validateDateRange(request.getStartDate(), request.getEndDate());

        existing.setLeaveType(request.getLeaveType());
        existing.setStartDate(request.getStartDate());
        existing.setEndDate(request.getEndDate());
        existing.setReason(request.getReason());
        existing.setStatus(request.getStatus());

        LeaveRequest updated = leaveRepository.save(existing);
        log.info("Successfully updated leave request with ID: {}", updated.getId());
        return LeaveResponseDto.fromEntity(updated);
    }

    @Override
    public void deleteLeave(Long id) {
        log.info("Deleting leave request with ID: {}", id);
        if (!leaveRepository.existsById(id)) {
            throw new LeaveNotFoundException(id);
        }
        leaveRepository.deleteById(id);
        log.info("Successfully deleted leave request with ID: {}", id);
    }

    /**
     * Cross-field business rule validation.
     * Ensures end date is not prior to start date.
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidLeaveDateException(
                    "End date (" + endDate + ") cannot be before start date (" + startDate + ")"
            );
        }
    }
}
