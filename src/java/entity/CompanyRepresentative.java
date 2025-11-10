package entity;

import java.util.ArrayList;
import java.util.List;

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private boolean approved;
    private List<String> createdInternshipIds;

    public CompanyRepresentative(String userId, String name, String password,
                                 String companyName, String department, String position) {
        super(userId, name, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.approved = false;
        this.createdInternshipIds = new ArrayList<>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<String> getCreatedInternshipIds() {
        return createdInternshipIds;
    }

    public void addCreatedInternship(String internshipId) {
        if (!createdInternshipIds.contains(internshipId)) {
            createdInternshipIds.add(internshipId);
        }
    }

    public void removeCreatedInternship(String internshipId) {
        createdInternshipIds.remove(internshipId);
    }

    public boolean canCreateMoreInternships() {
        return createdInternshipIds.size() < 5;
    }

    @Override
    public String getUserRole() {
        return "CompanyRepresentative";
    }
}

