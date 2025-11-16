package service;

import entity.Internship;

import java.util.List;

/**
 * StaffInternshipApprovalService defines actions for career center staff
 * to review and decide on internship postings.
 * 
 * 
 */


public interface StaffInternshipApprovalService {
    
    List<Internship> getPendingInternships();

    void approveInternship(String internshipId);

    void rejectInternship(String internshipId);
}


