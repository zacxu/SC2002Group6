package controller;

import model.*;
import util.Validator;
import java.util.List;

public class ApplicationController {
    private SystemController systemController;
    
    public ApplicationController() {
        this.systemController = SystemController.getInstance();
    }
    


    



    public Application applyForInternship(String internshipId) {
        User currentUser = systemController.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can apply for internships");
        }
        
        Student student = (Student) currentUser;
        InternshipOpportunity internship = systemController.getInternship(internshipId);
        
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        
        // Check if already applied
        if (student.getAppliedInternshipIds().contains(internshipId)) {
            throw new IllegalStateException("You have already applied for this internship");
        }
        
        // Check application limit
        if (!student.canApplyForMore()) {
            throw new IllegalStateException("Maximum number of applications (3) reached");
        }
        
        // Check eligibility
        if (!Validator.isEligibleForInternship(student, internship)) {
            throw new IllegalStateException("You are not eligible for this internship based on your year of study and/or major");
        }
        
        // Check if internship is open for applications
        if (!internship.isOpenForApplication()) {
            throw new IllegalStateException("This internship is not open for applications");
        }
        
        String applicationId = systemController.generateApplicationId();
        Application application = new Application(applicationId, student.getUserId(), internshipId);
        systemController.addApplication(application);
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving application: " + e.getMessage());
        }
        
        return application;
    }
    







    public void approveApplication(String applicationId) {
        Application application = systemController.getApplication(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        
        User currentUser = systemController.getCurrentUser();
        InternshipOpportunity internship = systemController.getInternship(application.getInternshipId());
        
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        
        // Check permissions - only company rep who created the internship can approve
        if (currentUser instanceof CompanyRepresentative) {
            if (!internship.getCompanyRepId().equals(currentUser.getUserId())) {
                throw new IllegalStateException("You can only approve applications for your own internships");
            }
        }
        
        application.setStatus(Application.ApplicationStatus.Successful);
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }












    
    public void rejectApplication(String applicationId) {
        Application application = systemController.getApplication(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        
        User currentUser = systemController.getCurrentUser();
        InternshipOpportunity internship = systemController.getInternship(application.getInternshipId());
        
        if (internship == null) {
            throw new IllegalArgumentException("Internship not found");
        }
        
        // Check permissions
        if (currentUser instanceof CompanyRepresentative) {
            if (!internship.getCompanyRepId().equals(currentUser.getUserId())) {
                throw new IllegalStateException("You can only reject applications for your own internships");
            }
        }
        
        application.setStatus(Application.ApplicationStatus.Unsuccessful);
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }













    
    public void acceptPlacement(String applicationId) {
        Application application = systemController.getApplication(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        
        User currentUser = systemController.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can accept placements");
        }
        
        Student student = (Student) currentUser;
        
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only accept your own placements");
        }
        
        if (application.getStatus() != Application.ApplicationStatus.Successful) {
            throw new IllegalStateException("Can only accept successful applications");
        }
        
        if (student.hasAcceptedPlacement()) {
            throw new IllegalStateException("You have already accepted a placement");
        }
        
        // Accept placement
        student.setAcceptedInternshipId(application.getInternshipId());
        
        // Withdraw all other applications
        List<Application> otherApplications = systemController.getApplicationsByStudent(student.getUserId());
        for (Application app : otherApplications) {
            if (!app.getApplicationId().equals(applicationId)) {
                // Remove from student's applied list
                student.removeAppliedInternship(app.getInternshipId());
                // Remove application
                systemController.removeApplication(app.getApplicationId());
            }
        }
        
        // Update internship filled slots
        InternshipOpportunity internship = systemController.getInternship(application.getInternshipId());
        if (internship != null) {
            internship.setFilledSlots(internship.getFilledSlots() + 1);
        }
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }









    
    public WithdrawalRequest requestWithdrawal(String applicationId) {
        Application application = systemController.getApplication(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("Application not found");
        }
        
        User currentUser = systemController.getCurrentUser();
        if (!(currentUser instanceof Student)) {
            throw new IllegalStateException("Only students can request withdrawal");
        }
        
        Student student = (Student) currentUser;
        
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only withdraw your own applications");
        }
        
        // Check if already has a pending withdrawal request
        for (WithdrawalRequest request : systemController.getWithdrawalRequests().values()) {
            if (request.getApplicationId().equals(applicationId) && 
                request.getStatus() == WithdrawalRequest.WithdrawalStatus.Pending) {
                throw new IllegalStateException("You already have a pending withdrawal request for this application");
            }
        }
        
        boolean isAfterPlacement = student.hasAcceptedPlacement() && 
                                   student.getAcceptedInternshipId().equals(application.getInternshipId());
        
        String requestId = systemController.generateWithdrawalRequestId();
        WithdrawalRequest request = new WithdrawalRequest(
            requestId, applicationId, student.getUserId(), 
            application.getInternshipId(), isAfterPlacement
        );
        
        systemController.addWithdrawalRequest(request);
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
        
        return request;
    }
    










    public void approveWithdrawal(String requestId) {
        WithdrawalRequest request = systemController.getWithdrawalRequest(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Withdrawal request not found");
        }
        
        request.setStatus(WithdrawalRequest.WithdrawalStatus.Approved);
        
        // Remove application
        Application application = systemController.getApplication(request.getApplicationId());
        if (application != null) {
            User user = systemController.getUser(request.getStudentId());
            if (user instanceof Student) {
                Student student = (Student) user;
                student.removeAppliedInternship(request.getInternshipId());
                
                // If was accepted placement, clear it
                if (student.hasAcceptedPlacement() && 
                    student.getAcceptedInternshipId().equals(request.getInternshipId())) {
                    student.setAcceptedInternshipId(null);
                }
            }
            
            // If application was successful, decrease filled slots
            if (application.getStatus() == Application.ApplicationStatus.Successful) {
                InternshipOpportunity internship = systemController.getInternship(request.getInternshipId());
                if (internship != null && internship.getFilledSlots() > 0) {
                    internship.setFilledSlots(internship.getFilledSlots() - 1);
                    // If status was Filled, change back to Approved if slots available
                    if (internship.getStatus() == InternshipOpportunity.InternshipStatus.Filled &&
                        internship.getAvailableSlots() > 0) {
                        internship.setStatus(InternshipOpportunity.InternshipStatus.Approved);
                    }
                }
            }
            
            systemController.removeApplication(request.getApplicationId());
        }
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }
    












    public void rejectWithdrawal(String requestId) {
        WithdrawalRequest request = systemController.getWithdrawalRequest(requestId);
        if (request == null) {
            throw new IllegalArgumentException("Withdrawal request not found");
        }
        
        request.setStatus(WithdrawalRequest.WithdrawalStatus.Rejected);
        
        try {
            systemController.saveAll();
        } catch (Exception e) {
            System.err.println("Error saving: " + e.getMessage());
        }
    }
}

