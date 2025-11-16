package entity;

import entity.enums.ApplicationStatus;

import java.time.LocalDate;

public class Application {
    private final String applicationId;
    private final String studentId;
    private final String internshipId;
    private ApplicationStatus status;
    private final LocalDate applicationDate;

    public Application(String applicationId, String studentId, String internshipId, LocalDate applicationDate) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.internshipId = internshipId;
        this.applicationDate = applicationDate;
        this.status = ApplicationStatus.Pending;
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

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }
}
