package repository;

import entity.WithdrawalRequest;
import util.FileManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WithdrawalRegistry {
    private static final WithdrawalRegistry INSTANCE = new WithdrawalRegistry();

    private final Map<String, WithdrawalRequest> withdrawalRequests = new HashMap<>();
    private boolean initialized = false;
    private int nextWithdrawalId = 1;

    private WithdrawalRegistry() {
    }

    public static WithdrawalRegistry getInstance() {
        return INSTANCE;
    }

    public synchronized void initialize() throws IOException {
        if (initialized) {
            return;
        }
        withdrawalRequests.clear();
        withdrawalRequests.putAll(FileManager.loadWithdrawalRequests());
        updateNextWithdrawalId();
        initialized = true;
    }

    private void ensureInitialized() {
        if (!initialized) {
            try {
                initialize();
            } catch (IOException e) {
                throw new RuntimeException("Unable to initialise withdrawal registry", e);
            }
        }
    }

    private void updateNextWithdrawalId() {
        nextWithdrawalId = 1;
        for (String key : withdrawalRequests.keySet()) {
            if (key.startsWith("WR")) {
                try {
                    int value = Integer.parseInt(key.substring(2));
                    if (value >= nextWithdrawalId) {
                        nextWithdrawalId = value + 1;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
    }

    public synchronized void save() {
        ensureInitialized();
        try {
            FileManager.saveWithdrawalRequests(withdrawalRequests);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save withdrawal requests", e);
        }
    }

    public String nextId() {
        ensureInitialized();
        return "WR" + String.format("%05d", nextWithdrawalId++);
    }

    public WithdrawalRequest newRequest(String applicationId,
                                        String studentId,
                                        String internshipId,
                                        boolean afterPlacement,
                                        String reason) {
        return new WithdrawalRequest(
            nextId(),
            applicationId,
            studentId,
            internshipId,
            afterPlacement,
            LocalDate.now(),
            reason
        );
    }

    public void addRequest(WithdrawalRequest request) {
        ensureInitialized();
        withdrawalRequests.put(request.getRequestId(), request);
    }

    public WithdrawalRequest getRequestById(String requestId) {
        ensureInitialized();
        return withdrawalRequests.get(requestId);
    }

    public List<WithdrawalRequest> getPendingRequests() {
        ensureInitialized();
        List<WithdrawalRequest> pending = new ArrayList<>();
        for (WithdrawalRequest request : withdrawalRequests.values()) {
            if (request.getStatus() == entity.enums.WithdrawalStatus.Pending) {
                pending.add(request);
            }
        }
        return pending;
    }

    public List<WithdrawalRequest> getRequestsByStudent(String studentId) {
        ensureInitialized();
        List<WithdrawalRequest> list = new ArrayList<>();
        for (WithdrawalRequest request : withdrawalRequests.values()) {
            if (request.getStudentId().equals(studentId)) {
                list.add(request);
            }
        }
        return list;
    }

    public Collection<WithdrawalRequest> getAllRequests() {
        ensureInitialized();
        return Collections.unmodifiableCollection(withdrawalRequests.values());
    }
}


