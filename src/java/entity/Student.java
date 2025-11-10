package entity;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private int yearOfStudy;
    private String major;
    private List<String> appliedInternshipIds;
    private String acceptedInternshipId;

    public Student(String userId, String name, String password, int yearOfStudy, String major) {
        super(userId, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
        this.appliedInternshipIds = new ArrayList<>();
        this.acceptedInternshipId = null;
    }

    public int getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(int yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public List<String> getAppliedInternshipIds() {
        return appliedInternshipIds;
    }

    public void addAppliedInternship(String internshipId) {
        if (!appliedInternshipIds.contains(internshipId)) {
            appliedInternshipIds.add(internshipId);
        }
    }

    public void removeAppliedInternship(String internshipId) {
        appliedInternshipIds.remove(internshipId);
    }

    public String getAcceptedInternshipId() {
        return acceptedInternshipId;
    }

    public void setAcceptedInternshipId(String acceptedInternshipId) {
        this.acceptedInternshipId = acceptedInternshipId;
    }

    public boolean hasAcceptedPlacement() {
        return acceptedInternshipId != null;
    }

    public int getApplicationCount() {
        return appliedInternshipIds.size();
    }

    public boolean canApplyForMore() {
        return getApplicationCount() < 3;
    }

    @Override
    public String getUserRole() {
        return "Student";
    }
}

