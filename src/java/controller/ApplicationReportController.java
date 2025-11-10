package controller;

import entity.Application;

import java.util.List;

/**
 * Provides application-centric reporting utilities.
 * 
 * exposing list retrievals from ApplicationController (by internship, student, or application status)
 */



public class ApplicationReportController {
    private final ApplicationController applicationController = ApplicationController.getInstance();


    public List<Application> getApplicationsForInternship(String internshipId) {
        return applicationController.getApplicationsByInternship(internshipId);
    }


    public List<Application> getApplicationsByStudent(String studentId) {
        return applicationController.getApplicationsByStudent(studentId);
    }

    
    public List<Application> getApplicationByStatus(Application.ApplicationStatus status) {
        return applicationController.getApplicationsByStatus(status);
    }



}


