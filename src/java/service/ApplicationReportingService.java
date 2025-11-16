package service;

import entity.Application;
import entity.enums.ApplicationStatus;

import java.util.List;

/**
 *  Interface class defines views for applications by internship, student, or status.
 * 
 */



public interface ApplicationReportingService {


    List<Application> getApplicationsForInternship(String internshipId);

    List<Application> getApplicationsByStudent(String studentId);

    List<Application> getApplicationsByStatus(ApplicationStatus status);
}


