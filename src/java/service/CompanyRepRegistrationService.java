package service;

import entity.CompanyRepresentative;

import java.util.List;

/**
 * CompanyRepRegistrationService defines methods for company representative registration and approval, 
 * together with listing of pending accounts.
 * 
 * 
 */

 
public interface CompanyRepRegistrationService {

    CompanyRepresentative registerCompanyRep(String email,
                                             String name,
                                             String password,
                                             String companyName,
                                             String department,
                                             String position);

    List<CompanyRepresentative> getPendingCompanyReps();

    void approveCompanyRepresentative(String userId);

    void rejectCompanyRepresentative(String userId);
}


