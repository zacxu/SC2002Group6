package entity;

public class WithdrawalRequest {
    private String requestId;
    private String applicationId;
    private String studentId;
    private String internshipId;
    private WithdrawalStatus status;
    private boolean isAfterPlacement;

    public enum WithdrawalStatus {
        Pending, Approved, Rejected
    }

    public WithdrawalRequest(String requestId, String applicationId,
                             String studentId, String internshipId,
                             boolean isAfterPlacement) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.status = WithdrawalStatus.Pending;
        this.isAfterPlacement = isAfterPlacement;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getInternshipId() {
        return internshipId;
    }

    public void setInternshipId(String internshipId) {
        this.internshipId = internshipId;
    }

    public WithdrawalStatus getStatus() {
        return status;
    }

    public void setStatus(WithdrawalStatus status) {
        this.status = status;
    }

    public boolean isAfterPlacement() {
        return isAfterPlacement;
    }

    public void setAfterPlacement(boolean isAfterPlacement) {
        this.isAfterPlacement = isAfterPlacement;
    }
}

