package com.rsl.leavemanagement.repository.impl;

import com.rsl.leavemanagement.model.LeaveRequest;
import com.rsl.leavemanagement.model.LeaveStatus;
import com.rsl.leavemanagement.model.LeaveType;
import com.rsl.leavemanagement.repository.LeaveRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-safe, in-memory implementation of LeaveRepository.
 * Uses ConcurrentHashMap and AtomicLong to guarantee safe concurrent access
 * across multiple HTTP worker threads without external database dependencies.
 */
@Repository
public class InMemoryLeaveRepository implements LeaveRepository {

    private final ConcurrentHashMap<Long, LeaveRequest> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    @Value("${app.seed-mock-data:false}")
    private boolean seedMockData;

    @PostConstruct
    public void init() {
        if (seedMockData) {
            save(new LeaveRequest(
                    null,
                    101L,
                    LeaveType.ANNUAL,
                    LocalDate.now().plusDays(5),
                    LocalDate.now().plusDays(10),
                    "Annual family vacation",
                    LeaveStatus.PENDING
            ));
            save(new LeaveRequest(
                    null,
                    102L,
                    LeaveType.SICK,
                    LocalDate.now().plusDays(1),
                    LocalDate.now().plusDays(3),
                    "Doctor appointment and recovery",
                    LeaveStatus.APPROVED
            ));
        }
    }

    @Override
    public LeaveRequest save(LeaveRequest leaveRequest) {
        if (leaveRequest.getId() == null) {
            leaveRequest.setId(idSequence.incrementAndGet());
        }
        store.put(leaveRequest.getId(), leaveRequest);
        return leaveRequest;
    }

    @Override
    public Optional<LeaveRequest> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<LeaveRequest> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public void deleteAll() {
        store.clear();
        idSequence.set(0);
    }
}
