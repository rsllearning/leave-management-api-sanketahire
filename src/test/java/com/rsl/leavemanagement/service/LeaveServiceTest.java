package com.rsl.leavemanagement.service;

import com.rsl.leavemanagement.dto.request.CreateLeaveRequestDto;
import com.rsl.leavemanagement.dto.request.UpdateLeaveRequestDto;
import com.rsl.leavemanagement.dto.response.LeaveResponseDto;
import com.rsl.leavemanagement.exception.InvalidLeaveDateException;
import com.rsl.leavemanagement.exception.LeaveNotFoundException;
import com.rsl.leavemanagement.model.LeaveRequest;
import com.rsl.leavemanagement.model.LeaveStatus;
import com.rsl.leavemanagement.model.LeaveType;
import com.rsl.leavemanagement.repository.LeaveRepository;
import com.rsl.leavemanagement.service.impl.LeaveServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Isolated unit tests for LeaveServiceImpl using Mockito.
 */
@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRepository leaveRepository;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    @Test
    @DisplayName("createLeave: should save entity with PENDING status and return DTO")
    void createLeave_success() {
        CreateLeaveRequestDto request = new CreateLeaveRequestDto(
                101L,
                LeaveType.ANNUAL,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                "Family vacation"
        );

        LeaveRequest savedEntity = new LeaveRequest(
                1L,
                request.getEmployeeId(),
                request.getLeaveType(),
                request.getStartDate(),
                request.getEndDate(),
                request.getReason(),
                LeaveStatus.PENDING
        );

        when(leaveRepository.save(any(LeaveRequest.class))).thenReturn(savedEntity);

        LeaveResponseDto result = leaveService.createLeave(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(101L, result.getEmployeeId());
        assertEquals(LeaveType.ANNUAL, result.getLeaveType());
        assertEquals(LeaveStatus.PENDING, result.getStatus());
        verify(leaveRepository).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("createLeave: should throw InvalidLeaveDateException when endDate is before startDate")
    void createLeave_invalidDateRange_throwsException() {
        CreateLeaveRequestDto request = new CreateLeaveRequestDto(
                101L,
                LeaveType.ANNUAL,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2), // End date is before start date
                "Invalid dates"
        );

        assertThrows(InvalidLeaveDateException.class, () -> leaveService.createLeave(request));
        verify(leaveRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("getLeaveById: should return LeaveResponseDto when ID exists")
    void getLeaveById_success() {
        LeaveRequest entity = new LeaveRequest(
                1L, 101L, LeaveType.SICK,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                "Flu recovery", LeaveStatus.PENDING
        );

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(entity));

        LeaveResponseDto result = leaveService.getLeaveById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(LeaveType.SICK, result.getLeaveType());
    }

    @Test
    @DisplayName("getLeaveById: should throw LeaveNotFoundException when ID does not exist")
    void getLeaveById_notFound_throwsException() {
        when(leaveRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(LeaveNotFoundException.class, () -> leaveService.getLeaveById(999L));
    }

    @Test
    @DisplayName("getAllLeaves: should return list of LeaveResponseDto")
    void getAllLeaves_success() {
        LeaveRequest leave1 = new LeaveRequest(1L, 101L, LeaveType.CASUAL,
                LocalDate.now(), LocalDate.now().plusDays(1), "Personal", LeaveStatus.APPROVED);
        LeaveRequest leave2 = new LeaveRequest(2L, 102L, LeaveType.SICK,
                LocalDate.now(), LocalDate.now().plusDays(2), "Fever", LeaveStatus.PENDING);

        when(leaveRepository.findAll()).thenReturn(Arrays.asList(leave1, leave2));

        List<LeaveResponseDto> result = leaveService.getAllLeaves();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("updateLeave: should update fields and return updated LeaveResponseDto")
    void updateLeave_success() {
        LeaveRequest existing = new LeaveRequest(1L, 101L, LeaveType.CASUAL,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                "Personal", LeaveStatus.PENDING);

        UpdateLeaveRequestDto updateRequest = new UpdateLeaveRequestDto(
                LeaveType.ANNUAL,
                LocalDate.now().plusDays(2),
                LocalDate.now().plusDays(4),
                "Updated vacation plan",
                LeaveStatus.APPROVED
        );

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(leaveRepository.save(any(LeaveRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LeaveResponseDto result = leaveService.updateLeave(1L, updateRequest);

        assertNotNull(result);
        assertEquals(LeaveType.ANNUAL, result.getLeaveType());
        assertEquals(LeaveStatus.APPROVED, result.getStatus());
        assertEquals("Updated vacation plan", result.getReason());
        verify(leaveRepository).save(existing);
    }

    @Test
    @DisplayName("updateLeave: should throw LeaveNotFoundException when updating non-existent ID")
    void updateLeave_notFound_throwsException() {
        UpdateLeaveRequestDto updateRequest = new UpdateLeaveRequestDto(
                LeaveType.ANNUAL, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                "Reason", LeaveStatus.APPROVED
        );

        when(leaveRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(LeaveNotFoundException.class, () -> leaveService.updateLeave(999L, updateRequest));
        verify(leaveRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("updateLeave: should throw InvalidLeaveDateException when updated dates are invalid")
    void updateLeave_invalidDateRange_throwsException() {
        LeaveRequest existing = new LeaveRequest(1L, 101L, LeaveType.CASUAL,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                "Personal", LeaveStatus.PENDING);

        UpdateLeaveRequestDto updateRequest = new UpdateLeaveRequestDto(
                LeaveType.ANNUAL,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusDays(2), // Invalid: endDate before startDate
                "Reason",
                LeaveStatus.PENDING
        );

        when(leaveRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(InvalidLeaveDateException.class, () -> leaveService.updateLeave(1L, updateRequest));
        verify(leaveRepository, never()).save(any(LeaveRequest.class));
    }

    @Test
    @DisplayName("deleteLeave: should call repository deleteById when ID exists")
    void deleteLeave_success() {
        when(leaveRepository.existsById(1L)).thenReturn(true);

        leaveService.deleteLeave(1L);

        verify(leaveRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteLeave: should throw LeaveNotFoundException when ID does not exist")
    void deleteLeave_notFound_throwsException() {
        when(leaveRepository.existsById(999L)).thenReturn(false);

        assertThrows(LeaveNotFoundException.class, () -> leaveService.deleteLeave(999L));
        verify(leaveRepository, never()).deleteById(999L);
    }
}
