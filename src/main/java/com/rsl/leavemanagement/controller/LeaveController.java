package com.rsl.leavemanagement.controller;

import com.rsl.leavemanagement.dto.request.CreateLeaveRequestDto;
import com.rsl.leavemanagement.dto.request.UpdateLeaveRequestDto;
import com.rsl.leavemanagement.dto.response.ErrorResponse;
import com.rsl.leavemanagement.dto.response.LeaveResponseDto;
import com.rsl.leavemanagement.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for managing employee leave requests.
 * Uses constructor injection to adhere to Spring Boot best practices.
 */
@RestController
@RequestMapping("/leaves")
@Tag(name = "Leave Management", description = "Endpoints for managing employee leave requests")
public class LeaveController {

    private final LeaveService leaveService;

    /**
     * Constructor injection ensures explicit dependency wiring.
     */
    public LeaveController(LeaveService leaveService) {
        this.leaveService = leaveService;
    }

    /**
     * GET /leaves - Retrieve all leave requests.
     */
    @GetMapping
    @Operation(summary = "Retrieve all leave requests", description = "Returns a complete list of all submitted leave requests.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved leave requests",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = LeaveResponseDto.class))))
    public ResponseEntity<List<LeaveResponseDto>> getAllLeaves() {
        List<LeaveResponseDto> leaves = leaveService.getAllLeaves();
        return ResponseEntity.ok(leaves);
    }

    /**
     * GET /leaves/{id} - Retrieve a specific leave request by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a leave request by ID", description = "Returns details of the leave request matching the provided identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request found",
                    content = @Content(schema = @Schema(implementation = LeaveResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Leave request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LeaveResponseDto> getLeaveById(
            @Parameter(description = "Unique ID of the leave request", required = true)
            @PathVariable Long id) {
        LeaveResponseDto leave = leaveService.getLeaveById(id);
        return ResponseEntity.ok(leave);
    }

    /**
     * POST /leaves - Submit a new leave request.
     */
    @PostMapping
    @Operation(summary = "Create a new leave request", description = "Validates and records a new employee leave request in PENDING state.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Leave request created successfully",
                    content = @Content(schema = @Schema(implementation = LeaveResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failure or invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LeaveResponseDto> createLeave(
            @Valid @RequestBody CreateLeaveRequestDto request) {
        LeaveResponseDto created = leaveService.createLeave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /leaves/{id} - Update an existing leave request.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing leave request", description = "Updates details and approval status of an existing leave request.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Leave request updated successfully",
                    content = @Content(schema = @Schema(implementation = LeaveResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failure or invalid date range",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Leave request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LeaveResponseDto> updateLeave(
            @Parameter(description = "Unique ID of the leave request to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateLeaveRequestDto request) {
        LeaveResponseDto updated = leaveService.updateLeave(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /leaves/{id} - Delete a leave request.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a leave request", description = "Deletes the leave request record matching the provided ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Leave request deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Leave request not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteLeave(
            @Parameter(description = "Unique ID of the leave request to delete", required = true)
            @PathVariable Long id) {
        leaveService.deleteLeave(id);
        return ResponseEntity.noContent().build();
    }
}
