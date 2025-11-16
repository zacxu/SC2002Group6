package service;

import entity.Application;

import java.util.List;

/**
 * StudentApplicationService defines student application methods:
 * apply to internships, view own applications, and accept placement.
 * 
 */



public interface StudentApplicationService {

    Application applyForInternship(String internshipId);

    List<Application> getApplicationsByStudent(String studentId);

    void acceptPlacement(String applicationId);
}


