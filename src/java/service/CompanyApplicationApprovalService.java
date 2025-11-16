package service;

import entity.Application;

import java.util.List;

/**
 * CompanyApplicationApprovalService defines methods for company representatives on applications.
 * 
 */


public interface CompanyApplicationApprovalService {

    List<Application> getApplicationsForRep(String companyRepId);

    void approveApplication(String applicationId);

    void rejectApplication(String applicationId);
}


