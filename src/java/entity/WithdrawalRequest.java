package entity;

import entity.enums.WithdrawalStatus;

import java.time.LocalDate;

public class WithdrawalRequest {
    private final String requestId;
    private final String applicationId;
    private final String studentId;
    private final String internshipId;
    private final boolean afterPlacement;
    private final LocalDate requestDate;
    private final String reason;
    private WithdrawalStatus status;

    public WithdrawalRequest(String requestId,
                             String applicationId,
                             String studentId,
                             String internshipId,
                             boolean afterPlacement,
                             LocalDate requestDate,
                             String reason) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.afterPlacement = afterPlacement;
        this.requestDate = requestDate;
        this.reason = reason;
        this.status = WithdrawalStatus.Pending;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public boolean isAfterPlacement() {
        return afterPlacement;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public String getReason() {
        return reason;
    }
}
