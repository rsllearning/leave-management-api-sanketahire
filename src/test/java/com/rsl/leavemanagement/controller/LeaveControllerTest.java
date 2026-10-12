package com.rsl.leavemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsl.leavemanagement.dto.request.CreateLeaveRequestDto;
import com.rsl.leavemanagement.dto.request.UpdateLeaveRequestDto;
import com.rsl.leavemanagement.dto.response.LeaveResponseDto;
import com.rsl.leavemanagement.exception.InvalidLeaveDateException;
import com.rsl.leavemanagement.exception.LeaveNotFoundException;
import com.rsl.leavemanagement.model.LeaveStatus;
import com.rsl.leavemanagement.model.LeaveType;
import com.rsl.leavemanagement.service.LeaveService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web-slice tests for LeaveController using MockMvc and @WebMvcTest.
 */
@WebMvcTest(LeaveController.class)
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LeaveService leaveService;

    @Test
    @DisplayName("GET /leaves: should return 200 OK and list of leave requests")
    void getAllLeaves_returns200() throws Exception {
        LeaveResponseDto dto = new LeaveResponseDto(
                1L, 101L, LeaveType.ANNUAL,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(5),
                "Vacation", LeaveStatus.PENDING
        );
        when(leaveService.getAllLeaves()).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/leaves"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].employeeId").value(101))
                .andExpect(jsonPath("$[0].leaveType").value("ANNUAL"));
    }

    @Test
    @DisplayName("GET /leaves/{id}: should return 200 OK when resource exists")
    void getLeaveById_exists_returns200() throws Exception {
        LeaveResponseDto dto = new LeaveResponseDto(
                1L, 101L, LeaveType.SICK,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                "Fever", LeaveStatus.APPROVED
        );
        when(leaveService.getLeaveById(1L)).thenReturn(dto);

        mockMvc.perform(get("/leaves/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /leaves/{id}: should return 404 Not Found when ID does not exist")
    void getLeaveById_notFound_returns404() throws Exception {
        when(leaveService.getLeaveById(999L)).thenThrow(new LeaveNotFoundException(999L));

        mockMvc.perform(get("/leaves/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Leave request with ID 999 not found"));
    }

    @Test
    @DisplayName("POST /leaves: should return 201 Created for valid payload")
    void createLeave_valid_returns201() throws Exception {
        CreateLeaveRequestDto request = new CreateLeaveRequestDto(
                101L, LeaveType.ANNUAL,
                LocalDate.now().plusDays(2), LocalDate.now().plusDays(6),
                "Summer break"
        );

        LeaveResponseDto responseDto = new LeaveResponseDto(
                1L, 101L, LeaveType.ANNUAL,
                request.getStartDate(), request.getEndDate(),
                request.getReason(), LeaveStatus.PENDING
        );

        when(leaveService.createLeave(any(CreateLeaveRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /leaves: should return 400 Bad Request when validation fails")
    void createLeave_validationFails_returns400() throws Exception {
        // Missing employeeId, null leaveType, and blank reason
        CreateLeaveRequestDto invalidRequest = new CreateLeaveRequestDto(
                null, null,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2),
                ""
        );

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.employeeId").exists())
                .andExpect(jsonPath("$.validationErrors.leaveType").exists())
                .andExpect(jsonPath("$.validationErrors.reason").exists());
    }

    @Test
    @DisplayName("POST /leaves: should return 400 Bad Request when date validation fails")
    void createLeave_invalidDateRange_returns400() throws Exception {
        CreateLeaveRequestDto request = new CreateLeaveRequestDto(
                101L, LeaveType.ANNUAL,
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(2),
                "Invalid dates"
        );

        when(leaveService.createLeave(any(CreateLeaveRequestDto.class)))
                .thenThrow(new InvalidLeaveDateException("End date cannot be before start date"));

        mockMvc.perform(post("/leaves")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("End date cannot be before start date"));
    }

    @Test
    @DisplayName("PUT /leaves/{id}: should return 200 OK for valid update")
    void updateLeave_valid_returns200() throws Exception {
        UpdateLeaveRequestDto updateRequest = new UpdateLeaveRequestDto(
                LeaveType.CASUAL,
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(3),
                "Updated personal leave", LeaveStatus.APPROVED
        );

        LeaveResponseDto responseDto = new LeaveResponseDto(
                1L, 101L, LeaveType.CASUAL,
                updateRequest.getStartDate(), updateRequest.getEndDate(),
                updateRequest.getReason(), LeaveStatus.APPROVED
        );

        when(leaveService.updateLeave(eq(1L), any(UpdateLeaveRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/leaves/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("DELETE /leaves/{id}: should return 204 No Content when deleted")
    void deleteLeave_exists_returns204() throws Exception {
        doNothing().when(leaveService).deleteLeave(1L);

        mockMvc.perform(delete("/leaves/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /leaves/{id}: should return 404 Not Found when non-existent")
    void deleteLeave_notFound_returns404() throws Exception {
        doThrow(new LeaveNotFoundException(999L)).when(leaveService).deleteLeave(999L);

        mockMvc.perform(delete("/leaves/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
